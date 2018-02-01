package image.exifweb.persistence;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import image.exifweb.persistence.view.AlbumCover;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Cascade;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
	private static final Logger logger = LoggerFactory.getLogger(Album.class);

	private Integer id;
	private String name;
	private boolean dirty;
	private List<Image> images;
	private Image cover;
	private Timestamp lastUpdate;
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

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	@Column(nullable = false, unique = true, length = 512)
	public String getName() {
		return name;
	}

	@CacheEvict(value = "covers", allEntries = true)
	public void setName(String name) {
//		logger.debug("album updated with name = {}:\n{}", name, toString());
		this.name = name;
	}

	/**
	 * http://in.relation.to/2016/09/28/performance-tuning-and-best-practices/
	 * <p>
	 * The parent-side @OneToOne association requires bytecode enhancement so that the association can be loaded lazily. Otherwise, the parent-side is always fetched even if the association is marked with FetchType.LAZY.
	 *
	 * @return
	 */
	@OneToOne
	@JoinColumn(name = "FK_IMAGE")
	public Image getCover() {
		return cover;
	}

	@CacheEvict(value = "covers", allEntries = true)
	public void setCover(Image cover) {
		this.cover = cover;
	}

	/**
	 * Means some images are changed while album's
	 * json file-pages are not regenerated yet.
	 * <p>
	 * The album cover is marked specially for dirty albums.
	 */
	@Column(name = "dirty")
	public boolean isDirty() {
		return dirty;
	}

	@CacheEvict(value = "covers", allEntries = true)
	public void setDirty(boolean dirty) {
		logger.debug("album updated with dirty = {}:\n{}", dirty, toString());
		this.dirty = dirty;
	}

	@Column(nullable = false)
	public boolean isDeleted() {
		return deleted;
	}

	@CacheEvict(value = "covers", allEntries = true)
	public void setDeleted(boolean deleted) {
		this.deleted = deleted;
	}

	@JsonFormat(pattern = "dd.MM.yyyy HH:mm:ss", timezone = "Europe/Bucharest")
	@Version
	@Column(name = "last_update")
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
		logger.debug("album updated with lastUpdate = {}:\n{}", lastUpdate, toString());
		this.lastUpdate = lastUpdate;
	}

	@OneToMany(mappedBy = "album", orphanRemoval = true)
	@Cascade({org.hibernate.annotations.CascadeType.ALL})
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

	@Override
	public String toString() {
		return "Album{" +
				"id=" + id +
				", name='" + name + '\'' +
				", dirty=" + dirty +
				", images=" + (images == null ? null : images.size()) +
				", cover=" + (cover == null ? null : cover.getId()) +
				", lastUpdate=" + lastUpdate +
				", deleted=" + deleted +
				'}';
	}
}
