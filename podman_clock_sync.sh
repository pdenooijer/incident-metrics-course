#!/usr/bin/env bash
set -Eeuo pipefail

podman machine ssh incident-metrics-workshop sudo date --set "$(date +'%Y-%m-%dT%H:%M:%S')"
