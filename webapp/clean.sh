#!/bin/bash

if [ -e "env.sh" ]; then
	source env.sh
elif [ -e "../env.sh" ]; then
    source ../env.sh
fi

$MVN -Dmaven.javadoc.skip=true clean

