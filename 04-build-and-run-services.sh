#!/usr/bin/env bash
set -Eeuo pipefail

mvn package -Dmaven.test.skip

registry=localhost:5001
version=$(date +%s)

hello_world_tag=${registry}/hello-world:${version}
podman build applications/hello-world/ --file applications/hello-world/src/main/docker/Dockerfile \
  --tag "${hello_world_tag}"
podman push "${hello_world_tag}" --tls-verify=false

helm upgrade --install hello-world applications/hello-world/helm/ --namespace applications --create-namespace \
  --set version="${version}"

api_gateway_tag=${registry}/api-gateway:${version}
podman build applications/api-gateway/ --file applications/api-gateway/src/main/docker/Dockerfile --tag "${api_gateway_tag}"
podman push "${api_gateway_tag}" --tls-verify=false

helm upgrade --install api-gateway applications/api-gateway/helm/ --namespace applications --create-namespace \
  --set version="${version}"

processor_tag=${registry}/rabbitmq-processor:${version}
podman build applications/rabbitmq/rabbitmq-processor/ --file applications/rabbitmq/rabbitmq-processor/src/main/docker/Dockerfile \
  --tag "${processor_tag}"
podman push "${processor_tag}" --tls-verify=false

producer_tag=${registry}/rabbitmq-producer:${version}
podman build applications/rabbitmq/rabbitmq-producer/ --file applications/rabbitmq/rabbitmq-producer/src/main/docker/Dockerfile \
  --tag "${producer_tag}"
podman push "${producer_tag}" --tls-verify=false

helm upgrade --install rabbitmq applications/rabbitmq/helm/ --namespace applications --create-namespace \
  --set version="${version}"
