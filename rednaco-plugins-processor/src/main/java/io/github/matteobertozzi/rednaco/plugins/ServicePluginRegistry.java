/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.github.matteobertozzi.rednaco.plugins;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.ServiceLoader;
import java.util.Set;

import io.github.matteobertozzi.easerinsights.logging.Logger;
import io.github.matteobertozzi.rednaco.strings.HumansTableView;
import io.github.matteobertozzi.rednaco.strings.HumansUtil;
import io.github.matteobertozzi.rednaco.util.BuildInfo;
import io.github.matteobertozzi.rednaco.util.function.FailableConsumer;

public final class ServicePluginRegistry {
  public static final ServicePluginRegistry INSTANCE = new ServicePluginRegistry();

  private final HashMap<String, ServicePlugin> loadedPlugins = new HashMap<>();

  private ServicePluginRegistry() {
    // no-op
  }

  public Set<String> getServiceNames() {
    return loadedPlugins.keySet();
  }

  public Collection<ServicePlugin> getPlugins() {
    return loadedPlugins.values();
  }

  public ServicePlugin getPlugin(final String name) {
    return loadedPlugins.get(name);
  }

  // ===============================================================================================
  //  Scan/Load/Destroy plugin related
  // ===============================================================================================
  public ServiceLoader<ServicePlugin> scanPlugins() {
    return ServiceLoader.load(ServicePlugin.class);
  }

  public boolean load(final ServicePlugin plugin) throws Exception {
    Logger.info("loading plugin: {}", plugin.serviceName());

    try {
      plugin.initialize();
    } catch (final Throwable e) {
      Logger.error(e, "unable to initialize {}", plugin.serviceName());
      throw e;
    }

    final BuildInfo buildInfo = plugin.buildInfo();
    if (buildInfo != null && !buildInfo.isValid()) {
      Logger.error("MISSING BUILD INFO FOR PLUGIN: {} {} (PLUGIN NOT LOADED)", plugin.serviceName(), buildInfo);
      plugin.destroy();
      return false;
    }

    addPlugin(plugin);
    return true;
  }

  public void addPlugin(final ServicePlugin plugin) {
    loadedPlugins.put(plugin.serviceName(), plugin);
  }

  public void destroyLoadedPlugins() {
    for (final ServicePlugin plugin: loadedPlugins.values()) {
      plugin.destroy();
    }
    loadedPlugins.clear();
  }

  // ===============================================================================================
  //  Loading plugin related
  // ===============================================================================================
  private record PluginServiceInitTime(String name, long loadTime) {}
  public void loadPluginServices(final Set<String> modules, final FailableConsumer<ServicePlugin> consumer) throws Exception {
    final long startTime = System.nanoTime();

    final ArrayList<PluginServiceInitTime> pluginInitTimes = new ArrayList<>();

    Logger.info("scanning for plugins. configured modules: {}", modules);
    for (final ServicePlugin plugin: scanPlugins()) {
      if (!modules.contains(plugin.serviceName())) {
        Logger.info("plugin available, but not configured to be loaded: {}", plugin.serviceName());
        continue;
      }

      Logger.info("loading feature: {}", plugin.serviceName());
      final long pluginInitStartTime = System.nanoTime();
      if (!load(plugin)) {
        continue;
      }

      consumer.accept(plugin);

      final long elapsed = System.nanoTime() - pluginInitStartTime;
      pluginInitTimes.add(new PluginServiceInitTime(plugin.serviceName(), elapsed));
      Logger.info("plugin '{}' init took {}", plugin.serviceName(), HumansUtil.humanTimeNanos(elapsed));
    }

    Collections.sort(pluginInitTimes, (a, b) -> Long.compare(b.loadTime(), a.loadTime()));
    final HumansTableView tableView = new HumansTableView();
    tableView.addColumns("Plugin Name", "Load Time");
    for (final PluginServiceInitTime entry: pluginInitTimes) {
      tableView.addRow(entry.name(), HumansUtil.humanTimeNanos(entry.loadTime()));
    }
    Logger.info("plugin services init took {}", HumansUtil.humanTimeSince(startTime));
  }
}
