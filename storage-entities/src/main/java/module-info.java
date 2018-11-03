module adrhc.exifweb.storage.entities {
	requires lombok;
	requires hibernate.core;
	requires hibernate.jpa;
	requires adrhc.exifweb.cdm;
	exports image.persistence.entity;
}
