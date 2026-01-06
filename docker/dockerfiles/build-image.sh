#!/bin/bash

set +e

function usage() {
    echo "Usage: build-image.sh <build_type>"
    echo "    build_type: JVM | FLINK | BUILD | RUNTIME | ALL<BUILD, RUNTIME, FLINK>"
}

function validate_docker() {
    docker info > /dev/null 2>&1
    if [ $? -ne 0 ]; then
        echo "Docker is not running, please start docker first."
        exit 1
    fi
}

function build_jvm() {
    JVM_IMAGE=$(docker images --filter "reference=onebase-jvm" --format "json")
    if [ -n "${JVM_IMAGE}" ]; then
        echo "onebase-jvm image already exists, skip building."
        return
    fi
    docker build -t onebase-jvm:v1.0.0 -f onebase-base-jvm.Dockerfile .
}

function copy_libs() {
    mv ../../onebase-server/target/onebase-server.jar onebase-server-build/lib/
    mv ../../onebase-server-flink/target/onebase-server-flink.jar onebase-server-flink/lib/
    mv ../../onebase-server-runtime/target/onebase-server-runtime.jar onebase-server-runtime/lib/
}

CURRENT_DIR=$(cd $(dirname $0); pwd)
cd ${CURRENT_DIR}

validate_docker
copy_libs

case $1 in
    all | ALL)
        build_jvm
        docker build -t onebase-server-flink:v1.0.0 -f onebase-server-flink.Dockerfile .
        docker build -t onebase-server-build:v1.0.0 -f onebase-server-build.Dockerfile .
        docker build -t onebase-server-runtime:v1.0.0 -f onebase-server-runtime.Dockerfile .
        docker save onebase-server-build:v1.0.0 > onebase-be-build-1.0.0.tar
        docker save onebase-server-flink:v1.0.0 > onebase-be-flink-1.0.0.tar
        docker save onebase-server-runtime:v1.0.0 > onebase-be-runtime-1.0.0.tar
        shift
        break
        ;;
    flink | FLINK)
        build_jvm
        docker build -t onebase-server-flink:v1.0.0 -f onebase-server-flink.Dockerfile .
        docker save onebase-server-flink:v1.0.0 > onebase-be-flink-1.0.0.tar
        shift
        break
        ;;
    build | BUILD)
        build_jvm
        docker build -t onebase-server-build:v1.0.0 -f onebase-server-build.Dockerfile .
        docker save onebase-server-build:v1.0.0 > onebase-be-build-1.0.0.tar
        shift
        break
        ;;
    runtime | RUNTIME)
        build_jvm
        docker build -t onebase-server-runtime:v1.0.0 -f onebase-server-runtime.Dockerfile .
        docker save onebase-server-runtime:v1.0.0 > onebase-be-runtime-1.0.0.tar
        shift
        break
        ;;
    jvm | JVM)
        build_jvm
        docker save onebase-jvm:v1.0.0 > onebase-jvm-1.0.0.tar
        shift
        break
        ;;
    *)
        usage
        exit 0
        ;;
esac