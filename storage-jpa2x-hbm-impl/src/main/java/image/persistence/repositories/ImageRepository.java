package image.persistence.repositories;

import image.persistence.entity.Image;
import image.persistence.jpacustomizations.ICustomJpaRepository;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.stereotype.Repository;

import javax.persistence.QueryHint;
import java.util.List;

@Repository
public interface ImageRepository extends ImageRepositoryCustom, ICustomJpaRepository<Image, Integer> {
	@QueryHints(@QueryHint(name = "org.hibernate.cacheable", value = "true"))
	List<Image> findByAlbumId(Integer albumId);

	@QueryHints(@QueryHint(name = "org.hibernate.cacheable", value = "true"))
	Image findByNameAndAlbumId(String name, Integer albumId);
}
