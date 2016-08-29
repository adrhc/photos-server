set "M2_REPO=%C:\maven.repository.3x%"
set "APP_ROOT=C:\Adr\exifweb-31\trunk\target"
set "EXIFWEB_PORT=8080"
set "EXIFWEB_DIR=C:\Adr\exifweb-31\trunk\target\exifweb"

@REM C:\jdk1.6.0_33\bin\java -agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=5005 -Dfile.encoding=UTF-8 -classpath "%APP_ROOT%\classes;%APP_ROOT%\exifweb\WEB-INF\lib\*" image.exifweb.ExifWebApp

C:\jdk1.6.0_33\bin\java -Dfile.encoding=UTF-8 -classpath "%APP_ROOT%\classes;%APP_ROOT%\exifweb\WEB-INF\lib\*" image.exifweb.ExifWebApp

pause