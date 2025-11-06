#!/usr/bin/env bash
set -Eeuo pipefail

cluster_path=$(dirname "$0")/cluster

# Create kind cluster with containerd registry config dir enabled
#
# See:
# https://github.com/kubernetes-sigs/kind/issues/2875
# https://github.com/containerd/containerd/blob/main/docs/cri/config.md#registry-configuration
# See: https://github.com/containerd/containerd/blob/main/docs/hosts.md
export KIND_EXPERIMENTAL_PROVIDER=podman
name=incident-metrics-workshop

# Delete the cluster if it already exists
kind delete cluster --name "${name}" 2>/dev/null || true

kind create cluster --name "${name}" --config="$cluster_path/kind-cluster.yml"

# Add the registry config to the nodes
#
# This is necessary because localhost resolves to loopback addresses that are network-namespace local. In other words:
# localhost in the container is not localhost on the host. We want a consistent name that works from both ends,
# so we tell containerd to alias localhost:5001 to the registry container when pulling images.
registry_name='kind-registry'
registry_port='5001'
registry_dir="/etc/containerd/certs.d/localhost:${registry_port}"
for node in $(kind get nodes --name "${name}"); do
  podman exec "${node}" mkdir -p "${registry_dir}"
  cat <<EOF | podman exec --interactive "${node}" cp /dev/stdin "${registry_dir}/hosts.toml"
[host."http://${registry_name}:5000"]
EOF
done

# Connect the registry to the cluster network.
if podman network inspect kind | grep -q "\"name\": \"${registry_name}\""; then
  echo "already connected"
else
  echo "connecting network..."
  podman network connect "kind" "${registry_name}"
fi

# Document the local registry.
# https://github.com/kubernetes/enhancements/tree/master/keps/sig-cluster-lifecycle/generic/1755-communicating-a-local-registry
kubectl apply --filename "$cluster_path/local-registry-configmap.yml"
