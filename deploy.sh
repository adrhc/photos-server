#!/bin/bash
source env.sh

# rm -v ~/apps/opt/tomcat/lib/spring-instrument-tomcat-*.jar 2>/dev/null
# cp -v /home/adr/.m2/repository/org/springframework/spring-instrument-tomcat/4.2.9.RELEASE/spring-instrument-tomcat-4.2.9.RELEASE.jar ~/apps/opt/tomcat/lib

echo "deploying ..."
rm -r $HOME/apps/opt/tomcat/webapps/exifweb
cp -r target/exifweb $HOME/apps/opt/tomcat/webapps
echo "touched web.xml"
touch $HOME/apps/opt/tomcat/webapps/exifweb/WEB-INF/web.xml
