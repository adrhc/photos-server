#!/bin/bash
source env.sh

# ./test-report.sh -P db-off
# ./test-report.sh -P db-on

# $MVN test
$MVN surefire-report:report "$@"
