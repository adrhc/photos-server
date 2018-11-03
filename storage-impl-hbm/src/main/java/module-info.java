module adrhc.exifweb.storage.impl.hbm {
	requires transitive adrhc.exifweb.storage.api;
	requires java.xml;
	requires spring.context;
	requires spring.beans;
	requires spring.tx;
	requires spring.orm;
	requires org.hibernate.orm.core;
	requires java.sql;
	requires spring.core;
	requires javax.inject;
	requires slf4j.api;
	requires spring.jdbc;
	requires com.zaxxer.hikari;
	requires java.persistence;
	requires java.naming;
	requires spring.context.support;
	exports image.hbm;
	exports image.hbm.repository;
}
