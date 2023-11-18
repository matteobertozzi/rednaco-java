#!/usr/bin/env python3
# Licensed to the Apache Software Foundation (ASF) under one
# or more contributor license agreements.  See the NOTICE file
# distributed with this work for additional information
# regarding copyright ownership.  The ASF licenses this file
# to you under the Apache License, Version 2.0 (the
# "License"); you may not use this file except in compliance
# with the License.  You may obtain a copy of the License at
#
#     http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.

import itertools
import json
import os
import re
import sys
import xml.etree.ElementTree as ET
from collections import namedtuple
from functools import cmp_to_key
from urllib import request

Dependency = namedtuple('Dependency', ['group_id', 'artifact_id', 'version_variable', 'version'])
BuildVersion = namedtuple('BuildVersion', ['major', 'minor', 'patch', 'build', 'rc', 'version_number', 'version'])
VersionKey = namedtuple('VersionKey', ['major', 'minor'])

# ================================================================================
#  pom.xml related
# ================================================================================
MAVEN_TEMPLATE_VAR_PATTERN = re.compile(r'\$\{(.*?)}')
MAVEN_MISSING_GROUP_IDS = {
  'maven-surefire-plugin': 'org.apache.maven.plugins',
  'maven-compiler-plugin': 'org.apache.maven.plugins',
  'maven-failsafe-plugin': 'org.apache.maven.plugins',
}

def _xml_extract_text(node, defaultValue=None):
  return node.text if node is not None else defaultValue

def _pom_extract_dependency(node, ns, properties) -> Dependency:
  artifact_id = node.find('mvn:artifactId', ns).text
  group_id = _xml_extract_text(node.find('mvn:groupId', ns), MAVEN_MISSING_GROUP_IDS.get(artifact_id))
  if group_id is None: print('NULL GROUP ID', artifact_id)
  version = _xml_extract_text(node.find('mvn:version', ns))
  match = MAVEN_TEMPLATE_VAR_PATTERN.match(version) if version else None
  if match:
    version_variable = match.group(1)
    version = properties[match.group(1)]
  else:
    version_variable = None
  return Dependency(group_id, artifact_id, version_variable, version_parse(version))

def parse_pom(path):
  ns = { 'mvn': 'http://maven.apache.org/POM/4.0.0' }
  tree = ET.parse(path)
  root = tree.getroot()

  properties = {}
  properties['project.version'] = root.find('mvn:version', ns).text
  for properties_node in root.findall('mvn:properties', ns):
    for property in properties_node:
      name = property.tag.split('}')[1]
      version = property.text
      properties[name] = version

  for dependencies in root.findall('mvn:dependencies', ns):
    for dependency in dependencies:
      yield _pom_extract_dependency(dependency, ns, properties)

  for build in root.findall('mvn:build', ns):
    for plugins in build.findall('mvn:plugins', ns):
      for plugin in plugins:
        yield _pom_extract_dependency(plugin, ns, properties)

# ================================================================================
#  Remote Maven Repo related
# ================================================================================
RE_MVN_REPO_URL = re.compile(r'href=[\'"]?([^\'" >]+)')
RE_MVN_REPO_VERSION = re.compile(r'[0-9]+\.[0-9]+\.*[0-9]*[0-9a-zA-Z+\.-]*')
RELEASE_TYPES = {
  'FINAL': 0,
  'SNAPSHOT': 1000,
  'EA': 1000,
  'M': 2000,
  'ALPHA': 3000,
  'PREVIEW': 3000,
  'BETA': 4000,
  'RC': 5000,
  'CR': 5000,
}

#MVN_REPO_URL = 'https://repo.maven.apache.org/maven2/'
MVN_REPO_URL = 'https://repo1.maven.org/maven2/'
def fetch_remove_versions(dep: Dependency) -> dict[VersionKey, list[BuildVersion]]:
  pkg = '%s/%s' % (dep.group_id.replace('.', '/'), dep.artifact_id)

  #print(MVN_REPO_URL + pkg)
  try:
    with request.urlopen(MVN_REPO_URL + pkg) as response:
        html = response.read().decode('utf-8')
  except KeyboardInterrupt:
    raise
  except:
    return {}

  minors = {}
  for subUrl in RE_MVN_REPO_URL.findall(html):
    v = RE_MVN_REPO_VERSION.findall(subUrl)
    if not v: continue

    try:
      version = version_parse(v[0])
      minors.setdefault(VersionKey(version.major, version.minor), []).append(version)
    except KeyboardInterrupt:
      raise
    except:
      pass

  return {minor: sort_versions(versions) for minor, versions in minors.items()}

def sort_versions(versions: list[BuildVersion]) -> list[BuildVersion]:
  return sorted(versions, key=lambda v: v.version_number, reverse=True)

def _parse_int(strIntValue: str, defaultValue = 0) -> int:
  try:
    return int(strIntValue)
  except:
    return defaultValue

