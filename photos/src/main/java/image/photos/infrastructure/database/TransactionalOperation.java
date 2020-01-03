package image.photos.infrastructure.database;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.function.Supplier;

@Component
public class TransactionalOperation {
	@Transactional
	public <T> T readWrite(Supplier<T> transaction) {
		return transaction.get();
	}

	@Transactional
	public void readWriteWithVoidResult(Runnable transaction) {
		transaction.run();
	}

	@Transactional(readOnly = true)
	public <T> T read(Supplier<T> transaction) {
		return transaction.get();
	}

	@Transactional(readOnly = true)
	public void readWithVoidResult(Runnable transaction) {
		transaction.run();
	}
}
