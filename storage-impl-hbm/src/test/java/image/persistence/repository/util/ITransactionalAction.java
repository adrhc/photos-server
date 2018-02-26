package image.persistence.repository.util;

import javax.transaction.Transactional;

/**
 * Created by adr on 2/26/18.
 */
public interface ITransactionalAction {
	@Transactional
	default void doTransaction(Runnable action) {
		action.run();
	}
}
