package image.infrastructure.messaging.message;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@SuperBuilder
@ToString
public abstract class BaseMessage<T, I, C> implements Message<I, C> {
	private I id;
	private C type;
	private T entity;
}
