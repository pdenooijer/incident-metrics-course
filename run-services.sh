#!/bin/bash -Eeu
set -o pipefail

podman compose --file podman-compose.yml up
