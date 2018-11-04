module adrhc.exifweb.storage.entities {
	requires static lombok;
	requires transitive adrhc.exifweb.cdm;
//	requires java.persistence;
//	requires org.hibernate.orm.core;
	requires hibernate.core;
	requires hibernate.jpa;
	exports image.persistence.entity;
	exports image.persistence.entity.image;
	exports image.persistence.entity.enums;
}
