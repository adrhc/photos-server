package image.persistence.entitytests.image;

import image.cdm.image.status.EImageStatus;

public interface IImageFlagsUtils {
	default boolean areEquals(ImageFlags imageFlags, EImageStatus eImageStatus) {
		switch (eImageStatus) {
			case DEFAULT:
				return !imageFlags.isDuplicate() && !imageFlags.isHidden() &&
						!imageFlags.isPersonal() && !imageFlags.isPrintable() && !imageFlags.isUgly();
			case DUPLICATE:
				return imageFlags.isDuplicate() && !imageFlags.isHidden() &&
						!imageFlags.isPersonal() && !imageFlags.isPrintable() && !imageFlags.isUgly();
			case HIDDEN:
				return !imageFlags.isDuplicate() && imageFlags.isHidden() &&
						!imageFlags.isPersonal() && !imageFlags.isPrintable() && !imageFlags.isUgly();
			case PERSONAL:
				return !imageFlags.isDuplicate() && !imageFlags.isHidden() &&
						imageFlags.isPersonal() && !imageFlags.isPrintable() && !imageFlags.isUgly();
			case PRINTABLE:
				return !imageFlags.isDuplicate() && !imageFlags.isHidden() &&
						!imageFlags.isPersonal() && imageFlags.isPrintable() && !imageFlags.isUgly();
			case UGLY:
				return !imageFlags.isDuplicate() && !imageFlags.isHidden() &&
						!imageFlags.isPersonal() && !imageFlags.isPrintable() && imageFlags.isUgly();
			default:
				throw new UnsupportedOperationException();
		}
	}

	default ImageFlags of(EImageStatus eImageStatus) {
		ImageFlags imageFlags = new ImageFlags();
		switch (eImageStatus) {
			case DEFAULT:
				break;
			case DUPLICATE:
				imageFlags.setDuplicate(true);
				break;
			case HIDDEN:
				imageFlags.setHidden(true);
				break;
			case PERSONAL:
				imageFlags.setPersonal(true);
				break;
			case PRINTABLE:
				imageFlags.setPrintable(true);
				break;
			case UGLY:
				imageFlags.setUgly(true);
				break;
			default:
				throw new UnsupportedOperationException();
		}
		return imageFlags;
	}

	default ImageFlags of(byte combinedFlags) {
		ImageFlags imageFlags = new ImageFlags();
		imageFlags.setDuplicate((combinedFlags &
				EImageStatus.DUPLICATE.getValue()) == EImageStatus.DUPLICATE.getValue());
		imageFlags.setHidden((combinedFlags &
				EImageStatus.HIDDEN.getValue()) == EImageStatus.HIDDEN.getValue());
		imageFlags.setPersonal((combinedFlags &
				EImageStatus.PERSONAL.getValue()) == EImageStatus.PERSONAL.getValue());
		imageFlags.setPrintable((combinedFlags &
				EImageStatus.PRINTABLE.getValue()) == EImageStatus.PRINTABLE.getValue());
		imageFlags.setUgly((combinedFlags &
				EImageStatus.UGLY.getValue()) == EImageStatus.UGLY.getValue());
		return imageFlags;
	}
}