def version_parse(version: str) -> BuildVersion:
  if not version: return None

  normalized_version = version
  for c in ('-', '_'):
    normalized_version = normalized_version.replace(c, '.')

  release_type = None
  release_priority = 0
  release_type_index = None
  for prefix, priority in RELEASE_TYPES.items():
    release_type_index = normalized_version.upper().find(prefix)
    if release_type_index >= 0:
      release_type = normalized_version[release_type_index:]
      if normalized_version[release_type_index - 1] == '.':
        release_type_index -= 1
      release_version = release_type[len(prefix):]
      release_version = [int(v) for v in re.findall(r'\d+', release_version)]
      release_priority = priority + (release_version[0] if release_version else 0)
      break
  else:
    release_type_index = len(normalized_version)

  if release_priority == 0:
    release_type = None

  # Version Parts
  version_parts = [_parse_int(v) for v in normalized_version[:release_type_index].split('.')[:4] if v]
  while len(version_parts) < 4: version_parts.append(0)
  major, minor, patch, build = version_parts
  version_number = major * 1000_000_000_000 + minor * 1000_000_000 + patch * 1000_000 + build * 1000 + release_priority
  return BuildVersion(major, minor, patch, build, release_type, version_number, version)


# ================================================================================
#  Tool main()
# ================================================================================
def find_updates(remote_versions: dict[VersionKey, list[BuildVersion]], current_version: BuildVersion):
  new_minor_version = None
  new_major_version = None

  current_key = VersionKey(current_version.major, current_version.minor)
  remote_minors = remote_versions.get(current_key)
  latest_version = remote_minors[0] if remote_minors else None
  if latest_version and (latest_version.patch > current_version.patch or latest_version.build > current_version.build):
    new_minor_version = latest_version

  for k in sorted(remote_versions.keys(), reverse=True):
    if k < current_key: break
    new_major_version = remote_versions[k][0]
    break
  return new_minor_version, new_major_version

def _print_dependency(args, d: Dependency, properties):
  if not d.version:
    print('%32s' % (d.artifact_id))
    return

  if args.check or args.upgrade:
    remote_versions = fetch_remove_versions(d)
    latest_minor, latest_major = find_updates(remote_versions, d.version)
    if latest_minor:
      print('%32s %15s  ${%s} -> UPDATE AVAIL %s' % (d.artifact_id, d.version.version, d.version_variable, latest_minor.version))
      properties[d.version_variable] = latest_minor.version
      return

  print('%32s %15s  ${%s}' % (d.artifact_id, d.version.version, d.version_variable))
  if args.remote:
    version_key = VersionKey(d.version.major, d.version.minor)
    remote_versions = fetch_remove_versions(d)
    for k in sorted(remote_versions.keys(), reverse=True):
      if k < version_key: break
      versions = remote_versions[k]
      stable = [v.version for v in versions if not v.rc][:3]
      unstable = [v.version for v in versions if v.rc][:3]
      print(' %48s -> %d.%d.x stable:%s unstable:%s' % ('', k.major, k.minor, stable, unstable))

def tool_check_pom(args, pomfile: str):
  print('-' * 80)
  print(pom_file)
  print('-' * 80)
  properties = {}
  data = sorted(parse_pom(pom_file), key=lambda d: d.group_id)
  for k, g in itertools.groupby(data, lambda d: d.group_id):
    print(k)
    for d in g:
      _print_dependency(args, d, properties)
    print()

  if properties and args.upgrade:
    _replace_properties(pomfile, properties)
  return properties

def _scan_for_pom_files(path):
  for root, _, files in os.walk(path):
    for filename in files:
      if filename == 'pom.xml':
        yield os.path.join(root, filename)

def _replace_properties(pomfile, properties):
  with open(pomfile, 'r') as fd:
    content = fd.read()
  original_content = content

  for k, v in properties.items():
    regex = '(?<=<' + k +'>)(.*?)(?=</' + k +'>)'
    content = re.sub(regex, v, content)

  if content != original_content:
    print('[UPDT]', pomfile)
    with open(pomfile, 'w') as fd:
      fd.write(content)
  else:
    print('[KEEP]', pomfile)

if __name__ == '__main__':
  import argparse
  parser = argparse.ArgumentParser(description="Maven Dependencies Tool")
  cmd_parsers = parser.add_subparsers(help='command help')
  # pom-deps
  parser_pom_deps = cmd_parsers.add_parser('pom-deps', help='Maven pom.xml Dependencies Inspectors')
  parser_pom_deps.add_argument('--upgrade', action='store_const', const=True, help='Update pom.xml when minor versions are available')
  parser_pom_deps.add_argument('--remote', action='store_const', const=True, help='Remote Update check')
  parser_pom_deps.add_argument('--check', action='store_const', const=True, help='Remote Update check minor')
  parser_pom_deps.add_argument('pom_files', metavar='path/pom.xml', type=str, nargs='+',
                               help='The path to the pom.xml file to extract dependencies from')
  # scan-pom
  parser_scan_pom = cmd_parsers.add_parser('scan-pom', help='Scan for pom.xml in path')
  parser_scan_pom.add_argument('--upgrade', action='store_const', const=True, help='Update pom.xml when minor versions are available')
  parser_scan_pom.add_argument('--remote', action='store_const', const=True, help='Remote Update check')
  parser_scan_pom.add_argument('--check', action='store_const', const=True, help='Remote Update check minor')
  parser_scan_pom.add_argument('pom_dirs', metavar='path', type=str, nargs='+',
                               help='The path to scan for pom files')
  args = parser.parse_args()

  if 'pom_files' in args and args.pom_files:
    for pom_file in args.pom_files:
      tool_check_pom(args, pom_file)
  elif 'pom_dirs' in args and args.pom_dirs:
    for pom_dir in args.pom_dirs:
      for pom_file in _scan_for_pom_files(pom_dir):
        tool_check_pom(args, pom_file)