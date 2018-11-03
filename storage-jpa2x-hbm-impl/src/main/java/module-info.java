module adrhc.exifweb.storage.jpa2x.hbm.impl {
	requires transitive adrhc.exifweb.storage.api;
	requires spring.data.commons;
	requires spring.data.jpa;
	requires java.persistence;
	requires org.hibernate.orm.core;
	requires spring.core;
	requires spring.tx;
	requires org.apache.commons.lang3;
	requires spring.context;
	requires javax.inject;
	requires lombok;
	requires adrhc.exifweb.storage.impl.hbm;
	requires spring.orm;
	requires java.sql;
	requires spring.beans;
	exports image.jpa2x;
	exports image.jpa2x.jpacustomizations;
	exports image.jpa2x.repositories;
	exports image.jpa2x.services;
}
