#!/bin/bash

# these require <forkCount> usage:
# ./test-report.sh -P db-off	-> equivalent to in-memory-only
# ./test-report.sh -P db-on
#
# these does not require <forkCount> usage:
# ./test-report.sh -P staging-only
# ./test-report.sh -P production-only
./mvnw -e surefire-report:report "$@"
