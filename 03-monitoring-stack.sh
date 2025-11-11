#!/usr/bin/env bash
set -Eeuo pipefail

monitoring_path=$(dirname "$0")/monitoring

helm repo add elastic https://helm.elastic.co
helm repo add prometheus https://prometheus-community.github.io/helm-charts
helm repo update elastic prometheus

echo "--- Installing ---"

echo "Elasticsearch..."
helm upgrade --install elasticsearch elastic/elasticsearch --values "$monitoring_path/elasticsearch-values.yml" --namespace monitoring --create-namespace

echo "Filebeat..."
helm upgrade --install filebeat elastic/filebeat --values "$monitoring_path/filebeat-values.yml" --namespace monitoring

echo "Logstash..."
helm upgrade --install logstash elastic/logstash --values "$monitoring_path/logstash-values.yml" --namespace monitoring

echo "Kibana..."
kubectl delete secret kibana-kibana-es-token --namespace monitoring --ignore-not-found
kubectl delete configmap kibana-kibana-helm-scripts --namespace monitoring --ignore-not-found
kubectl delete serviceaccount pre-install-kibana-kibana --namespace monitoring --ignore-not-found
kubectl delete role pre-install-kibana-kibana --namespace monitoring --ignore-not-found
kubectl delete rolebinding pre-install-kibana-kibana --namespace monitoring --ignore-not-found
kubectl delete job pre-install-kibana-kibana --namespace monitoring --ignore-not-found
helm upgrade --install kibana elastic/kibana --values "$monitoring_path/kibana-values.yml" --namespace monitoring --force

echo "Prometheus & Grafana..."
helm upgrade --install prometheus prometheus-community/kube-prometheus-stack \
  --values "$monitoring_path/prometheus-values.yml" --namespace monitoring

echo "APM..."
helm upgrade --install apm-server elastic/apm-server --namespace monitoring --values "$monitoring_path/apm-values.yml"

echo
echo "--- Prometheus & Grafana credentials ---"
kubectl --namespace monitoring get secrets prometheus-grafana -o jsonpath="{.data.admin-user}" | base64 --decode | xargs printf 'Username: %s \n'
kubectl --namespace monitoring get secrets prometheus-grafana -o jsonpath="{.data.admin-password}" | base64 --decode | xargs printf 'Password: %s \n'

echo
echo "--- Kibana & Elasticsearch credentials ---"
kubectl get secret elasticsearch-master-credentials --namespace monitoring -o jsonpath="{.data.username}" | base64 --decode | xargs printf 'Username: %s \n'
kubectl get secret elasticsearch-master-credentials --namespace monitoring -o jsonpath="{.data.password}" | base64 --decode | xargs printf 'Password: %s \n'
