#!/bin/bash

if [ -e "env.sh" ]; then
	source env.sh
elif [ -e mvnw ]; then
    source ../env.sh
fi

$MVN -Dmaven.javadoc.skip=true install "$@"
