package image.exifweb.persistence.view;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import image.exifweb.image.ImageDimensions;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * Created with IntelliJ IDEA.
 * User: adr
 * Date: 7/12/14
 * Time: 1:02 PM
 * To change this template use File | Settings | File Templates.
 */
@Entity
@Table(name = "v_album_cover")
@JsonIgnoreProperties(ignoreUnknown = true,
		value = {"hibernateLazyInitializer", "handler", "thumbLastModified"})
//@Cacheable
//@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class AlbumCover implements ImageDimensions {
	@Id
	private Integer id;
	@Column(nullable = false, unique = true)
	private String albumName;
	@Column(nullable = false)
	private String imgName;
	@Column(nullable = false)
	private int imageHeight;
	@Column(nullable = false)
	private int imageWidth;
	@Column(name = "dirty")
	private boolean dirty;
	@Column
	private String thumbPath;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getAlbumName() {
		return albumName;
	}

	public void setAlbumName(String albumName) {
		this.albumName = albumName;
	}

	public String getThumbPath() {
		return thumbPath;
	}

	public void setThumbPath(String thumbPath) {
		this.thumbPath = thumbPath;
	}

	public int getImageHeight() {
		return imageHeight;
	}

	public void setImageHeight(int imageHeight) {
		this.imageHeight = imageHeight;
	}

	public int getImageWidth() {
		return imageWidth;
	}

	public void setImageWidth(int imageWidth) {
		this.imageWidth = imageWidth;
	}

	public boolean isDirty() {
		return dirty;
	}

	public void setDirty(boolean dirty) {
		this.dirty = dirty;
	}

	public String getImgName() {
		return imgName;
	}

	public void setImgName(String imgName) {
		this.imgName = imgName;
	}
}
