#!/bin/bash
source env.sh

# ./test-report.sh -P db-off
# ./test-report.sh -P local-db
# ./test-report.sh -P remote-db

# $MVN test
$MVN surefire-report:report "$@"
