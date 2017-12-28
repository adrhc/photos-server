package image.exifweb.persistence;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

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
public class AppConfig implements Serializable {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;
	@Column
	private String name;
	@Column
	private String value;
	@JsonIgnore
	@Version
	@Column(name = "last_update")
	private Timestamp lastUpdate;

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

	public String getValue() {
		return value;
	}

	/**
	 * small optimization based on MC - JOverflow
	 *
	 * @param value
	 */
	public void setValue(String value) {
		this.value = value == null || !value.equals("true") && !value.equals("false") ? value : value.intern();
	}

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
