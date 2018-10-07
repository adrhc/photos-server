package image.persistence.jpacustomizations;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;

@NoRepositoryBean
public interface CustomJpaRepository<T, ID> extends CustomCrudRepository<T>, JpaRepository<T, ID> {}
