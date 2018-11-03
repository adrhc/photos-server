#!/bin/bash
./mvnw -e -Dmaven.javadoc.skip=true install "$@"
