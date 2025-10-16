#!/bin/bash -Eeu
set -o pipefail

mvn package

registry=localhost:5001
version=$(date +%s)

hello_world_tag=${registry}/hello-world:${version}
podman build hello-world/ --file hello-world/src/main/docker/Dockerfile --tag "${hello_world_tag}"
podman push "${hello_world_tag}" --tls-verify=false

helm upgrade --install hello-world hello-world/helm/ --namespace applications --create-namespace \
  --set version="${version}"

processor_tag=${registry}/rabbitmq-processor:${version}
podman build rabbitmq/rabbitmq-processor/ --file rabbitmq/rabbitmq-processor/src/main/docker/Dockerfile --tag "${processor_tag}"
podman push "${processor_tag}" --tls-verify=false

producer_tag=${registry}/rabbitmq-producer:${version}
podman build rabbitmq/rabbitmq-producer/ --file rabbitmq/rabbitmq-producer/src/main/docker/Dockerfile --tag "${producer_tag}"
podman push "${producer_tag}" --tls-verify=false

helm upgrade --install rabbitmq rabbitmq/helm/ --namespace applications --create-namespace \
  --set version="${version}"
