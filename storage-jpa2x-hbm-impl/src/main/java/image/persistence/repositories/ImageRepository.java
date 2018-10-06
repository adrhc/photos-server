package image.persistence.repositories;

import image.persistence.entity.Image;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.data.repository.PagingAndSortingRepository;

import javax.persistence.QueryHint;

public interface ImageRepository extends PagingAndSortingRepository<Image, Integer> {
	@Override
	@QueryHints(@QueryHint(name = "org.hibernate.cacheable", value = "true"))
	@Query("select count(i) from Image i")
	long count();
}
