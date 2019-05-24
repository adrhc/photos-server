#!/bin/bash

if [ -e "env.sh" ]; then
	source env.sh
elif [ -e mvnw ]; then
    source ../env.sh
fi

rm -vf exifweb-test.log

# $MVN -Dmaven.javadoc.skip=true -Dmaven.test.skip=true clean install
# $MVN -Dmaven.javadoc.skip=true clean install -P local-db
$MVN -Dmaven.javadoc.skip=true clean install "$@"
