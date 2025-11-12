#!/usr/bin/env bash
set -Eeuo pipefail

name=incident-metrics-workshop

echo "Stopping and deleting ${name} VM (if exists)..."
podman machine stop "${name}" || true
podman machine rm --force "${name}" || true

echo "Creating VM ${name}..."
podman machine init --memory 16384 --cpus 6 "${name}"
echo "Starting VM..."
podman machine start "${name}"
echo "Setting default VM..."
podman system connection default "${name}"

registry_name=kind-registry
echo "Set up ${registry_name} and starting it..."
# Create registry container
# https://kind.sigs.k8s.io/docs/user/local-registry/
podman run --detach --restart=always --publish "127.0.0.1:5001:5000" --network bridge --name "${registry_name}" registry:2
