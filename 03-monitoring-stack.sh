#!/usr/bin/env bash
set -Eeuo pipefail

monitoring_path=$(dirname "$0")/monitoring

helm repo add elastic https://helm.elastic.co
helm repo add prometheus https://prometheus-community.github.io/helm-charts
helm repo update elastic prometheus

echo "--- Clean up ---"
helm uninstall kibana --ignore-not-found
helm uninstall apm-server --ignore-not-found
helm uninstall prometheus --ignore-not-found
helm uninstall logstash --ignore-not-found
helm uninstall filebeat --ignore-not-found
helm uninstall elasticsearch --ignore-not-found
kubectl delete namespace monitoring --ignore-not-found

echo "--- Installing ---"

echo "Elasticsearch..."
helm install elasticsearch elastic/elasticsearch --namespace monitoring \
  --values "$monitoring_path/elasticsearch-values.yml" --create-namespace

echo "Filebeat..."
helm install filebeat elastic/filebeat --namespace monitoring --values "$monitoring_path/filebeat-values.yml"

echo "Logstash..."
helm install logstash elastic/logstash --namespace monitoring --values "$monitoring_path/logstash-values.yml"

echo "Prometheus & Grafana..."
helm install prometheus prometheus/kube-prometheus-stack --namespace monitoring \
  --values "$monitoring_path/prometheus-values.yml"

echo "APM..."
helm install apm-server elastic/apm-server --namespace monitoring --values "$monitoring_path/apm-values.yml"

echo "Kibana..."
helm install kibana elastic/kibana --namespace monitoring --values "$monitoring_path/kibana-values.yml"

echo
echo "--- Prometheus & Grafana credentials ---"
kubectl --namespace monitoring get secrets prometheus-grafana -o jsonpath="{.data.admin-user}" | base64 --decode | xargs printf 'Username: %s \n'
kubectl --namespace monitoring get secrets prometheus-grafana -o jsonpath="{.data.admin-password}" | base64 --decode | xargs printf 'Password: %s \n'

echo
echo "--- Kibana & Elasticsearch credentials ---"
kubectl get secret elasticsearch-master-credentials --namespace monitoring -o jsonpath="{.data.username}" | base64 --decode | xargs printf 'Username: %s \n'
kubectl get secret elasticsearch-master-credentials --namespace monitoring -o jsonpath="{.data.password}" | base64 --decode | xargs printf 'Password: %s \n'
