#!/bin/bash -Eeu
set -o pipefail

monitoring_path=$(dirname "$0")

helm repo add elastic https://helm.elastic.co
helm repo update elastic

helm install elasticsearch elastic/elasticsearch --values "$monitoring_path/helm/elasticsearch-values.yml" \
  --namespace monitoring --create-namespace
helm install filebeat elastic/filebeat --values "$monitoring_path/helm/filebeat-values.yml" --namespace monitoring
helm install logstash elastic/logstash --values "$monitoring_path/helm/logstash-values.yml" --namespace monitoring
helm install kibana elastic/kibana --values "$monitoring_path/helm/kibana-values.yml" --namespace monitoring

echo
echo "--- Elasticsearch credentials ---"
kubectl get secret elasticsearch-master-credentials --namespace monitoring -o jsonpath="{.data.username}" |\
  base64 --decode | xargs printf 'Username: %s \n'
kubectl get secret elasticsearch-master-credentials --namespace monitoring -o jsonpath="{.data.password}" |\
  base64 --decode | xargs printf 'Password: %s \n'
