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

package io.github.matteobertozzi.rednaco.dispatcher.session;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import io.github.matteobertozzi.rednaco.collections.arrays.ArrayUtil;
import io.github.matteobertozzi.rednaco.collections.maps.MapUtil;
import io.github.matteobertozzi.rednaco.data.json.JsonArray;
import io.github.matteobertozzi.rednaco.data.json.JsonElement;
import io.github.matteobertozzi.rednaco.data.json.JsonNull;
import io.github.matteobertozzi.rednaco.data.json.JsonObject;
import io.github.matteobertozzi.rednaco.data.json.JsonUtil;
import io.github.matteobertozzi.rednaco.strings.StringFormat;

public record AuthSessionPermissions(Map<String, String[]> moduleRoles) {
  public static final AuthSessionPermissions EMPTY_PERMISSIONS = new AuthSessionPermissions(Collections.emptyMap());

  public boolean isEmpty() {
    return MapUtil.isEmpty(moduleRoles);
  }

  public boolean isNotEmpty() {
    return MapUtil.isNotEmpty(moduleRoles);
  }

  public Set<String> modules() {
    return moduleRoles.keySet();
  }

  public String[] moduleRoles(final String module) {
    return moduleRoles.get(module);
  }

  public boolean hasRole(final String module, final String roleToCheck) {
    final String[] roles = moduleRoles.get(module);
    return roles != null && Arrays.binarySearch(roles, roleToCheck) >= 0;
  }

  public boolean hasAllRoles(final String module, final String[] rolesToCheck) {
    final String[] roles = moduleRoles.get(module);
    if (ArrayUtil.isEmpty(roles)) return false;

    int found = 0;
    for (int i = 0; i < rolesToCheck.length; ++i) {
      if (Arrays.binarySearch(roles, rolesToCheck[i]) >= 0) {
        found++;
      }
    }
    return found == rolesToCheck.length;
  }

  public boolean hasOneOfRoles(final String module, final String[] rolesToCheck) {
    final String[] roles = moduleRoles.get(module);
    if (ArrayUtil.isEmpty(roles)) return false;

    for (int i = 0; i < rolesToCheck.length; ++i) {
      if (Arrays.binarySearch(roles, rolesToCheck[i]) >= 0) {
        return true;
      }
    }
    return false;
  }

  @Override
  public boolean equals(final Object obj) {
    if (obj == this) return true;
    if (!(obj instanceof final AuthSessionPermissions other)) {
      return false;
    }
    if (moduleRoles().size() != other.moduleRoles().size()) {
      return false;
    }
    for (final String module: moduleRoles().keySet()) {
      if (!Arrays.equals(moduleRoles(module), other.moduleRoles(module))) {
        return false;
      }
    }
    return true;
  }

  public static AuthSessionPermissions fromJson(final JsonElement json) {
    if (json == null || !json.isJsonObject()) {
      return AuthSessionPermissions.EMPTY_PERMISSIONS;
    }

    final JsonObject jsonObj = json.getAsJsonObject();
    if (jsonObj.isEmpty()) return AuthSessionPermissions.EMPTY_PERMISSIONS;

    final HashMap<String, String[]> moduleRoles = HashMap.newHashMap(jsonObj.size());
    for (final Map.Entry<String, JsonElement> entry: jsonObj.entrySet()) {
      final JsonArray jsonArray = entry.getValue().getAsJsonArray();
      final String[] roles = new String[jsonArray.size()];
      for (int i = 0; i < roles.length; ++i) {
        roles[i] = jsonArray.get(i).getAsString();
      }
      Arrays.sort(roles);
      moduleRoles.put(entry.getKey(), roles);
    }
    return new AuthSessionPermissions(Map.copyOf(moduleRoles));
  }

  public static AuthSessionPermissions fromMap(final Map<String, Set<String>> map) {
    if (MapUtil.isEmpty(map)) return AuthSessionPermissions.EMPTY_PERMISSIONS;

    final HashMap<String, String[]> moduleRoles = HashMap.newHashMap(map.size());
    for (final Map.Entry<String, Set<String>> entry: map.entrySet()) {
      final String[] roles = entry.getValue().toArray(new String[0]);
      if (ArrayUtil.isEmpty(roles)) continue;

      Arrays.sort(roles);
      moduleRoles.put(entry.getKey(), roles);
    }
    return new AuthSessionPermissions(Map.copyOf(moduleRoles));
  }

  public JsonElement toJson() {
    if (moduleRoles == null) return JsonNull.INSTANCE;

    return JsonUtil.toJsonTree(moduleRoles);
  }

  @Override
  public String toString() {
    return "RestPermissions [modules=" + moduleRoles.keySet() + "]";
  }

  public static final class Builder {
    private final Map<String, String[]> moduleRoles = new HashMap<>();

    public void setRoles(final String module, final String... roles) {
      Arrays.sort(roles);
      moduleRoles.put(module, roles);
    }

    public void setRoles(final String module, final Set<String> roles) {
      setRoles(module, roles.toArray(new String[0]));
    }

    public void addRoles(final String module, final String[] roles) {
      if (ArrayUtil.isEmpty(roles)) return;

      final String[] currentRoles = moduleRoles.get(module);
      if (ArrayUtil.isNotEmpty(currentRoles)) {
        final HashSet<String> newRoles = HashSet.newHashSet(currentRoles.length + roles.length);
        for (int r = 0; r < currentRoles.length; ++r) newRoles.add(currentRoles[r]);
        for (int r = 0; r < roles.length; ++r) newRoles.add(roles[r]);
        setRoles(module, newRoles);
      } else {
        setRoles(module, roles);
      }
    }

    public AuthSessionPermissions build() {
      if (moduleRoles.isEmpty()) {
        return AuthSessionPermissions.EMPTY_PERMISSIONS;
      }
      return new AuthSessionPermissions(moduleRoles);
    }

    @Override
    public String toString() {
      return "AuthSessionPermissions [moduleRoles=" + StringFormat.valueOf(moduleRoles) + "]";
    }
  }
}
