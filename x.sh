#!/bin/bash

# export EXIFWEB_PORT=8080
# export EXIFWEB_DIR=$HOME/Projects/git.exifweb/webapp/target/exifweb
# echo "cp: $EXIFWEB_DIR/WEB-INF/classes:$EXIFWEB_DIR/WEB-INF/lib/*"
# java -Dfile.encoding=UTF-8 -classpath "$EXIFWEB_DIR/WEB-INF/classes:$EXIFWEB_DIR/WEB-INF/lib/*" image.exifweb.ExifWebApp

TOMCAT_STOP_TIME=5

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
        sleep $TOMCAT_STOP_TIME
        if [ "`ps aux | grep "[c]lasspath /home/adr/apps/opt/$TOMCAT/bin/bootstrap.jar"`" != "" ]; then
            echo -e "stopped $TOMCAT\n"
        else
            echo -e "can't stop $TOMCAT\n"
            exit 1
        fi
    fi
    if [[ "$TOMCAT" == "9.0.12" && ! -e "$HOME/apps/opt/$TOMCAT/lib/jaxb-runtime-2.3.1.jar" ]]; then
        cp -v /home/adr/.m2/repository/javax/xml/bind/jaxb-api/2.3.1/jaxb-api-2.3.1.jar $HOME/apps/opt/$TOMCAT/lib/
        cp -v /home/adr/.m2/repository/org/glassfish/jaxb/jaxb-runtime/2.3.1/jaxb-runtime-2.3.1.jar $HOME/apps/opt/$TOMCAT/lib/
        echo -e "copied jaxb libs to $TOMCAT\n"
    fi
    rm -v $HOME/apps/opt/$TOMCAT/logs/*
    echo -e "$TOMCAT logs removed\n"
    ./deploy.sh
    echo -e "exifweb application deployed\n"
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
        sleep $TOMCAT_STOP_TIME
        if [ "`ps aux | grep "[c]lasspath /home/adr/apps/opt/$TOMCAT/bin/bootstrap.jar"`" != "" ]; then
            echo -e "stopped $TOMCAT\n"
        else
            echo -e "can't stop $TOMCAT\n"
            exit 1
        fi
    else
        echo "$TOMCAT is already stopped"
    fi
}

$1 "$@"
