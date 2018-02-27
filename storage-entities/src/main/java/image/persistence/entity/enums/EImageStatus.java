package image.persistence.entity.enums;

/**
 * Created by adr on 2/27/18.
 */
public enum EImageStatus {
	DEFAULT(0), HIDDEN(1), PERSONAL(2), UGLY(4), DUPLICATE(8), PRINTABLE(16);

	private byte value;

	EImageStatus(int value) {
		this.value = (byte) value;
	}

	public byte getValue() {
		return this.value;
	}
}
