#!/bin/bash -Eeu
set -o pipefail

podman machine stop

# Create machine and set as default
podman machine init --memory 16384 --cpus 6 incident-metrics-workshop
podman machine start incident-metrics-workshop
podman system connection default incident-metrics-workshop

# Create KinD cluster
KIND_EXPERIMENTAL_PROVIDER=podman kind create cluster --name incident-metrics-workshop
