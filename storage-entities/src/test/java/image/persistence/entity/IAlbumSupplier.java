package image.persistence.entity;

import java.util.concurrent.ThreadLocalRandom;

/**
 * Created by adr on 2/25/18.
 */
public interface IAlbumSupplier {
	default String supplyAlbumName() {
		return "album-" + ThreadLocalRandom.current().nextInt();
	}
}
