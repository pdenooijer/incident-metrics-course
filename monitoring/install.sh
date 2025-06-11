#!/bin/bash -Eeu
set -o pipefail

monitoring_path=$(dirname "$0")

helm repo add elastic https://helm.elastic.co
helm repo update elastic

helm install elasticsearch elastic/elasticsearch -f "$monitoring_path/helm/elasticsearch-values.yml" -n monitoring --create-namespace
helm install filebeat elastic/filebeat -f "$monitoring_path/helm/filebeat-values.yml" -n monitoring
helm install logstash elastic/logstash -f "$monitoring_path/helm/logstash-values.yml" -n monitoring
helm install kibana elastic/kibana -f "$monitoring_path/helm/kibana-values.yml" -n monitoring

kubectl get secret elasticsearch-master-credentials -n monitoring -o jsonpath="{.data.username}" | base64 --decode |\
  xargs printf 'Username: %s \n'
kubectl get secret elasticsearch-master-credentials -n monitoring -o jsonpath="{.data.password}" | base64 --decode |\
  xargs printf 'Password: %s \n'
