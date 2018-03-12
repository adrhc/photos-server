package image.cdm.image;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * Created with IntelliJ IDEA.
 * User: adr
 * Date: 7/20/14
 * Time: 9:58 PM
 * To change this template use File | Settings | File Templates.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ImageRating implements Serializable {
	public static final byte MIN_RATING = 1;

	private Integer imageId;
	private byte rating;

	public Integer getImageId() {
		return this.imageId;
	}

	public void setImageId(Integer imageId) {
		this.imageId = imageId;
	}

	public byte getRating() {
		return this.rating;
	}

	public void setRating(byte status) {
		this.rating = status;
	}
}
