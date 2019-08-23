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
	 * No really a memory waste but might be a burden when there are many Album:
	 * https://vladmihalcea.com/hibernate-query-cache-n-plus-1-issue/
	 * when caching is enabled than only IDENTIFIER(s) would
	 * be cached which would be List<Image.id> per Album
	 * <p>
	 * competes with AlbumServiceImpl.getImages(Integer albumId)
	 */
	@QueryHints(@QueryHint(name = "org.hibernate.cacheable", value = "true"))
	List<Image> findByAlbumId(Integer albumId);

	/**
	 * make no sense to cache because would be a waste of it:
	 * all Image.id would be cached in the end which is like a no-sql DB
	 * <p>
	 * competes with ImageServiceImpl.findByNameAndAlbumId
	 */
	Image findByNameAndAlbumId(String name, Integer albumId);

	@Query("SELECT id FROM Image WHERE name = :name AND album.id = :albumId")
	Integer findIdByNameAndAlbumId(String name, Integer albumId);

	@Query("SELECT i FROM Image i WHERE " +
			"(" +
			"LOWER(i.name) LIKE CONCAT('%', LOWER(:nameNoExt), '%') OR  " +
			"LOWER(:nameNoExt) LIKE " +
			"CONCAT('%', LOWER(" +
			"   CASE WHEN LOCATE('.', i.name) <= 1 " +
			"   THEN i.name " + // e.g. ".jpeg"
			"   ELSE SUBSTRING(i.name, 1, LOCATE('.', i.name) - 1) END" + // e.g. "xxx" for xxx.y.jpeg
			"), '%')" +
			") " +
			"AND i.album.id <> :albumId")
	List<Image> findDuplicates(String nameNoExt, Integer albumId);
}
