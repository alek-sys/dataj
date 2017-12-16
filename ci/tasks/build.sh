#!/usr/bin/env bash

set -e

mkdir ~/.gradle/
touch ~/.gradle/gradle.properties
echo "ossrhUser=${OSSRHUSER}" >> ~/.gradle/gradle.properties
echo "ossrhPassword=${OSSRHPASSWORD}" >> ~/.gradle/gradle.properties

cd sources

./gradlew :dataj-core:build -x signArchives

cp dataj-core/build/libs/* ../build