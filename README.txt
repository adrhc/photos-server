ATENTIE, depinde de subtitles-extractor compilat non-shade!

tomcat/lib (pt a functiona aspectj - @Async):
spring-instrument-tomcat-3.2.4.RELEASE.jar

tomcat/lib (pt a functiona varianta tomcat-embedded):
mysql-connector-java-5.1.25.jar

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