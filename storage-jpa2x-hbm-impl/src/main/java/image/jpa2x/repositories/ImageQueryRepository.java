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
	 * competes with ImageServiceImpl.getImages(Integer albumId)
	 */
	@QueryHints(@QueryHint(name = "org.hibernate.cacheable", value = "true"))
	List<Image> findByAlbumId(Integer albumId);
}