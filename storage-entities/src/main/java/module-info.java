module adrhc.exifweb.storage.entities {
	requires static lombok;
	requires java.persistence;
	requires org.hibernate.orm.core;
	requires transitive adrhc.exifweb.cdm;
	exports image.persistence.entity;
	exports image.persistence.entitytests.image;
	exports image.persistence.entity.enums;
}
