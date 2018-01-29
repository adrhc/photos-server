ATENTIE, depinde de subtitles-extractor compilat non-shade!

tomcat/lib (pt a functiona aspectj - @Async):
spring-instrument-tomcat-4.2.9.RELEASE.jar

tomcat/lib (pt a functiona varianta tomcat-embedded):
mysql-connector-java-5.1.38.jar

In tomcat/conf/server.xml:
  <Host name="localhost"  appBase="webapps"
        unpackWARs="true" autoDeploy="true">

    <Context path="/exifweb" docBase="exifweb">
        <Loader loaderClass="org.springframework.instrument.classloading.tomcat.TomcatInstrumentableClassLoader" />
    </Context>

iar in tomcat/conf/context.xml:
<Context>
	...
    <Resource name="exifweb"
              auth="Container"
              type="javax.sql.DataSource"
              factory="org.apache.tomcat.jdbc.pool.DataSourceFactory"
              testWhileIdle="true"
              testOnBorrow="true"
              testOnReturn="false"
              validationQuery="SELECT 1"
              validationInterval="30000"
              timeBetweenEvictionRunsMillis="30000"
              maxActive="10"
              minIdle="2"
              maxWait="10000"
              initialSize="1"
              removeAbandonedTimeout="60"
              removeAbandoned="true"
              logAbandoned="false"
              minEvictableIdleTimeMillis="30000"
              jmxEnabled="false"
              jdbcInterceptors="org.apache.tomcat.jdbc.pool.interceptor.ConnectionState"
              username="exifweb"
              password="exifweb"
              driverClassName="com.mysql.jdbc.Driver"
              url="jdbc:mysql://192.168.1.31:3306/exifweb"/>
</Context>

timeouts: albums re/importing takes a lot of time
see also xhttpd_zld.conf
<mvc:async-support default-timeout="600000"/>
curl -X POST --cookie "JSESSIONID=01812DFA4BA8BDC00F9F4DCEF9ED6562" -H "Content-Type: application/json" -d '{"value":"2015-10-24 Botez Nataly"}' https://adrhc.go.ro/photos/app/json/action/exif

AlbumCtrl.getAllCovers: test getLastUpdatedForAlbums cache
curl -I -X GET --cookie "JSESSIONID=01812DFA4BA8BDC00F9F4DCEF9ED6562" -H "Content-Type: application/json" https://adrhc.go.ro/photos/app/json/album

AlbumCtrl.getAlbumById: test getAlbumById cache
28 = 2000-01-01 Test
curl -I -X GET --cookie "JSESSIONID=01812DFA4BA8BDC00F9F4DCEF9ED6562" -H "Content-Type: application/json" https://adrhc.go.ro/photos/app/json/album/28

AlbumCtrl.importNewAlbumsOnly: test getAlbumByName cache
curl -X POST --cookie "JSESSIONID=01812DFA4BA8BDC00F9F4DCEF9ED6562" -H "Content-Type: application/json" https://adrhc.go.ro/photos/app/json/album/importAlbums

AlbumCtrl.updateJsonForAlbum: getAlbumByName (creates cache) then AlbumService.clearDirtyForAlbum (removes cache)
curl -X POST --cookie "JSESSIONID=01812DFA4BA8BDC00F9F4DCEF9ED6562" -H "Content-Type: application/json" -d '{"value":"2012-01-01 Revelion"}' https://adrhc.go.ro/photos/app/json/album/updateJsonForAlbum
curl -X POST --cookie "JSESSIONID=01812DFA4BA8BDC00F9F4DCEF9ED6562" -H "Content-Type: application/json" -d '{"value":"2000-01-01 Test"}' https://adrhc.go.ro/photos/app/json/album/updateJsonForAlbum

ExtractExifCtrl.reImport: getAlbumByName (creates cache) then AlbumService.clearDirtyForAlbum (removes cache)
curl -X POST --cookie "JSESSIONID=01812DFA4BA8BDC00F9F4DCEF9ED6562" -H "Content-Type: application/json" -d '{"value":"2012-01-01 Revelion"}' https://adrhc.go.ro/photos/app/json/action/exif
curl -X POST --cookie "JSESSIONID=01812DFA4BA8BDC00F9F4DCEF9ED6562" -H "Content-Type: application/json" -d '{"value":"2000-01-01 Test"}' https://adrhc.go.ro/photos/app/json/action/exif
