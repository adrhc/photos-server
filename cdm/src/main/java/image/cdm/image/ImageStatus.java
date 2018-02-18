package image.cdm.image;

import java.io.Serializable;

/**
 * Created with IntelliJ IDEA.
 * User: adr
 * Date: 11/24/13
 * Time: 10:51 PM
 * To change this template use File | Settings | File Templates.
 */
public class ImageStatus implements Serializable {
	private Integer id;
	private Byte status;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Byte getStatus() {
		return status;
	}

	public void setStatus(Byte status) {
		this.status = status;
	}
}
