#!/bin/bash

# disable all tests:
# ./mvnw -e -Dmaven.javadoc.skip=true -Dmaven.test.skip=true clean install

# these require <forkCount> usage:
# ./clean.install.sh -P db-off	-> equivalent to in-memory-only
# ./clean.install.sh -P db-on
#
# these does not require <forkCount> usage:
# ./clean.install.sh -P staging-only
# ./clean.install.sh -P production-only
./mvnw -e -Dmaven.javadoc.skip=true clean install "$@"
