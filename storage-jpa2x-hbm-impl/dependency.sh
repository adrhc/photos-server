#!/bin/bash
source ../env.sh

$MVN "$@" dependency:tree
