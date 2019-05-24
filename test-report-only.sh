#!/bin/bash

if [ -e "env.sh" ]; then
	source env.sh
elif [ -e "../env.sh" ]; then
    source ../env.sh
fi

# these require <forkCount> usage:
# ./test-report-only.sh -P db-off	-> equivalent to in-memory-only
# ./test-report-only.sh -P db-on
#
# these does not require <forkCount> usage:
# ./test-report-only.sh -P staging-only
# ./test-report-only.sh -P production-only
$MVN -e surefire-report:report-only
