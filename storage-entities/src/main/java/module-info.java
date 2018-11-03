module adrhc.exifweb.storage.entities {
	requires lombok;
	requires adrhc.exifweb.cdm;
	requires java.persistence;
	requires org.hibernate.orm.core;
	exports image.persistence.entity;
}
