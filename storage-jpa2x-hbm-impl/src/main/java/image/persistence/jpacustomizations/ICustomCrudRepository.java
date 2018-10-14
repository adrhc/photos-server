package image.persistence.jpacustomizations;

public interface ICustomCrudRepository<T, ID> {
	void persist(T entity);

	/**
	 * similar to hibernate's Session.get
	 */
	T getById(ID id);
}
