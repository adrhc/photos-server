package image.exifweb.persistence;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;
import java.io.Serializable;
import java.sql.Timestamp;

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
	private Timestamp lastUpdate;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	@Column
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Column
	public String getValue() {
		return value;
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

	@JsonIgnore
	@Version
	@Column(name = "last_update")
	public Timestamp getLastUpdate() {
		return lastUpdate;
	}

	public void setLastUpdate(Timestamp lastUpdate) {
		this.lastUpdate = lastUpdate;
	}

	@Override
	public String toString() {
		return "AppConfig{" +
				"id=" + id +
				", name='" + name + '\'' +
				", value='" + value + '\'' +
				", lastUpdate=" + lastUpdate +
				'}';
	}
}
