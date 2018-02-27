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
	private Integer id;
	private Byte rating;

	public Integer getId() {
		return this.id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Byte getRating() {
		return this.rating;
	}

	public void setRating(Byte status) {
		this.rating = status;
	}
}
