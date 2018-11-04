module adrhc.exifweb.storage.impl.hbm {
	requires transitive adrhc.exifweb.storage.api;
	requires spring.context;
	requires spring.beans;
	requires spring.tx;
	requires spring.orm;
//	requires java.persistence;
//	requires org.hibernate.orm.core;
	requires hibernate.core;
	requires java.sql;
	requires spring.core;
	requires javax.inject;
	requires slf4j.api;
	requires spring.jdbc;
	requires com.zaxxer.hikari;
	requires java.naming;
	requires spring.context.support;
	exports image.hbm;
	exports image.hbm.repository;
}
