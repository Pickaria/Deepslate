#!/bin/bash

VANILLA_VERSION=1.19.2
ROOT_DIR=${1:-.}

mkdir -p $ROOT_DIR/server/plugins

echo "Getting Paper version..."

build=${PAPERBUILD:=$(curl -fsSL "https://papermc.io/api/v2/projects/paper/versions/${VANILLA_VERSION}" -H "accept: application/json" |
  jq '.builds[-1]')}

SERVER=$(curl -fsSL "https://papermc.io/api/v2/projects/paper/versions/${VANILLA_VERSION}/builds/${build}" -H "accept: application/json" |
  jq -r '.downloads.application.name')

echo "Downloading $SERVER..."

curl -fsSL -o "$ROOT_DIR/server/paper.jar" \
  "https://papermc.io/api/v2/projects/paper/versions/${VANILLA_VERSION}/builds/${build}/downloads/${SERVER}" \
  -H "accept: application/java-archive"

echo "Downloading PlugManX..."

curl -fsSL -o "$ROOT_DIR/server/plugins/PlugManX.jar" \
  "https://api.spiget.org/v2/resources/88135/download" \
  -H "accept: application/java-archive"

echo "Downloading Vault..."

curl -fsSL -o "$ROOT_DIR/server/plugins/Vault.jar" \
  "https://api.spiget.org/v2/resources/34315/download" \
  -H "accept: application/java-archive"

echo "Creating basic configuration..."

echo "eula=true" > $ROOT_DIR/server/eula.txt