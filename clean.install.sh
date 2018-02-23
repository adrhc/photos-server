#!/bin/bash
source env.sh

# $MVN -Dmaven.test.skip=true -Dmaven.javadoc.skip=true clean install
# $MVN -Dmaven.javadoc.skip=true clean install -P db-on
$MVN -Dmaven.javadoc.skip=true clean install "$@"
