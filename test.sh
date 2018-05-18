#!/bin/bash
source env.sh

# these require <forkCount> usage:
# ./test.sh -P db-off	-> equivalent to in-memory-only
# ./test.sh -P db-on
#
# these does not require <forkCount> usage:
# ./test.sh -P staging-only
# ./test.sh -P production-only
$MVN test "$@"
