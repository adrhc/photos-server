#!/bin/bash

if [ -e "env.sh" ]; then
	source env.sh
elif [ -e mvnw ]; then
    source ../env.sh
fi

# rm -v ~/apps/opt/tomcat/lib/spring-instrument-tomcat-*.jar 2>/dev/null
# cp -v /home/adr/.m2/repository/org/springframework/spring-instrument-tomcat/4.2.9.RELEASE/spring-instrument-tomcat-4.2.9.RELEASE.jar ~/apps/opt/tomcat/lib

./undeploy.sh
if [ "$TARGET_WEBAPP" == "" ]; then
    cp -r webapp/target/exifweb $HOME/apps/opt/tomcat/webapps/
else
    mv -v $TARGET_WEBAPP/exifweb $HOME/apps/opt/tomcat/webapps/
fi
touch $HOME/apps/opt/tomcat/webapps/exifweb/WEB-INF/web.xml && echo "touched web.xml"
