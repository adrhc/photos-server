#!/bin/bash
source env.sh

$MVN surefire-report:report-only
