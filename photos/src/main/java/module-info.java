module adrhc.exifweb.photos {
	requires transitive adrhc.exifweb.storage.jpa2x.hbm.impl;
	requires adrhc.exifweb.util;
	requires spring.core;
	requires spring.context;
	requires javax.inject;
	requires slf4j.api;
	requires com.fasterxml.jackson.databind;
	requires io.reactivex.rxjava2;
	requires java.annotation;
	requires commons.beanutils;
	requires spring.beans;
	requires metadata.extractor;
	requires org.apache.commons.io;
	requires com.fasterxml.jackson.datatype.hibernate5;
	requires com.fasterxml.jackson.core;
	exports image.photos;
	exports image.photos.album;
	exports image.photos.config;
	exports image.photos.events.album;
	exports image.photos.events.image;
	exports image.photos.image;
	exports image.photos.util;
	exports image.photos.util.conversion;
	exports image.photos.util.process;
	exports image.photos.util.status;
}
