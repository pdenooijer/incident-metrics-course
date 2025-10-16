#!/bin/bash -Eeu
set -o pipefail

# Create kind cluster with containerd registry config dir enabled
#
# See:
# https://github.com/kubernetes-sigs/kind/issues/2875
# https://github.com/containerd/containerd/blob/main/docs/cri/config.md#registry-configuration
# See: https://github.com/containerd/containerd/blob/main/docs/hosts.md
export KIND_EXPERIMENTAL_PROVIDER=podman
name=incident-metrics-workshop
cat <<EOF | kind create cluster --name "${name}" --config=-
kind: Cluster
apiVersion: kind.x-k8s.io/v1alpha4
containerdConfigPatches:
- |-
  [plugins."io.containerd.grpc.v1.cri".registry]
    config_path = "/etc/containerd/certs.d"
nodes:
- role: control-plane
  extraPortMappings:
  - containerPort: 30000
    hostPort: 30000
    listenAddress: "0.0.0.0" # Optional, defaults to "0.0.0.0"
    protocol: tcp # Optional, defaults to tcp
- role: worker

EOF

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
  cat <<EOF | podman exec -i "${node}" cp /dev/stdin "${registry_dir}/hosts.toml"
[host."http://${registry_name}:5000"]
EOF
done

# Connect the registry to the cluster network.
podman network connect "kind" "${registry_name}"

# Document the local registry.
# https://github.com/kubernetes/enhancements/tree/master/keps/sig-cluster-lifecycle/generic/1755-communicating-a-local-registry
cat <<EOF | kubectl apply -f -
apiVersion: v1
kind: ConfigMap
metadata:
  name: local-registry-hosting
  namespace: kube-public
data:
  localRegistryHosting.v1: |
    host: "localhost:${registry_port}"
    help: "https://kind.sigs.k8s.io/docs/user/local-registry/"
EOF
