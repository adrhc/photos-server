package image.jpa2x.repositories;

import image.jpa2x.jpacustomizations.ICustomJpaRepository;
import image.persistence.entity.Image;
import org.springframework.stereotype.Repository;

@Repository
public interface ImageRepository extends ImageQueryRepository,
		ImageRepositoryCustom, ICustomJpaRepository<Image, Integer> {}
