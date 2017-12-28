#!/bin/bash
source env.sh

mvn -Dmaven.test.skip=true -Dmaven.javadoc.skip=true clean install
