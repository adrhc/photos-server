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

28 = 2000-01-01 Test
57 = 2017-07-01 Family

CacheStatisticsCtrl.printCacheStatistics
((CacheStatisticsCtrl)ajc$this).sessionFactory.getStatistics().getCollectionRoleNames();
((CacheStatisticsCtrl)ajc$this).sessionFactory.getStatistics().getCollectionStatistics("image.exifweb.persistence.Album.images");
((CacheStatisticsCtrl)ajc$this).sessionFactory.getStatistics().getSecondLevelCacheStatistics("Album");
((CacheStatisticsCtrl)ajc$this).sessionFactory.getStatistics().getEntityStatistics("image.exifweb.persistence.Album");
((CacheStatisticsCtrl)ajc$this).sessionFactory.getStatistics().getNaturalIdCacheStatistics("Album");
((CacheStatisticsCtrl)ajc$this).sessionFactory.getStatistics().getQueries();

CacheStatisticsCtrl.printCacheStatistics
curl -I -X GET --cookie "JSESSIONID=0481FA4C3CB4C27C29C28D56AC8EC500" -H "Content-Type: application/json" https://adrhc.go.ro/photos/app/json/cacheStat

AlbumCtrl.getAlbumById:
curl -I -X GET --cookie "JSESSIONID=0481FA4C3CB4C27C29C28D56AC8EC500" -H "Content-Type: application/json" https://adrhc.go.ro/photos/app/json/album/28
curl -I -X GET --cookie "JSESSIONID=0481FA4C3CB4C27C29C28D56AC8EC500" -H "Content-Type: application/json" https://adrhc.go.ro/photos/app/json/album/57

AlbumCtrl.getAlbumByName:
curl -I -X GET --cookie "JSESSIONID=0481FA4C3CB4C27C29C28D56AC8EC500" -H "Content-Type: application/json" 'https://adrhc.go.ro/photos/app/json/album/byName/2000-01-01 Test'
curl -I -X GET --cookie "JSESSIONID=0481FA4C3CB4C27C29C28D56AC8EC500" -H "Content-Type: application/json" 'https://adrhc.go.ro/photos/app/json/album/byName/2017-07-01 Family'

19200 = 2017-07-01 Family/IMG_1225.JPG -> cover
19207 = 2017-07-01 Family/IMG_1226.JPG
ImageCtrl.getById
curl -I -X GET --cookie "JSESSIONID=0481FA4C3CB4C27C29C28D56AC8EC500" -H "Content-Type: application/json" 'https://adrhc.go.ro/photos/app/json/image/19200'
curl -I -X GET --cookie "JSESSIONID=0481FA4C3CB4C27C29C28D56AC8EC500" -H "Content-Type: application/json" 'https://adrhc.go.ro/photos/app/json/image/19207'

timeouts: albums re/importing takes a lot of time
see also xhttpd_zld.conf
<mvc:async-support default-timeout="600000"/>
curl -X POST --cookie "JSESSIONID=0481FA4C3CB4C27C29C28D56AC8EC500" -H "Content-Type: application/json" -d '{"value":"2015-10-24 Botez Nataly"}' https://adrhc.go.ro/photos/app/json/action/exif

AlbumCtrl.getAllCovers: test getLastUpdatedForAlbums cache
curl -I -X GET --cookie "JSESSIONID=0481FA4C3CB4C27C29C28D56AC8EC500" -H "Content-Type: application/json" https://adrhc.go.ro/photos/app/json/album

AlbumCtrl.importNewAlbumsOnly: test getAlbumByName cache
curl -X POST --cookie "JSESSIONID=0481FA4C3CB4C27C29C28D56AC8EC500" -H "Content-Type: application/json" https://adrhc.go.ro/photos/app/json/album/importAlbums

AlbumCtrl.updateJsonForAlbum: getAlbumByName (creates cache) then AlbumService.clearDirtyForAlbum (removes cache)
curl -X POST --cookie "JSESSIONID=0481FA4C3CB4C27C29C28D56AC8EC500" -H "Content-Type: application/json" -d '{"value":"2017-07-01 Family"}' https://adrhc.go.ro/photos/app/json/album/updateJsonForAlbum
curl -X POST --cookie "JSESSIONID=0481FA4C3CB4C27C29C28D56AC8EC500" -H "Content-Type: application/json" -d '{"value":"2000-01-01 Test"}' https://adrhc.go.ro/photos/app/json/album/updateJsonForAlbum

ExtractExifCtrl.reImport: getAlbumByName (creates cache) then AlbumService.clearDirtyForAlbum (removes cache)
curl -X POST --cookie "JSESSIONID=0481FA4C3CB4C27C29C28D56AC8EC500" -H "Content-Type: application/json" -d '{"value":"2012-01-01 Revelion"}' https://adrhc.go.ro/photos/app/json/action/exif
curl -X POST --cookie "JSESSIONID=0481FA4C3CB4C27C29C28D56AC8EC500" -H "Content-Type: application/json" -d '{"value":"2000-01-01 Test"}' https://adrhc.go.ro/photos/app/json/action/exif

Image table trigger:
CREATE DEFINER=`exifweb`@`%` TRIGGER `ALBUM_DIRTY_ON_RATING` AFTER UPDATE ON `Image` FOR EACH ROW UPDATE Album SET dirty = 1, last_update = NOW() WHERE id = NEW.FK_ALBUM AND (NEW.rating != OLD.rating OR NEW.status != OLD.status)

java compiler/player/runner
https://repl.it/@osteele/SimpleDateFormat-milliseconds

TIMESTAMP(3) supports milliseconds

SELECT now(6), DATE_FORMAT(now(6), '%H:%i:%S.%f');
ALTER TABLE Album ADD COLUMN `last_update1` TIMESTAMP(3) NOT NULL DEFAULT now(3) AFTER `last_update`;
UPDATE Album SET last_update1 = last_update;
-- DROP INDEX `idx_album_last_update` ON `exifweb`.`Album`;
-- ALTER TABLE `exifweb`.`Album` DROP COLUMN `last_update`;
ALTER TABLE Album CHANGE COLUMN `last_update1` `last_update` TIMESTAMP(3) NOT NULL DEFAULT now(3);

ALTER TABLE Image ADD COLUMN `last_update1` TIMESTAMP(3) NOT NULL DEFAULT now(3) AFTER `last_update`;
UPDATE Image SET last_update1 = last_update;
-- ALTER TABLE Image DROP COLUMN `last_update`;
ALTER TABLE Image CHANGE COLUMN `last_update1` `last_update` TIMESTAMP(3) NOT NULL DEFAULT now(3);

ALTER TABLE AppConfig ADD COLUMN `last_update1` TIMESTAMP(3) NOT NULL DEFAULT now(3) AFTER `last_update`;
UPDATE AppConfig SET last_update1 = last_update;
-- ALTER TABLE AppConfig DROP COLUMN `last_update`;
ALTER TABLE AppConfig CHANGE COLUMN `last_update1` `last_update` TIMESTAMP(3) NOT NULL DEFAULT now(3);
