#!/bin/bash -Eeu
set -o pipefail

mvn package

registry=localhost:5001
version=$(date +%s)
version_tag=${registry}/hello-world:${version}
podman build hello-world/ --file hello-world/src/main/docker/Dockerfile --tag "${version_tag}"
podman push "${version_tag}" --tls-verify=false

helm upgrade --install hello-world hello-world/helm/ --namespace applications --create-namespace \
  --set version="${version}"
