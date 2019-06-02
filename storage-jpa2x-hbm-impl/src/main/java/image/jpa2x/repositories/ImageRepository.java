package image.jpa2x.repositories;

import image.jpa2x.jpacustomizations.ICustomJpaRepository;
import image.persistence.entity.Image;
import org.springframework.data.jpa.repository.Query;
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

	@QueryHints(@QueryHint(name = "org.hibernate.cacheable", value = "true"))
	@Query("select i from Image i join fetch i.album")
	Image takeById(Integer id);
}
