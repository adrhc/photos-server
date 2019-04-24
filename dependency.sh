#!/bin/bash
source env.sh
$MVN -e dependency:tree
