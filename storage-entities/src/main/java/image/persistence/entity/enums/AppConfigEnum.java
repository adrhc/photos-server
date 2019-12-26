package image.persistence.entity.enums;

import lombok.Getter;

import java.io.Serializable;

/**
 * Created with IntelliJ IDEA.
 * User: adrian.petre
 * Date: 11/8/13
 * Time: 5:30 PM
 * To change this template use File | Settings | File Templates.
 */
@Getter
public enum AppConfigEnum implements Serializable {
	albums_path("albums_path"),
	photos_per_page("photos_per_page"),
	photos_json_FS_path("photos json FS path");

	/**
	 * corresponds to the DB value
	 */
	private String value;

	AppConfigEnum(String value) {
		this.value = value;
	}
}
