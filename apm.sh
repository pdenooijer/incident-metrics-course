#!/usr/bin/env bash
set -Eeuo pipefail

monitoring_path=$(dirname "$0")/monitoring

helm repo add elastic https://helm.elastic.co
helm repo update elastic

helm upgrade --install apm-server elastic/apm-server --namespace monitoring --values "$monitoring_path/helm/apm-values.yml"
helm install eck-stack-with-apm-server elastic/eck-stack \
    --values https://raw.githubusercontent.com/elastic/cloud-on-k8s/3.2/deploy/eck-stack/examples/apm-server/basic.yaml -n elastic-stack



# Eventueel via de operator een installatie doen? Is dat makkelijker?
#helm install elastic-operator elastic/eck-operator -n elastic-system --create-namespace
## Install an eck-managed Elasticsearch, Kibana, and standalone APM Server using custom values.
#helm install eck-stack-with-apm-server elastic/eck-stack --values https://raw.githubusercontent.com/elastic/cloud-on-k8s/3.2/deploy/eck-stack/examples/apm-server/basic.yaml -n elastic-stack --create-namespace



#-javaagent:/Users/isc92401/tmp/observability/apm/elastic-apm-agent.jar -Delastic.apg.service_name=wvggz-connector -Delastic.apm.server_url=http://localhost:8200 -Delastic.apm.application.package=nl.politie.bvi.zenv.wvggz.connector -Dserver.port=8083
#-javaagent:/path/to/elastic-apm-agent-<version>.jar
