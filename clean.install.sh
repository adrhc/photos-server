#!/bin/bash
source env.sh

# $MVN -Dmaven.javadoc.skip=true -Dmaven.test.skip=true clean install
# $MVN -Dmaven.javadoc.skip=true clean install -P db-off
# $MVN -Dmaven.javadoc.skip=true clean install -P db-on
$MVN -Dmaven.javadoc.skip=true clean install "$@"
