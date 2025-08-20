#!/bin/bash -Eeu
set -o pipefail

podman machine stop 2>/dev/null || true

# Create podman machine and set as default
name=incident-metrics-workshop
podman machine init --memory 16384 --cpus 6 "${name}"
podman machine start "${name}"
podman system connection default "${name}"

# Create registry container
# https://kind.sigs.k8s.io/docs/user/local-registry/
podman run --detach --restart=always --publish "127.0.0.1:5001:5000" --network bridge --name 'kind-registry' registry:2
