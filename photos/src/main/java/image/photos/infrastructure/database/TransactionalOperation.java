package image.photos.infrastructure.database;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.function.Consumer;
import java.util.function.Function;

@Component
public class TransactionalOperation {
	@PersistenceContext
	private EntityManager em;

	@Transactional
	public <R> R write(Function<EntityManager, R> transaction) {
		return transaction.apply(em);
	}

	@Transactional
	public void writeWithVoidResult(Consumer<EntityManager> transaction) {
		transaction.accept(em);
	}

	@Transactional(readOnly = true)
	public <R> R read(Function<EntityManager, R> transaction) {
		return transaction.apply(em);
	}
}
