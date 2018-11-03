#!/bin/bash

# rm -v ~/apps/opt/tomcat/lib/spring-instrument-tomcat-*.jar 2>/dev/null
# cp -v /home/adr/.m2/repository/org/springframework/spring-instrument-tomcat/4.2.9.RELEASE/spring-instrument-tomcat-4.2.9.RELEASE.jar ~/apps/opt/tomcat/lib

./undeploy.sh
# mkdir -p $HOME/apps/opt/tomcat/webapps/exifweb
cp -r webapp/target/exifweb $HOME/apps/opt/tomcat/webapps/
touch $HOME/apps/opt/tomcat/webapps/exifweb/WEB-INF/web.xml && echo "touched web.xml"
