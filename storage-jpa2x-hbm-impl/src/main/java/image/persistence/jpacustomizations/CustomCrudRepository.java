package image.persistence.jpacustomizations;

public interface CustomCrudRepository<T> {
	<S extends T> S persist(S entity);
}
