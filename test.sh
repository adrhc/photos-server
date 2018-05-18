#!/bin/bash
source env.sh

# ./test.sh -P db-off
# ./test.sh -P db-on

$MVN test "$@"
