package image.exifweb.persistence.enums;

/**
 * Created with IntelliJ IDEA.
 * User: adrian.petre
 * Date: 11/8/13
 * Time: 5:30 PM
 * To change this template use File | Settings | File Templates.
 */
public enum AppConfigEnum {
	LINUX_ALBUMS_PATH(1), PHOTOS_PER_PAGE(2);
	private Integer value;

	AppConfigEnum(Integer dbIdAppConfig) {
		this.value = dbIdAppConfig;
	}

	public Integer getValue() {
		return value;
	}
}
