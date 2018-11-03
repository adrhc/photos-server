package image.persistence.entity;

import image.cdm.image.ImageRating;
import image.cdm.image.status.EImageStatus;
import image.persistence.entitytests.image.IImageFlagsUtils;
import image.persistence.entitytests.image.ImageFlags;
import image.persistence.entitytests.image.ImageMetadata;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;
import java.util.Date;

/**
 * Watch out to triggers!
 * e.g. ALBUM_DIRTY_ON_RATING used when using nginx drizzle (mysql)
 * <p>
 * Created with IntelliJ IDEA.
 * User: adrian.petre
 * Date: 11/7/13
 * Time: 9:06 PM
 * To change this template use File | Settings | File Templates.
 */
@Entity
@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class Image implements IStorageEntity, IImageFlagsUtils {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;
	@Column(nullable = false, length = 256)
	private String name;
	@Column(nullable = false)
	private boolean deleted;
	@Embedded
	private ImageMetadata imageMetadata;
	@Embedded
	private ImageFlags flags = of(EImageStatus.DEFAULT);
	/**
	 * see MIN_RATING = 1 (defined above)
	 */
	@Column(nullable = false, columnDefinition = "INTEGER(1) NOT NULL DEFAULT 1")
	private byte rating = ImageRating.MIN_RATING;
	@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
	@ManyToOne(optional = false, fetch = FetchType.LAZY)
	@JoinColumn(name = "FK_ALBUM")
	private Album album;
	/**
	 * used to compute albumPage Last-Modified (impact browser-cache)
	 * <p>
	 * nullable version or timestamp property is good:
	 * A version or timestamp property can never be null for a detached instance. Hibernate detects any instance with a null version or timestamp as transient, regardless of other unsaved-value strategies that you specify. Declaring a nullable version or timestamp property is an easy way to avoid problems with transitive reattachment in Hibernate, especially useful if you use assigned identifiers or composite keys.
	 * <p>
	 * Mysql by default saves without milliseconds; bad for optimistic locking!
	 * <p>
	 * DEFAULT CURRENT_TIMESTAMP -> desired
	 * ON UPDATE CURRENT_TIMESTAMP -> very bad; overwrites the value set by hibernate
	 * <p>
	 * related to db record -> rating, status, deleted change
	 * <p>
	 * TIMESTAMP(3) supports milliseconds
	 * last_update` TIMESTAMP(3) NOT NULL DEFAULT now(3)
	 * <p>
	 * Date represents a specific instant in time, with millisecond precision.
	 * java.sql.Timestamp holds the SQL TIMESTAMP fractional seconds value, by allowing the specification of fractional seconds to a precision of nanoseconds.
	 */
	@Version
	@Column(name = "last_update")
	private Date lastUpdate;

	public Date getLastUpdate() {
		return this.lastUpdate;
	}

	public void setLastUpdate(Date lastUpdate) {
		this.lastUpdate = lastUpdate;
	}

	public Integer getId() {
		return this.id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Album getAlbum() {
		return this.album;
	}

	public void setAlbum(Album album) {
		this.album = album;
	}

	public boolean isDeleted() {
		return this.deleted;
	}

	public void setDeleted(boolean deleted) {
		this.deleted = deleted;
	}

	public byte getRating() {
		return this.rating;
	}

	public void setRating(byte rating) {
		this.rating = rating;
	}

	public ImageMetadata getImageMetadata() {
		return this.imageMetadata;
	}

	public void setImageMetadata(ImageMetadata imageMetadata) {
		this.imageMetadata = imageMetadata;
	}

	public ImageFlags getFlags() {
		return this.flags;
	}

	public void setFlags(ImageFlags flags) {
		this.flags = flags;
	}

	@Override
	public String toString() {
		return "Image{" +
				"id=" + this.id +
				", name='" + this.name + '\'' +
				", deleted=" + this.deleted +
				", imageMetadata=" + this.imageMetadata.toString() +
				", flags=" + this.flags.toString() +
				", rating=" + this.rating +
				", album=" + this.album +
				", lastUpdate=" + this.lastUpdate +
				'}';
	}
}
