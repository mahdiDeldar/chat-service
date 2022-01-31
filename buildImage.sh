#!/usr/bin/env bash

./gradlew clean bootJar
mkdir -p build/dependency && (cd build/dependency; jar -xf ../libs/*.jar)
docker build -t shervin/chat_service:$(./gradlew properties -q | grep version | awk '{print $2}') .
docker tag shervin/chat_service:$(./gradlew properties -q | grep version | awk '{print $2}') shervin/chat_service:latest
