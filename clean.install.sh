#!/bin/bash
source env.sh

# $MVN -Dmaven.javadoc.skip=true -Dmaven.test.skip=true clean install
# ./clean.install.sh -P db-off
# ./clean.install.sh -P db-on
$MVN -Dmaven.javadoc.skip=true clean install "$@"
