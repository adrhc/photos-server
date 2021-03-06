package image.cdm.image.status;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.io.Serializable;

/**
 * Created by adr on 2/27/18.
 */
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public enum ImageFlagEnum implements Serializable {
	DEFAULT(0), HIDDEN(1), PERSONAL(2), UGLY(4), DUPLICATE(8), PRINTABLE(16);

	private static final java.util.Map<java.lang.Integer, ImageFlagEnum>
			$CODE_LOOKUP = new java.util.HashMap<>();

	static {
		for (ImageFlagEnum imageStatus : ImageFlagEnum.values()) {
			$CODE_LOOKUP.put(imageStatus.value, imageStatus);
		}
	}

	@Getter
	private final int value;

	public static ImageFlagEnum findByValue(final int value) {
		if ($CODE_LOOKUP.containsKey(value)) {
			return $CODE_LOOKUP.get(value);
		}
		throw new java.lang.IllegalArgumentException(
				java.lang.String.format("Enumeration \'ImageFlagEnum\' has no value \'%s\'", value));
	}

	public byte getValueAsByte() {
		return (byte) this.value;
	}
}
