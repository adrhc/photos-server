package image.persistence.jpacustomizations;

public interface ICustomCrudRepository<T, ID> {
	<S extends T> S persist(S entity);

	/**
	 * similar to hibernate's Session.get
	 */
	T getById(ID id);
}
