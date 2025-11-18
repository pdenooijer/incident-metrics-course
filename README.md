# Incident metrics course

This project uses Quarkus, the Supersonic Subatomic Java Framework.

If you want to learn more about Quarkus, please visit its website: <https://quarkus.io/>.

## Prerequisites

You must have the following installed on your machine:
* podman-desktop - `brew install podman-desktop podman`
* Kubernetes in Docker (kind) - `brew install kind`
* Helm - `brew install helm`
* Kubectl - `brew install kubectl`
* Maven
* Java 21 or newer

## Create Kubernetes cluster

A new podman machine will be created that will run the Kubernetes cluster and
image registry for this course. Run the following script:

```shell script
./01-create-machine.sh
```
Run this command to create the Kind Kubernetes cluster on the newly created Podman machine.

```shell script
./02-create-cluster.sh
```

## Add monitoring stack

```shell script
./03-monitoring-stack.sh
```

## Package, build and run the microservices with Maven & Podman

The applications can be packaged, build and run in a single command using:

```shell script
./04-build-and-run-services.sh
```

## You can find the applications under: 

- http://localhost:30080/rabbitmq
- http://localhost:30080/hello


- http://localhost:30080/kibana  
- http://localhost:30080/prometheus
- http://localhost:30080/grafana


## Add APM integration

1. Go to Kibana
2. Add Elastic APM integration: http://localhost:30080/kibana/app/integrations/detail/apm/overview
3. Under `General` > `Server configuration` change 
   1. `Host` from `localhost:8200` to `apm-server-apm-server:8200`
   2. `URL` from `https://localhost:8200` to `http://apm-server-apm-server:8200`
4. Save
5. In the next popup select `Add Elastic Agent later`


## Running the applications in dev mode

You can run your application in dev mode that enables live coding using:

```shell script
mvn quarkus:dev
```

> **_NOTE:_**  Quarkus ships with a Dev UI, which is available in dev mode only at <http://localhost:8080/q/dev/>.
