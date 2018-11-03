module adrhc.exifweb.storage.entities {
	requires static lombok;
	requires java.persistence;
	requires org.hibernate.orm.core;
	requires adrhc.exifweb.cdm;
	exports image.persistence.entity;
}
