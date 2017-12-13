#!/usr/bin/env bash

set -e -x

cd sources

./gradlew :dataj-core:build

cp dataj-core/build/libs/* ../build