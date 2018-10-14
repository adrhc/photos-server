package image.persistence.entity.image;

import image.cdm.image.status.EImageStatus;
import image.persistence.entity.testconfig.Junit5MiscNoSpringConfig;
import net.jcip.annotations.NotThreadSafe;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

@Junit5MiscNoSpringConfig
class IImageFlagsUtilsTest implements IImageFlagsUtils {
	@Test
	void ofEImageStatus() {
		Assertions.assertEquals(of(EImageStatus.DEFAULT), new ImageFlags());
	}

	@Nested
	class OfCombinedFlagsTest implements IImageFlagsUtils {
		private byte allFlags = (byte) Arrays.stream(EImageStatus.values()).mapToInt(EImageStatus::getValue).sum();
		private byte noFlag = EImageStatus.DEFAULT.getValueAsByte();
		private byte hidden = EImageStatus.HIDDEN.getValueAsByte();
		private byte hiddenAndPersonal =
				(byte) (EImageStatus.HIDDEN.getValueAsByte() + EImageStatus.PERSONAL.getValueAsByte());

		@Test
		void of() {
			ImageFlags allFlags1 = of(this.allFlags);
			Assertions.assertAll("allFlags",
					() -> Assertions.assertTrue(allFlags1.isDuplicate()),
					() -> Assertions.assertTrue(allFlags1.isHidden()),
					() -> Assertions.assertTrue(allFlags1.isPersonal()),
					() -> Assertions.assertTrue(allFlags1.isPrintable()),
					() -> Assertions.assertTrue(allFlags1.isUgly())
			);
			ImageFlags noFlag1 = of(this.noFlag);
			Assertions.assertAll("noFlag",
					() -> Assertions.assertFalse(noFlag1.isDuplicate()),
					() -> Assertions.assertFalse(noFlag1.isHidden()),
					() -> Assertions.assertFalse(noFlag1.isPersonal()),
					() -> Assertions.assertFalse(noFlag1.isPrintable()),
					() -> Assertions.assertFalse(noFlag1.isUgly())
			);
			ImageFlags hidden1 = of(this.hidden);
			Assertions.assertAll("hidden",
					() -> Assertions.assertFalse(hidden1.isDuplicate()),
					() -> Assertions.assertTrue(hidden1.isHidden()),
					() -> Assertions.assertFalse(hidden1.isPersonal()),
					() -> Assertions.assertFalse(hidden1.isPrintable()),
					() -> Assertions.assertFalse(hidden1.isUgly())
			);
			ImageFlags hiddenAndPersonal1 = of(this.hiddenAndPersonal);
			Assertions.assertAll("hiddenAndPersonal",
					() -> Assertions.assertFalse(hiddenAndPersonal1.isDuplicate()),
					() -> Assertions.assertTrue(hiddenAndPersonal1.isHidden()),
					() -> Assertions.assertTrue(hiddenAndPersonal1.isPersonal()),
					() -> Assertions.assertFalse(hiddenAndPersonal1.isPrintable()),
					() -> Assertions.assertFalse(hiddenAndPersonal1.isUgly())
			);

		}
	}

	@Nested
	@NotThreadSafe
	class AreEqualsTest implements IImageFlagsUtils {
		private ImageFlags imageFlags;

		@BeforeEach
		void beforeEach() {
			this.imageFlags = new ImageFlags();
		}

		@Test
		void areEqualsDUPLICATE() {
			this.imageFlags.setHidden(true);
			areEquals(this.imageFlags, EImageStatus.DUPLICATE);
		}

		@Test
		void areEqualsHIDDEN() {
			this.imageFlags.setHidden(true);
			areEquals(this.imageFlags, EImageStatus.HIDDEN);
		}

		@Test
		void areEqualsPERSONAL() {
			this.imageFlags.setHidden(true);
			areEquals(this.imageFlags, EImageStatus.PERSONAL);
		}

		@Test
		void areEqualsPRINTABLE() {
			this.imageFlags.setHidden(true);
			areEquals(this.imageFlags, EImageStatus.PRINTABLE);
		}

		@Test
		void areEqualsUGLY() {
			this.imageFlags.setHidden(true);
			areEquals(this.imageFlags, EImageStatus.UGLY);
		}
	}
}
