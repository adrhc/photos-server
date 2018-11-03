package image.jpa2x.repositories;

import image.jpa2x.jpacustomizations.ICustomJpaRepository;
import image.persistence.entity.AppConfig;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.stereotype.Repository;

import javax.persistence.QueryHint;
import java.util.Date;
import java.util.List;

@Repository
public interface AppConfigRepository extends AppConfigRepositoryCustom, ICustomJpaRepository<AppConfig, Integer> {
	@QueryHints(@QueryHint(name = "org.hibernate.cacheable", value = "true"))
	AppConfig findByName(String name);

	@Query("select a from AppConfig a where a.name = ?1")
	AppConfig findByNameNotCached(String name);

	@Query("select a from AppConfig a order by a.name asc")
	List<AppConfig> findAllOrderByNameAscNotCached();

	/**
	 * see NamedNativeQuery(name = "AppConfig.getDBNow" ...)
	 */
	Date getDBNow();
}
