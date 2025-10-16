#!/bin/bash -Eeu
set -o pipefail


echo "Stopping current VM..."
search_string="*"
podman machine list -q | grep "$search_string" | sed 's/*//g' | xargs -r -n 1 podman machine stop

# Create podman machine and set as default
name=incident-metrics-workshop

if podman machine ls -q | grep -q "^$name" ; then
  echo "VM $name already exists..."
else
  echo "Creating VM $name..."
  podman machine init --memory 16384 --cpus 6 "${name}"
fi
echo "Starting VM..."
podman machine start "${name}"
echo "Setting default VM..."
podman system connection default "${name}"

registry_name=kind-registry
if podman container exists "$registry_name" ; then
  echo "$registry_name already exists, starting it..."
  podman start "$registry_name"
else
  echo "Set up $registry_name and starting it..."
  # Create registry container
  # https://kind.sigs.k8s.io/docs/user/local-registry/
  podman run --detach --restart=always --publish "127.0.0.1:5001:5000" --network bridge --name "$registry_name" registry:2
fi
