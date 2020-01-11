package image.infrastructure.messaging.core.message;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@SuperBuilder
@ToString
public abstract class BaseMessage<T, S, C> implements Message<S, C> {
	private S stamp;
	private C type;
	private T entity;

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || this.getClass() != o.getClass()) return false;

		BaseMessage<?, ?, ?> that = (BaseMessage<?, ?, ?>) o;

		if (this.stamp != null ? !this.stamp.equals(that.stamp) : that.stamp != null) return false;
		if (this.type != null ? !this.type.equals(that.type) : that.type != null) return false;
		return this.entity != null ? this.entity.equals(that.entity) : that.entity == null;
	}

	@Override
	public int hashCode() {
		int result = this.stamp != null ? this.stamp.hashCode() : 0;
		result = 31 * result + (this.type != null ? this.type.hashCode() : 0);
		result = 31 * result + (this.entity != null ? this.entity.hashCode() : 0);
		return result;
	}
}
