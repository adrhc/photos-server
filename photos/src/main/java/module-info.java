module adrhc.exifweb.photos {
	requires transitive adrhc.exifweb.storage.jpa2x.hbm.impl;
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
