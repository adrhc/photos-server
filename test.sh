#!/bin/bash
source env.sh

# ./test.sh -P db-off
# ./test.sh -P local-db
# ./test.sh -P remote-db

$MVN test "$@"
