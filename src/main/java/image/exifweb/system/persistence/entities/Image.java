package image.exifweb.system.persistence.entities;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import image.exifweb.system.persistence.entities.image.ImageMetadata;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Formula;

import javax.persistence.*;
import java.io.Serializable;
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
@JsonIgnoreProperties(value = {"hibernateLazyInitializer", "handler"}, ignoreUnknown = true)
@JsonIdentityInfo(generator = ObjectIdGenerators.UUIDGenerator.class, scope = Image.class)
@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class Image implements Serializable {
	public static final Byte DEFAULT_STATUS = 0;
	public static final Byte DEFAULT_RATING = 1;
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;
	@Column(nullable = false, length = 256)
	private String name;
	@Embedded
	private ImageMetadata imageMetadata;
	/**
	 * see DEFAULT_STATUS = 0 defined above
	 */
	@Column(nullable = false)
	private Byte status = DEFAULT_STATUS;
	@Column(nullable = false)
	private boolean deleted;
	@Formula("(status & 1)")
	private boolean hidden;
	@Formula("(status & 2)")
	private boolean personal;
	@Formula("(status & 4)")
	private boolean ugly;
	@Formula("(status & 8)")
	private boolean duplicate;
	@Formula("(status & 16)")
	private boolean printable;
	/**
	 * see DEFAULT_RATING = 1 (defined above)
	 */
	@Column(nullable = false, columnDefinition = "INTEGER(1) NOT NULL DEFAULT 1")
	private byte rating = DEFAULT_RATING;
	@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
	@ManyToOne(optional = false, fetch = FetchType.LAZY)
	@JoinColumn(name = "FK_ALBUM")
	private Album album;
	/**
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
		return lastUpdate;
	}

	public void setLastUpdate(Date lastUpdate) {
		this.lastUpdate = lastUpdate;
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

	public Album getAlbum() {
		return album;
	}

	public void setAlbum(Album album) {
		this.album = album;
	}

	public Byte getStatus() {
		return status;
	}

	public void setStatus(Byte status) {
		this.status = status;
	}

	public boolean isHidden() {
		return hidden;
	}

	public boolean isPersonal() {
		return personal;
	}

	public boolean isUgly() {
		return ugly;
	}

	public boolean isDuplicate() {
		return duplicate;
	}

	public boolean isPrintable() {
		return printable;
	}

	public boolean isDeleted() {
		return deleted;
	}

	public void setDeleted(boolean deleted) {
		this.deleted = deleted;
	}

	public byte getRating() {
		return rating;
	}

	public void setRating(byte rating) {
		this.rating = rating;
	}

	public ImageMetadata getImageMetadata() {
		return imageMetadata;
	}

	public void setImageMetadata(ImageMetadata imageMetadata) {
		this.imageMetadata = imageMetadata;
	}

	@Override
	public String toString() {
		return "Image{" +
				"id=" + id +
				", name='" + name + '\'' +
				", imageMetadata=" + imageMetadata.toString() +
				", status=" + status +
				", deleted=" + deleted +
				", hidden=" + hidden +
				", personal=" + personal +
				", ugly=" + ugly +
				", duplicate=" + duplicate +
				", printable=" + printable +
				", rating=" + rating +
				", lastUpdate=" + lastUpdate +
				'}';
	}
}
