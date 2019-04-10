#!/bin/bash
echo "removing $HOME/apps/opt/tomcat/webapps/exifweb"
rm -r $HOME/apps/opt/tomcat/webapps/exifweb 2>/dev/null
echo "removing $HOME/apps/opt/tomcat/work/Catalina/localhost/exifweb"
rm -r $HOME/apps/opt/tomcat-work/exifweb 2>/dev/null
rm -r $HOME/apps/opt/tomcat-work/Catalina/localhost/exifweb 2>/dev/null
echo "undeploying done"
