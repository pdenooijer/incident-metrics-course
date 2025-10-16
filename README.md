# Incident metrics course

This project uses Quarkus, the Supersonic Subatomic Java Framework.

If you want to learn more about Quarkus, please visit its website: <https://quarkus.io/>.

## Prerequisites
You must have the following installed on your machine:
* podman-desktop - `brew install podman-desktop`
* Kubernetes in Docker (kind) - `brew install kind`
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

## Running the applications in dev mode

You can run your application in dev mode that enables live coding using:

```shell script
mvn quarkus:dev
```

> **_NOTE:_**  Quarkus ships with a Dev UI, which is available in dev mode only at <http://localhost:8080/q/dev/>.

## Package, build and run the microservices with Maven & Podman

The applications can be packaged, build and run in a single command using:

```shell script
./03-build-and-run-services.sh
```
