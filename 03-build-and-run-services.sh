#!/bin/bash -Eeu
set -o pipefail


helm upgrade --install rabbitmq rabbitmq/helm/ --namespace applications --create-namespace


mvn package

registry=localhost:5001
version=$(date +%s)
version_tag=${registry}/hello-world:${version}
podman build hello-world/ --file hello-world/src/main/docker/Dockerfile --tag "${version_tag}"
podman push "${version_tag}" --tls-verify=false

helm upgrade --install hello-world hello-world/helm/ --namespace applications --create-namespace \
  --set version="${version}"


version_tag=${registry}/rabbitmq-processor:${version}
podman build rabbitmq-processor/ --file rabbitmq-processor/src/main/docker/Dockerfile --tag "${version_tag}"
podman push "${version_tag}" --tls-verify=false

helm upgrade --install rabbitmq-processor rabbitmq-processor/helm/ --namespace applications --create-namespace \
  --set version="${version}"


version_tag=${registry}/rabbitmq-producer:${version}
podman build rabbitmq-producer/ --file rabbitmq-producer/src/main/docker/Dockerfile --tag "${version_tag}"
podman push "${version_tag}" --tls-verify=false

helm upgrade --install rabbitmq-producer rabbitmq-producer/helm/ --namespace applications --create-namespace \
  --set version="${version}"
