#!/bin/bash
source ../env.sh

../clean.db.sh
$MVN test "$@"
