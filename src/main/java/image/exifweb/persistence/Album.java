package image.exifweb.persistence;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import image.exifweb.persistence.view.AlbumCover;
import org.hibernate.annotations.Cascade;

import javax.persistence.*;
import java.io.Serializable;
import java.sql.Timestamp;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: adrian.petre
 * Date: 11/8/13
 * Time: 1:03 PM
 * To change this template use File | Settings | File Templates.
 */
@Entity
@JsonIgnoreProperties(value = {"hibernateLazyInitializer", "handler"}, ignoreUnknown = true)
@JsonIdentityInfo(generator = ObjectIdGenerators.UUIDGenerator.class, scope = Album.class)
public class Album implements Serializable {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;
	@Column(nullable = false, unique = true, length = 512)
	private String name;
	@Column(name = "dirty")
	private boolean dirty;
	@OneToMany(mappedBy = "album", orphanRemoval = true)
	@Cascade({org.hibernate.annotations.CascadeType.ALL})
	private List<Image> images;
	@OneToOne
	@JoinColumn(name = "FK_IMAGE")
	private Image cover;
	@JsonIgnore
	@Version
	@Column(name = "last_update")
	private Timestamp lastUpdate;
	@Column(nullable = false)
	private boolean deleted;

	public Album() {
	}

	public Album(AlbumCover albumCover) {
		this.id = albumCover.getId();
		this.name = albumCover.getAlbumName();
		this.dirty = albumCover.isDirty();
	}

	public Album(String name) {
		this.name = name;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Image getCover() {
		return cover;
	}

	public void setCover(Image cover) {
		this.cover = cover;
	}

	public List<Image> getImages() {
		return this.images;
	}

	public void setImages(List<Image> images) {
		this.images = images;
	}

	public boolean getDirty() {
		return dirty;
	}

	public void setDirty(boolean dirty) {
		this.dirty = dirty;
	}

	public Timestamp getLastUpdate() {
		return lastUpdate;
	}

	/**
	 * Atentie, e o coloana @Version! Nu o seta niciodata ca o seteaza hibernate!
	 *
	 * @param lastUpdate
	 */
	public void setLastUpdate(Timestamp lastUpdate) {
		this.lastUpdate = lastUpdate;
	}

	public boolean isDeleted() {
		return deleted;
	}

	public void setDeleted(boolean deleted) {
		this.deleted = deleted;
	}
}
