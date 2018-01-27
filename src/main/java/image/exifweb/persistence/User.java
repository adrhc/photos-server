package image.exifweb.persistence;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

import javax.persistence.*;
import java.io.Serializable;

/**
 * Created with IntelliJ IDEA.
 * User: adr
 * Date: 7/19/14
 * Time: 10:45 PM
 * To change this template use File | Settings | File Templates.
 */
@Entity
@Table(name = "user")
@JsonIgnoreProperties(value = {"hibernateLazyInitializer", "handler"}, ignoreUnknown = true)
@JsonIdentityInfo(generator = ObjectIdGenerators.UUIDGenerator.class, scope = AppConfig.class)
public class User implements Serializable {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;
	private String username;
	private String password;
	private short enabled;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public short getEnabled() {
		return enabled;
	}

	public void setEnabled(short enabled) {
		this.enabled = enabled;
	}
}
