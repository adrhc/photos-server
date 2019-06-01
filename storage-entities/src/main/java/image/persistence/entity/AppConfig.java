package image.persistence.entity;

import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;
import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: adrian.petre
 * Date: 11/8/13
 * Time: 5:28 PM
 * To change this template use File | Settings | File Templates.
 */
@NamedNativeQuery(name = "AppConfig.getDBNow", query = "SELECT now() FROM dual")
@Table(name = "AppConfig")
@Entity
@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class AppConfig implements IStorageEntity {
	private Integer id;
	private String name;
	private String value;
	private Date lastUpdate;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	public Integer getId() {
		return this.id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	@Column
	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Column
	public String getValue() {
		return this.value;
	}

	/**
	 * small optimization based on MC - JOverflow Analyzer
	 * duplicate strings, String.intern(), memory optimization
	 * https://www.youtube.com/watch?v=b-mv9iWY8kw&ab_channel=OracleLearningLibrary
	 *
	 * @param value
	 */
	public void setValue(String value) {
		this.value = value == null || !value.equals("true") && !value.equals("false") ? value : value.intern();
	}

	/**
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

	public void setLastUpdate(Date lastUpdate) {
		this.lastUpdate = lastUpdate;
	}

	public boolean similarTo(AppConfig other) {
		return this.name == null ? other.getName() == null : this.name.equals(other.getName()) &&
				this.value == null ? other.getValue() == null : this.value.equals(other.getValue());
	}

	/**
	 * https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
	 * https://vladmihalcea.com/the-best-way-to-implement-equals-hashcode-and-tostring-with-jpa-and-hibernate/
	 * identifier checked only for non-transient entities
	 */
	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (!(o instanceof AppConfig)) {
			return false;
		}

		AppConfig other = (AppConfig) o;

		// no other properties checked!
		// see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
		return this.id != null && this.id.equals(other.getId());
	}

	/**
	 * https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
	 * https://vladmihalcea.com/the-best-way-to-implement-equals-hashcode-and-tostring-with-jpa-and-hibernate/
	 */
	@Override
	public int hashCode() {
		return 71;
	}

	@Override
	public String toString() {
		return "AppConfig{" +
				"id=" + this.id +
				", name='" + this.name + '\'' +
				", value='" + this.value + '\'' +
				", lastUpdate=" + this.lastUpdate +
				'}';
	}
}
