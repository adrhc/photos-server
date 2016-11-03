#!/bin/bash
source env.sh

export EXIFWEB_PORT=8080
export EXIFWEB_DIR=/home/adr/Projects/exifweb/trunk/target/exifweb
#echo "EXIFWEB_PORT = $EXIFWEB_PORT"
#echo "EXIFWEB_DIR = $EXIFWEB_DIR"
#echo "cp: $EXIFWEB_DIR/WEB-INF/classes:$EXIFWEB_DIR/WEB-INF/lib/*"
java -Dfile.encoding=UTF-8 -classpath "$EXIFWEB_DIR/WEB-INF/classes:$EXIFWEB_DIR/WEB-INF/lib/*" image.exifweb.ExifWebApp