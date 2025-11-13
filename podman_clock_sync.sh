#!/usr/bin/env bash
set -Eeuo pipefail
podman machine ssh sudo date --set $(date +'%Y-%m-%dT%H:%M:%S')
