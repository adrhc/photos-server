#!/bin/bash
source env.sh
$MVN -e -Dmaven.javadoc.skip=true install "$@"
