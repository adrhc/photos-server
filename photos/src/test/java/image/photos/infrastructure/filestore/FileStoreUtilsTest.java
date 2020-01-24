package image.photos.infrastructure.filestore;

import org.junit.jupiter.api.Test;

import static image.photos.infrastructure.filestore.FileStoreUtils.changeToOppositeExtensionCase;
import static org.junit.jupiter.api.Assertions.assertEquals;

class FileStoreUtilsTest {
	@Test
	void changeToOppositeExtensionCaseTest() {
		assertEquals("x.Y", changeToOppositeExtensionCase("x.y"));
		assertEquals(".Y", changeToOppositeExtensionCase(".y"));
		assertEquals("x", changeToOppositeExtensionCase("x"));
	}
}
