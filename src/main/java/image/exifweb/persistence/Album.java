package image.exifweb.persistence;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import image.exifweb.persistence.view.AlbumCover;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Cascade;
import org.springframework.cache.annotation.CacheEvict;

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
@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region = "Album")
public class Album implements Serializable {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;
	@Column(nullable = false, unique = true, length = 512)
	private String name;
	/**
	 * Means some images are changed while album's
	 * json file-pages are not regenerated yet.
	 * <p>
	 * The album cover is marked specially for dirty albums.
	 */
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

	@CacheEvict(value = "covers", allEntries = true)
	public void setName(String name) {
		this.name = name;
	}

	public Image getCover() {
		return cover;
	}

	@CacheEvict(value = "covers", allEntries = true)
	public void setCover(Image cover) {
		this.cover = cover;
	}

	public boolean isDirty() {
		return dirty;
	}

	@CacheEvict(value = "covers", allEntries = true)
	public void setDirty(boolean dirty) {
		this.dirty = dirty;
	}

	public boolean isDeleted() {
		return deleted;
	}

	@CacheEvict(value = "covers", allEntries = true)
	public void setDeleted(boolean deleted) {
		this.deleted = deleted;
	}

	public Timestamp getLastUpdate() {
		return lastUpdate;
	}

	/**
	 * Atentie, e o coloana @Version! Nu o seta niciodata pt ca o seteaza hibernate!
	 * Dpv al cache-ului o eventuala modificare de lastUpdate e datorata
	 * unei modificari a altei proprietati care ar afecta cache la randu-i.
	 *
	 * @param lastUpdate
	 */
	public void setLastUpdate(Timestamp lastUpdate) {
		this.lastUpdate = lastUpdate;
	}

	public List<Image> getImages() {
		return this.images;
	}

	/**
	 * Nu intra in zona de cache a Album.
	 *
	 * @param images
	 */
	public void setImages(List<Image> images) {
		this.images = images;
	}
}
