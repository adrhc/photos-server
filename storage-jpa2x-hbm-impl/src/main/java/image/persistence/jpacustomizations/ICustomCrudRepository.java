package image.persistence.jpacustomizations;

public interface ICustomCrudRepository<T> {
	<S extends T> S persist(S entity);
}
