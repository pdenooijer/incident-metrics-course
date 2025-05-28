#!/bin/bash -Eeu
set -o pipefail

helm repo add elastic https://helm.elastic.co
helm repo update elastic

helm install elasticsearch elastic/elasticsearch -f helm/elasticsearch-values.yml -n monitoring --create-namespace
helm install filebeat elastic/filebeat -f helm/filebeat-values.yml -n monitoring
helm install logstash elastic/logstash -f helm/logstash-values.yml -n monitoring
helm install kibana elastic/kibana -f helm/kibana-values.yml -n monitoring

kubectl get secret elasticsearch-master-credentials -n monitoring -o jsonpath="{.data.username}" | base64 --decode |\
  xargs printf 'Username: %s \n'
kubectl get secret elasticsearch-master-credentials -n monitoring -o jsonpath="{.data.password}" | base64 --decode |\
  xargs printf 'Password: %s \n'
