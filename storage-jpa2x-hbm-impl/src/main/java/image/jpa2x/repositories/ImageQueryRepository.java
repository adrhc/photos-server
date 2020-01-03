package image.jpa2x.repositories;

import image.persistence.entity.Image;
import org.springframework.data.jpa.repository.QueryHints;

import javax.persistence.QueryHint;
import java.util.List;

public interface ImageQueryRepository {
	/**
	 * All Image fields will be loaded too despite the fact that
	 * every individually Image returned might be already in cache.
	 * <p>
	 * Even when cached it still might be slow when there are many Image(s):
	 * https://vladmihalcea.com/hibernate-query-cache-n-plus-1-issue/
	 * when caching is enabled than only IDENTIFIER(s) would
	 * be cached which would be List<Image.id> per Album
	 * <p>
	 * competes with ImageQueryServiceImpl.getImages(Integer albumId)
	 *
	 * Result is cached till any Image (despite the album) is changed:
	 * select * from Image where FK_ALBUM=?
	 * select album0_.*, <<cover>> from Album left outer join ... where album0_.id=?
	 */
	@QueryHints(@QueryHint(name = "org.hibernate.cacheable", value = "true"))
	List<Image> findByAlbumId(Integer albumId);
}
