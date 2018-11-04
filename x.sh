#!/bin/bash

# export EXIFWEB_PORT=8080
# export EXIFWEB_DIR=$HOME/Projects/git.exifweb/webapp/target/exifweb
# echo "cp: $EXIFWEB_DIR/WEB-INF/classes:$EXIFWEB_DIR/WEB-INF/lib/*"
# java -Dfile.encoding=UTF-8 -classpath "$EXIFWEB_DIR/WEB-INF/classes:$EXIFWEB_DIR/WEB-INF/lib/*" image.exifweb.ExifWebApp

undeploy() {
	if [ "$1" == "deploy" ]; then
		shift
	fi
    local TOMCAT="$1"
    if [ "$TOMCAT" != "tomcat" ]; then
        TOMCAT="apache-tomcat-$TOMCAT"
    fi
    echo "removing $HOME/apps/opt/$TOMCAT/webapps/exifweb"
    rm -r $HOME/apps/opt/$TOMCAT/webapps/exifweb 2>/dev/null
    echo "removing $HOME/apps/opt/$TOMCAT/work/Catalina/localhost/exifweb"
    rm -r $HOME/apps/opt/$TOMCAT/work/Catalina/localhost/exifweb 2>/dev/null
    echo "exifweb undeployed"
}

deploy() {
	if [ "$1" == "deploy" ]; then
		shift
	fi
    local TOMCAT="$1"
    if [ "$TOMCAT" != "tomcat" ]; then
        TOMCAT="apache-tomcat-$TOMCAT"
    fi
    cp -r webapp/target/exifweb $HOME/apps/opt/$TOMCAT/webapps/
    touch $HOME/apps/opt/$TOMCAT/webapps/exifweb/WEB-INF/web.xml && echo "touched web.xml"
    echo "exifweb deployed"
}

# ./x.sh startWith "9.0.12"
startWith() {
	if [ "$1" == "startWith" ]; then
		shift
	fi
    local TOMCAT="$1"
    if [ "$TOMCAT" != "tomcat" ]; then
        TOMCAT="apache-tomcat-$TOMCAT"
    fi
	if [ "`$HOME/bin/tomcat.sh status | grep -i '[i]s running'`" != "" ]; then
        sudo systemctl stop tomcat
        echo -e "stopped systemd tomcat service\n"
    fi
    if [ "`ps aux | grep "[c]lasspath /home/adr/apps/opt/$TOMCAT/bin/bootstrap.jar"`" != "" ]; then
        /home/adr/apps/opt/$TOMCAT/bin/shutdown.sh
        while [ "`ps aux | grep "[c]lasspath /home/adr/apps/opt/$TOMCAT/bin/bootstrap.jar"`" != "" ]; do
            echo "waiting for $TOMCAT to stop"
            sleep 1
        done
    fi
    if [[ "$TOMCAT" == "9.0.12" && ! -e "$HOME/apps/opt/$TOMCAT/lib/jaxb-runtime-2.3.1.jar" ]]; then
        cp -v /home/adr/.m2/repository/javax/xml/bind/jaxb-api/2.3.1/jaxb-api-2.3.1.jar $HOME/apps/opt/$TOMCAT/lib/
        cp -v /home/adr/.m2/repository/org/glassfish/jaxb/jaxb-runtime/2.3.1/jaxb-runtime-2.3.1.jar $HOME/apps/opt/$TOMCAT/lib/
        echo -e "copied jaxb libs to $TOMCAT\n"
    fi
    rm -v $HOME/apps/opt/$TOMCAT/logs/*
    echo -e "$TOMCAT logs removed\n"
    undeploy "$1"
    deploy "$1"
    LD_LIBRARY_PATH=$HOME/apps/opt/$TOMCAT/lib $HOME/apps/opt/$TOMCAT/bin/startup.sh
    echo -e "\n$TOMCAT process details:\n"
    ps aux | grep "[c]lasspath /home/adr/apps/opt/$TOMCAT/bin/bootstrap.jar"
    echo -e "\ntailf $HOME/apps/opt/$TOMCAT/logs/catalina.out"
}

# ./x.sh stopWith "9.0.12"
stopWith() {
	if [ "$1" == "stopWith" ]; then
		shift
	fi
    local TOMCAT="$1"
    if [ "$TOMCAT" != "tomcat" ]; then
        TOMCAT="apache-tomcat-$TOMCAT"
    fi
    if [ "`ps aux | grep "[c]lasspath /home/adr/apps/opt/$TOMCAT/bin/bootstrap.jar"`" != "" ]; then
        /home/adr/apps/opt/$TOMCAT/bin/shutdown.sh
        while [ "`ps aux | grep "[c]lasspath /home/adr/apps/opt/$TOMCAT/bin/bootstrap.jar"`" != "" ]; do
            echo "waiting for $TOMCAT to stop"
            sleep 1
        done
    else
        echo "$TOMCAT is already stopped"
    fi
}

$1 "$@"
