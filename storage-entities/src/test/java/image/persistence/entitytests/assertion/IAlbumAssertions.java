package image.persistence.entitytests.assertion;

import image.persistence.entity.Album;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

public interface IAlbumAssertions {
	default void assertAlbumEquals(Album album, Album dbAlbum) {
		assertAll("equals albums",
				() -> assertEquals(album.getId(), dbAlbum.getId()),
				() -> assertEquals(album.getName(), dbAlbum.getName()),
				() -> assertEquals(album.getLastUpdate(), dbAlbum.getLastUpdate()));
	}
}
