#!/bin/zsh
podman pull docker.io/elasticsearch:8.18.0
podman pull docker.io/logstash:8.18.0
podman pull docker.io/kibana:8.18.0
podman pull docker.io/prom/prometheus:v3.3.1
podman pull docker.io/grafana/grafana:12.0.0

podman machine stop
podman machine set --cpus 2 --memory 8048
podman machine start