module adrhc.exifweb.cdm {
	requires static lombok;
	requires com.fasterxml.jackson.annotation;
	exports image.cdm;
	exports image.cdm.image.status;
	exports image.cdm.image;
}
