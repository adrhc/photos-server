package image.persistence.entity;

import com.fasterxml.jackson.annotation.JsonView;
import image.persistence.entity.jsonview.AlbumViews;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.NaturalId;
import org.hibernate.annotations.NaturalIdCache;

import javax.persistence.*;
import java.io.Serializable;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static image.persistence.entity.util.DateUtils.safeFormat;

/**
 * Created with IntelliJ IDEA.
 * User: adrian.petre
 * Date: 11/8/13
 * Time: 1:03 PM
 * To change this template use File | Settings | File Templates.
 */
@Cacheable
@NaturalIdCache
@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@Table(name = "Album")
@Entity
public class Album implements Serializable {
	private static final DateTimeFormatter sdf =
			DateTimeFormatter.ofPattern("yyyy.MM.dd HH:mm:ss.SSS").withZone(ZoneOffset.UTC);

	private Integer id;
	private String name;
	/**
	 * Means some images are changed while album's json page-files are stale.
	 * <p>
	 * Album dirty flag is used in GUI to highlight must-regenerate-json albums.
	 */
	private boolean dirty = true;
	@JsonView(AlbumViews.Images.class)
	private List<Image> images;
	@JsonView(AlbumViews.Cover.class)
	private Image cover;
	private Date lastUpdate;
	private boolean deleted;

	public Album() {}

	public Album(String name) {
		this.name = name;
	}

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	public Integer getId() {
		return this.id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	@NaturalId
	@Column(unique = true, nullable = false, updatable = false, length = 512)
	public String getName() {
		return this.name;
	}

	public void setName(String name) {
//		logger.debug("old name = {}, new name = {}", this.name, name);
		this.name = name;
	}

	/**
	 * http://in.relation.to/2016/09/28/performance-tuning-and-best-practices/
	 * <p>
	 * The parent-side @OneToOne association requires bytecode enhancement so that the
	 * association can be loaded lazily. Otherwise, the parent-side is always fetched
	 * even if the association is marked with FetchType.LAZY.
	 */
	@OneToOne
	@JoinColumn(name = "FK_IMAGE")
	public Image getCover() {
		return this.cover;
	}

	public void setCover(Image cover) {
//		logger.debug("old cover = {}, new cover = {}",
//				this.cover == null ? null : this.cover.getId(),
//				cover == null ? null : cover.getId());
		this.cover = cover;
	}

	@Column(name = "dirty")
	public boolean isDirty() {
		return this.dirty;
	}

	public void setDirty(boolean dirty) {
//		logger.debug("old dirty = {}, new dirty = {}", this.dirty, dirty);
		this.dirty = dirty;
	}

	@Column(nullable = false)
	public boolean isDeleted() {
		return this.deleted;
	}

	public void setDeleted(boolean deleted) {
//		logger.debug("old deleted = {}, new deleted = {}", this.deleted, deleted);
		this.deleted = deleted;
	}

	/**
	 * nullable version or timestamp property is good:
	 * A version or timestamp property can never be null for a detached instance. Hibernate detects any instance with a null version or timestamp as transient, regardless of other unsaved-value strategies that you specify. Declaring a nullable version or timestamp property is an easy way to avoid problems with transitive reattachment in Hibernate, especially useful if you use assigned identifiers or composite keys.
	 * <p>
	 * Mysql by default saves without milliseconds; bad for optimistic locking!
	 * <p>
	 * DEFAULT CURRENT_TIMESTAMP -> desired
	 * ON UPDATE CURRENT_TIMESTAMP -> very bad; overwrites the value set by hibernate
	 * <p>
	 * TIMESTAMP(3) supports milliseconds
	 * last_update` TIMESTAMP(3) NOT NULL DEFAULT now(3)
	 * <p>
	 * Date represents a specific instant in time, with millisecond precision.
	 * java.sql.Timestamp holds the SQL TIMESTAMP fractional seconds value, by allowing the specification of fractional seconds to a precision of nanoseconds.
	 */
	@Version
	@Column(name = "last_update")
	public Date getLastUpdate() {
		return this.lastUpdate;
	}

	/**
	 * Atentie, e o coloana @Version! Nu o seta niciodata pt ca o seteaza hibernate!
	 * Dpv al cache-ului o eventuala modificare de lastUpdate e datorata
	 * unei modificari a altei proprietati care oricum ar afecta cache la randu-i.
	 */
	public void setLastUpdate(Date lastUpdate) {
		this.lastUpdate = lastUpdate;
	}

	/**
	 * The collection cache is not write-through so any modification will trigger
	 * a collection cache entry invalidation. On a subsequent access, the collection
	 * will be loaded from the database and re-cached.
	 * <p>
	 * Normal but problematic @Cache operation rule:
	 * when deleting an individually an Image (e.g. em.remove(Image)) without
	 * removing it from Album.images too than the cache won't be invalidated!
	 */
//	@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
	@OneToMany(mappedBy = "album", orphanRemoval = true)
	@Cascade({org.hibernate.annotations.CascadeType.ALL})
	public List<Image> getImages() {
		return this.images;
	}

	/**
	 * Nu intra in zona de album cache.
	 * Dirty insa ar trebui se fie afectat si automat va afecta si album cache.
	 * <p>
	 * Only JPA provider is allowed to set Images!
	 */
	private void setImages(List<Image> images) {
		this.images = images;
	}

	public void addImages(List<Image> images) {
		images.forEach(this::addImage);
	}

	public void addImage(Image image) {
		if (this.images == null) {
			this.images = new ArrayList<>();
		}
		this.images.add(image);
		image.setAlbum(this);
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (!(o instanceof Album)) {
			return false;
		}
		Album other = (Album) o;
		return this.id != null && this.id.equals(other.getId());
	}

	@Override
	public int hashCode() {
		return 71;
	}

	@Override
	public String toString() {
		return "Album{" +
				"id=" + this.id +
				", name='" + this.name + '\'' +
				", dirty=" + this.dirty +
				", cover=" + (this.cover == null ? null : this.cover.getId()) +
				", lastUpdate=" + safeFormat(this.lastUpdate, sdf) +
				", deleted=" + this.deleted +
				'}';
	}
}
