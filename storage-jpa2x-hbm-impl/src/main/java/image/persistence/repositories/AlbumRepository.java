package image.persistence.repositories;

import image.persistence.entity.Album;
import image.persistence.jpacustomizations.ICustomJpaRepository;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.stereotype.Repository;

import javax.persistence.QueryHint;
import java.util.List;

@Repository
public interface AlbumRepository extends AlbumRepositoryCustom, ICustomJpaRepository<Album, Integer> {
	@QueryHints(@QueryHint(name = "org.hibernate.cacheable", value = "true"))
	List<Album> findByDeletedFalseOrderByNameDesc();

	@QueryHints(@QueryHint(name = "org.hibernate.cacheable", value = "true"))
	Album findAlbumByName(String name);
}
