#!/bin/bash
source ../env.sh

# $MVN test
$MVN surefire-report:report "$@"
