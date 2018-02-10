#!/bin/bash
source env.sh

echo "undeploying ..."
rm -r $HOME/apps/opt/tomcat/webapps/exifweb
