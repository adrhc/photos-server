#!/bin/bash
source env.sh

# ./test-report-only.sh -P db-off
# ./test-report-only.sh -P db-on

$MVN surefire-report:report-only
