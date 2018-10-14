#!/bin/bash
source ../env.sh

rm -vf exifweb-test.log

# $MVN -Dmaven.javadoc.skip=true -Dmaven.test.skip=true clean install
# $MVN -Dmaven.javadoc.skip=true clean install -P local-db
$MVN -Dmaven.javadoc.skip=true clean install "$@"
