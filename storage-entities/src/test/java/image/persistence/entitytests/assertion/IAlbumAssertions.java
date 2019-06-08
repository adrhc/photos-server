package image.persistence.entitytests.assertion;

import image.persistence.entity.Album;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

public interface IAlbumAssertions {
	default void assertAlbumEquals(Album expected, Album actual) {
		assertAll("equals albums",
				() -> assertEquals(expected.getId(), actual.getId()),
				() -> assertEquals(expected.getName(), actual.getName()),
				() -> assertEquals(expected.getLastUpdate(), actual.getLastUpdate()));
	}
}
