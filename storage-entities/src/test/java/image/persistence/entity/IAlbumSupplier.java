package image.persistence.entity;

import image.persistence.util.IPositiveRandom;

/**
 * Created by adr on 2/25/18.
 */
public interface IAlbumSupplier extends IPositiveRandom {
	default String supplyAlbumName() {
		return "album-" + positiveRandom();
	}
}
