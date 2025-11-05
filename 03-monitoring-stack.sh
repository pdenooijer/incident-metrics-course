#!/usr/bin/env bash
set -Eeuo pipefail

monitoring_path=$(dirname "$0")/monitoring

helm repo add elastic https://helm.elastic.co
helm repo update elastic

echo "--- Installing ---"

echo "Elasticsearch..."
helm upgrade --install elasticsearch elastic/elasticsearch --values "$monitoring_path/helm/elasticsearch-values.yml" --namespace monitoring --create-namespace

echo "Filebeat..."
helm upgrade --install filebeat elastic/filebeat --values "$monitoring_path/helm/filebeat-values.yml" --namespace monitoring

echo "Logstash..."
helm upgrade --install logstash elastic/logstash --values "$monitoring_path/helm/logstash-values.yml" --namespace monitoring

echo "Kibana..."
kubectl delete secret kibana-kibana-es-token -n monitoring --ignore-not-found
kubectl delete configmap kibana-kibana-helm-scripts -n monitoring --ignore-not-found
kubectl delete serviceaccount pre-install-kibana-kibana -n monitoring --ignore-not-found
kubectl delete role pre-install-kibana-kibana -n monitoring --ignore-not-found
kubectl delete rolebinding pre-install-kibana-kibana -n monitoring --ignore-not-found
kubectl delete job pre-install-kibana-kibana -n monitoring --ignore-not-found
helm upgrade --install kibana elastic/kibana --values "$monitoring_path/helm/kibana-values.yml" --namespace monitoring --force

echo
echo "--- Elasticsearch credentials ---"
kubectl get secret elasticsearch-master-credentials --namespace monitoring -o jsonpath="{.data.username}" | base64 --decode | xargs printf 'Username: %s \n'
kubectl get secret elasticsearch-master-credentials --namespace monitoring -o jsonpath="{.data.password}" | base64 --decode | xargs printf 'Password: %s \n'
