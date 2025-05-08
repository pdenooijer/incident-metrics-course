#!/bin/bash -Eeu
set -o pipefail

mvn package

podman build hello-world/ --file hello-world/src/main/docker/Dockerfile --tag incident-metrics-course/hello-world
