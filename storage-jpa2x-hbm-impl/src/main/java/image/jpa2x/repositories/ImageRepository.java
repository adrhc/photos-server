package image.jpa2x.repositories;

import image.jpa2x.jpacustomizations.ICustomJpaRepository;
import image.persistence.entity.Image;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ImageRepository extends ImageRepositoryCustom, ICustomJpaRepository<Image, Integer> {
	/**
	 * all Image fields will be loaded too despite the fact that
	 * every individually Image returned might be already cached
	 * <p>
	 * Memory waste:
	 * when caching is enabled than the entire query result should be cached
	 * despite the fact that every individually Image returned might be already cached
	 * <p>
	 * competes with AlbumServiceImpl.getImages(Integer albumId)
	 */
	List<Image> findByAlbumId(Integer albumId);

	/**
	 * make no sense to cache because would be a waste of it:
	 * all images would be cached in the end but they are already cached by id
	 */
	Image findByNameAndAlbumId(String name, Integer albumId);

	@Query("SELECT id FROM Image WHERE name = :name AND album.id = :albumId")
	Integer findIdByNameAndAlbumId(String name, Integer albumId);
}
