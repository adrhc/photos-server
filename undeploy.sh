#!/bin/bash
source env.sh

echo "undeploying ..."
rm -r $HOME/apps/opt/tomcat/webapps/exifweb 2>/dev/null
rm -r $HOME/apps/opt/tomcat/work/Catalina/localhost/exifweb 2>/dev/null
echo "undeploying done"
