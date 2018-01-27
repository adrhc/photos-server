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
 * Time: 10:41 PM
 * To change this template use File | Settings | File Templates.
 */
@Entity
@Table(name = "user_image")
@JsonIgnoreProperties(value = {"hibernateLazyInitializer", "handler"}, ignoreUnknown = true)
@JsonIdentityInfo(generator = ObjectIdGenerators.UUIDGenerator.class, scope = AppConfig.class)
public class UserImage implements Serializable {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;
	@Column(nullable = false)
	private byte rating = 5;
	@ManyToOne(optional = false, fetch = FetchType.LAZY)
	@JoinColumn(name = "FK_IMAGE")
	private Image image;
	@ManyToOne(optional = false, fetch = FetchType.LAZY)
	@JoinColumn(name = "FK_USER")
	private User user;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public byte getRating() {
		return rating;
	}

	public void setRating(byte rating) {
		this.rating = rating;
	}

	public Image getImage() {
		return image;
	}

	public void setImage(Image image) {
		this.image = image;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}
}
