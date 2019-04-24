#!/bin/bash
source env.sh
# these require <forkCount> usage:
# ./test-report.sh -P db-off	-> equivalent to in-memory-only
# ./test-report.sh -P db-on
#
# these does not require <forkCount> usage:
# ./test-report.sh -P staging-only
# ./test-report.sh -P production-only
$MVN -e surefire-report:report "$@"
