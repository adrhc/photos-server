#!/bin/bash

if [ -e "./env.sh" ]; then
	source ./env.sh
elif [ -e "../env.sh" ]; then
    source ../env.sh
fi

# ehcache static instance is overriden when using stage and production same time so we
# need forked tests when running stage and production tests in same mvn test command
#
# these require <forkCount> usage:
# ./test.sh -P db-off	-> equivalent to in-memory-only
# ./test.sh -P db-on
#
# these does not require <forkCount> usage:
# ./test.sh -P staging-only
# ./test.sh -P production-only
echo "./test.sh -P db-off|db-on|staging-only|production-only"
../clean.stage-db.sh
$MVN -e failsafe:integration-test failsafe:verify "$@"
