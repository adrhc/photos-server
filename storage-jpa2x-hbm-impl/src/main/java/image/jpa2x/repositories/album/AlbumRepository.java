package image.jpa2x.repositories.album;

import image.jpa2x.jpacustomizations.ICustomJpaRepository;
import image.persistence.entity.Album;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.stereotype.Repository;

import javax.persistence.QueryHint;
import java.util.Date;
import java.util.List;

@Repository
public interface AlbumRepository extends AlbumRepositoryCustom, ICustomJpaRepository<Album, Integer> {
	@QueryHints(@QueryHint(name = "org.hibernate.cacheable", value = "true"))
	List<Album> findByDeletedFalseOrderByNameDesc();

	/**
	 * when using @NaturalId on Album.name the query caching
	 * (org.hibernate.cacheable=true) is no longer necessary
	 */
	Album findByName(String name);

	@QueryHints(@QueryHint(name = "org.hibernate.cacheable", value = "true"))
	@Query("select max(lastUpdate) from Album")
	Date getMaxLastUpdateForAll();

	boolean existsByName(String albumName);
}
