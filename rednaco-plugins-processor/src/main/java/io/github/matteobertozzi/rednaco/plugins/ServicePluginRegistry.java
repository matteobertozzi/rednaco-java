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

import java.util.Collection;
import java.util.HashMap;
import java.util.ServiceLoader;
import java.util.Set;

import io.github.matteobertozzi.easerinsights.logging.Logger;
import io.github.matteobertozzi.rednaco.util.BuildInfo;

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
}
