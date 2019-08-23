package image.jpa2x.repositories;

import image.jpa2x.jpacustomizations.ICustomJpaRepository;
import image.persistence.entity.Image;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.stereotype.Repository;

import javax.persistence.QueryHint;
import java.util.Date;
import java.util.List;
import java.util.Optional;

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

	/*
		@Query("SELECT i FROM Image i WHERE " +
				"i.name LIKE :nameNoExt " +
				"AND album.id <> :albumId " +
				"AND i.imageMetadata.exifData.dateTimeOriginal = dateTimeOriginal")
	*/
	Optional<Image> findOneByNameStartsWithIgnoreCaseAndImageMetadataExifDataDateTimeOriginalAndAlbumIdNot(String nameNoExt, Date dateTimeOriginal, Integer albumId);
}
