package image.persistence.jpacustomizations;

public interface CustomJpaRepository<T> {
	<S extends T> S persist(S entity);
}
