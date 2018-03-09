package image.persistence.entity;

import image.persistence.repository.util.random.IEnhancedRandom;
import image.persistence.util.IPositiveIntegerRandom;

/**
 * Created by adr on 2/25/18.
 */
public interface IAlbumSupplier extends IEnhancedRandom, IPositiveIntegerRandom {
	default String supplyAlbumName() {
		return "album-" + randomPositiveInt();
	}
}
