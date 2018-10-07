package image.persistence.repositories;

import image.persistence.entity.Image;
import image.persistence.jpacustomizations.CustomJpaRepository;
import org.springframework.data.jpa.repository.QueryHints;

import javax.persistence.QueryHint;
import java.util.List;

public interface ImageRepository extends CustomJpaRepository<Image, Integer> {
	@QueryHints(@QueryHint(name = "org.hibernate.cacheable", value = "true"))
	List<Image> findByAlbumId(Integer albumId);
}
