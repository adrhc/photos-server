package image.jpa2x.repositories;

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

	@QueryHints(@QueryHint(name = "org.hibernate.cacheable", value = "true"))
	Album findByName(String name);

	@QueryHints(@QueryHint(name = "org.hibernate.cacheable", value = "true"))
	@Query("select max(lastUpdate) from Album")
	Date getMaxLastUpdateForAll();
}
