package image.exifweb.image;

import java.io.Serializable;

/**
 * Created with IntelliJ IDEA.
 * User: adr
 * Date: 7/20/14
 * Time: 9:58 PM
 * To change this template use File | Settings | File Templates.
 */
public class ImageRating implements Serializable {
	private Integer id;
	private Byte rating;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Byte getRating() {
		return rating;
	}

	public void setRating(Byte status) {
		this.rating = status;
	}
}
