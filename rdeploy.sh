#!/bin/bash

# rm -v ~/apps/opt/tomcat/lib/spring-instrument-tomcat-*.jar 2>/dev/null
# cp -v /home/adr/.m2/repository/org/springframework/spring-instrument-tomcat/4.2.9.RELEASE/spring-instrument-tomcat-4.2.9.RELEASE.jar ~/apps/opt/tomcat/lib

# rm -r /home/adr/apps/opt/tomcat/webapps/exifweb
# cp -r webapp/target/exifweb /home/adr/apps/opt/tomcat/webapps

RSYNCB="rsync -cEhikLmrtz"
$RSYNCB --delete-after webapp/target/exifweb -e "ssh" adr@adrhc.go.ro:/home/adr/apps/opt/tomcat/webapps/
# $RSYNCB --delete-after webapp/target/exifweb -e "ssh -p 443" adr@adrhc.go.ro:/home/adr/apps/opt/tomcat/webapps/
