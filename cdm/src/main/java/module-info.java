module adrhc.exifweb.cdm {
	requires static lombok;
	requires com.fasterxml.jackson.annotation;
	exports image.cdm;
	exports image.cdm.image;
	exports image.cdm.album.cover;
	exports image.cdm.album.page;
	exports image.cdm.image.feature;
	exports image.cdm.image.status;
}
