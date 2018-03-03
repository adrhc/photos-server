package image.persistence.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: adrian.petre
 * Date: 11/8/13
 * Time: 5:28 PM
 * To change this template use File | Settings | File Templates.
 */
@Entity
@JsonIgnoreProperties(value = {"hibernateLazyInitializer", "handler"}, ignoreUnknown = true)
@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class AppConfig implements Serializable {
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
	@JsonIgnore
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
