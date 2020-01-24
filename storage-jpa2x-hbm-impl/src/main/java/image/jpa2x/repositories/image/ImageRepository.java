package image.jpa2x.repositories.image;

import image.jpa2x.jpacustomizations.ICustomJpaRepository;
import image.persistence.entity.Image;
import org.springframework.stereotype.Repository;

@Repository
public interface ImageRepository extends ImageQueryRepository,
		ImageUpdateRepository, ICustomJpaRepository<Image, Integer> {}
