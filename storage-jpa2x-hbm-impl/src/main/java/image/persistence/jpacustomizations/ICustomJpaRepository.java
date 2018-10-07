package image.persistence.jpacustomizations;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;

@NoRepositoryBean
public interface ICustomJpaRepository<T, ID> extends ICustomCrudRepository<T>, JpaRepository<T, ID> {}
