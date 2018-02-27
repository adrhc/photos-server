package image.cdm.image;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * Created with IntelliJ IDEA.
 * User: adr
 * Date: 11/24/13
 * Time: 10:51 PM
 * To change this template use File | Settings | File Templates.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ImageStatus implements Serializable {
	private Integer id;
	private EImageStatus status;

	public Integer getId() {
		return this.id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public EImageStatus getStatus() {
		return this.status;
	}

	public void setStatus(EImageStatus status) {
		this.status = status;
	}
}
