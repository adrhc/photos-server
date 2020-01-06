package image.persistence.entity.image;

import image.cdm.image.status.ImageFlagEnum;

public interface IImageFlagsUtils {
	default boolean areEquals(ImageFlags imageFlags, ImageFlagEnum imageFlagEnum) {
		switch (imageFlagEnum) {
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

	default ImageFlags of(ImageFlagEnum imageFlagEnum) {
		ImageFlags imageFlags = new ImageFlags();
		switch (imageFlagEnum) {
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
				ImageFlagEnum.DUPLICATE.getValue()) == ImageFlagEnum.DUPLICATE.getValue());
		imageFlags.setHidden((combinedFlags &
				ImageFlagEnum.HIDDEN.getValue()) == ImageFlagEnum.HIDDEN.getValue());
		imageFlags.setPersonal((combinedFlags &
				ImageFlagEnum.PERSONAL.getValue()) == ImageFlagEnum.PERSONAL.getValue());
		imageFlags.setPrintable((combinedFlags &
				ImageFlagEnum.PRINTABLE.getValue()) == ImageFlagEnum.PRINTABLE.getValue());
		imageFlags.setUgly((combinedFlags &
				ImageFlagEnum.UGLY.getValue()) == ImageFlagEnum.UGLY.getValue());
		return imageFlags;
	}
}
