package image.persistence.repositories;

import image.persistence.entity.Image;
import image.persistence.jpacustomizations.CustomJpaRepository;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ImageRepository extends CustomJpaRepository<Image>, JpaRepository<Image, Integer> {
/*
	@Override
	@QueryHints(@QueryHint(name = "org.hibernate.cacheable", value = "true"))
	@Query("select count(i) from Image i")
	long count();
*/
}
