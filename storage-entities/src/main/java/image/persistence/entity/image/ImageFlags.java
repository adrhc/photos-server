package image.persistence.entity.image;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;

@Embeddable
public class ImageFlags implements Serializable {
	@Column(nullable = false)
	private boolean duplicate;
	@Column(nullable = false)
	private boolean hidden;
	@Column(nullable = false)
	private boolean personal;
	@Column(nullable = false)
	private boolean printable;
	@Column(nullable = false)
	private boolean ugly;

	public boolean isHidden() {
		return this.hidden;
	}

	public void setHidden(boolean hidden) {
		this.hidden = hidden;
	}

	public boolean isPersonal() {
		return this.personal;
	}

	public void setPersonal(boolean personal) {
		this.personal = personal;
	}

	public boolean isUgly() {
		return this.ugly;
	}

	public void setUgly(boolean ugly) {
		this.ugly = ugly;
	}

	public boolean isDuplicate() {
		return this.duplicate;
	}

	public void setDuplicate(boolean duplicate) {
		this.duplicate = duplicate;
	}

	public boolean isPrintable() {
		return this.printable;
	}

	public void setPrintable(boolean printable) {
		this.printable = printable;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {return true;}
		if (o == null || getClass() != o.getClass()) {return false;}

		ImageFlags that = (ImageFlags) o;

		if (this.hidden != that.hidden) {return false;}
		if (this.personal != that.personal) {return false;}
		if (this.ugly != that.ugly) {return false;}
		if (this.duplicate != that.duplicate) {return false;}
		return this.printable == that.printable;
	}

	/**
	 * https://vladmihalcea.com/the-best-way-to-implement-equals-hashcode-and-tostring-with-jpa-and-hibernate/
	 */
	@Override
	public int hashCode() {
		return 31;
	}

	@Override
	public String toString() {
		return "ImageFlags{" +
				"hidden=" + this.hidden +
				", personal=" + this.personal +
				", ugly=" + this.ugly +
				", duplicate=" + this.duplicate +
				", printable=" + this.printable +
				'}';
	}
}
