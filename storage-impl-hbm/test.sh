#!/bin/bash
source ../env.sh

../clean.stage-db.sh
$MVN test "$@"
