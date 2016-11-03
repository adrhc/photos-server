#!/bin/bash
source env.sh

cp -v /home/adr/.m2/repository/org/springframework/spring-instrument-tomcat/4.2.6.RELEASE/spring-instrument-tomcat-4.2.6.RELEASE.jar ~/apps/opt/apache-tomcat-7.0.64/lib
# cp -v /home/adr/.m2/repository/org/springframework/spring-instrument-tomcat/4.0.6.RELEASE/spring-instrument-tomcat-4.0.6.RELEASE.jar ~/apps/opt/apache-tomcat-7.0.64/lib
rm -v ~/apps/opt/apache-tomcat-7.0.64/lib/spring-instrument-tomcat-4.0.6.RELEASE.jar 2>/dev/null

rm -r /home/adr/apps/opt/apache-tomcat-7.0.64/webapps/exifweb
cp -r target/exifweb /home/adr/apps/opt/apache-tomcat-7.0.64/webapps/

