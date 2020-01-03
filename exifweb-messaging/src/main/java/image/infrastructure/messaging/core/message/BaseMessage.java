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
}
