package image.persistence.repositories;

import image.persistence.entity.enums.AppConfigEnum;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.QueryHint;
import javax.persistence.TypedQuery;

@Transactional
public class AppConfigRepositoryCustomImpl implements AppConfigRepositoryCustom {
	@PersistenceContext
	private EntityManager em;

	@Override
	public void deleteAppConfig(AppConfigEnum ace) {
	}

	@Override
	public Integer getPhotosPerPage() {
		return null;
	}

	@Override
	public String getAlbumsPath() {
		return null;
	}

	@Override
	public void updateValue(String value, Integer appConfigId) {

	}

	@Override
	@QueryHints(@QueryHint(name = "org.hibernate.cacheable", value = "true"))
	public String findByEnumeratedName(AppConfigEnum appConfigEnum) {
		TypedQuery<String> q = this.em.createQuery(
				"select a.value from AppConfig a where a.name = :name", String.class);
		q.setParameter("name", appConfigEnum.getValue());
		return q.getSingleResult();
	}
}
