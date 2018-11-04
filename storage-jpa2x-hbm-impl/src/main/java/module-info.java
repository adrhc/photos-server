module adrhc.exifweb.storage.jpa2x.hbm.impl {
	requires transitive adrhc.exifweb.storage.api;
	requires java.persistence;
	requires org.hibernate.orm.core;
	requires spring.data.commons;
	requires spring.data.jpa;
	requires spring.core;
	requires spring.tx;
	requires spring.context;
	requires spring.orm;
	requires spring.beans;
	requires org.apache.commons.lang3;
	requires javax.inject;
	requires static lombok;
	requires adrhc.exifweb.storage.impl.hbm;
	requires java.sql;
	exports image.jpa2x;
	exports image.jpa2x.jpacustomizations;
	exports image.jpa2x.repositories;
	exports image.jpa2x.services;
}
