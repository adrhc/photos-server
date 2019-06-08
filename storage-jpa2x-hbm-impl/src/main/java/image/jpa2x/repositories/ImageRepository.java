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
	/**
	 * all Image fields will be loaded too despite the fact that
	 * every individually Image returned might be already cached
	 * <p>
	 * Memory waste:
	 * when org.hibernate.cacheable=true the entire query result should be cached
	 * despite the fact that every individually Image returned might be already cached
	 * <p>
	 * competes with AlbumServiceImpl.getImages(Integer albumId)
	 */
	@QueryHints(@QueryHint(name = "org.hibernate.cacheable", value = "false"))
	List<Image> findByAlbumId(Integer albumId);

	/**
	 * make no sense to cache because would be too much to cache (all images)
	 */
	@QueryHints(@QueryHint(name = "org.hibernate.cacheable", value = "false"))
	Image findByNameAndAlbumId(String name, Integer albumId);

	@Query("SELECT id FROM Image WHERE name = :name AND album.id = :albumId")
	Integer findIdByNameAndAlbumId(String name, Integer albumId);
}
