package image.persistence.repositories;

import image.persistence.entity.Image;
import image.persistence.jpacustomizations.CustomJpaRepository;

public interface ImageRepository extends CustomJpaRepository<Image, Integer> {}
