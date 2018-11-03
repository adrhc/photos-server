package image.jpa2x.repositories;

import image.persistence.entity.AppConfig;
import image.persistence.entity.enums.AppConfigEnum;
import org.hibernate.jpa.QueryHints;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;

@Transactional
public class AppConfigRepositoryCustomImpl implements AppConfigRepositoryCustom {
	@PersistenceContext
	private EntityManager em;

	@Override
	public void deleteByEnumeratedName(AppConfigEnum ace) {
		Query q = this.em.createQuery("DELETE FROM AppConfig WHERE name = :name");
		q.setParameter("name", ace.getValue());
		q.executeUpdate();
	}

	@Override
	public Integer getPhotosPerPage() {
		String photosPerPage = findValueByEnumeratedName(AppConfigEnum.photos_per_page);
		return new Integer(photosPerPage);
	}

	@Override
	public String getAlbumsPath() {
		return findValueByEnumeratedName(AppConfigEnum.albums_path);
	}

	@Override
	public void updateValue(String value, Integer id) {
		AppConfig appConfig = this.em.find(AppConfig.class, id);
		appConfig.setValue(value);
	}

	@Override
	public String findValueByEnumeratedName(AppConfigEnum appConfigEnum) {
		TypedQuery<String> q = this.em.createQuery(
				"select a.value from AppConfig a where a.name = :name", String.class);
		q.setParameter("name", appConfigEnum.getValue());
		q.setHint(QueryHints.HINT_CACHEABLE, true);
		return q.getSingleResult();
	}
}
