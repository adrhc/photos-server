package image.persistence.entity;

import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Cascade;

import javax.persistence.*;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: adrian.petre
 * Date: 11/8/13
 * Time: 1:03 PM
 * To change this template use File | Settings | File Templates.
 */
@Table(name = "Album")
@Entity
@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class Album implements Serializable {
	private static final SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss.SSS");

	private Integer id;
	private String name;
	/**
	 * Means some images are changed while album's json page-files are stale.
	 * <p>
	 * Album dirty flag is used in GUI to highlight must-regenerate-json albums.
	 */
	private boolean dirty;
	private List<Image> images;
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

	@Column(nullable = false, unique = true, length = 512)
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
	 * The parent-side @OneToOne association requires bytecode enhancement so that the association can be loaded lazily. Otherwise, the parent-side is always fetched even if the association is marked with FetchType.LAZY.
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
	public String toString() {
		return "Album{" +
				"id=" + this.id +
				", name='" + this.name + '\'' +
				", dirty=" + this.dirty +
				", cover=" + (this.cover == null ? null : this.cover.getId()) +
				", lastUpdate=" + (this.lastUpdate == null ? null : sdf.format(this.lastUpdate)) +
				", deleted=" + this.deleted +
				'}';
	}
}
