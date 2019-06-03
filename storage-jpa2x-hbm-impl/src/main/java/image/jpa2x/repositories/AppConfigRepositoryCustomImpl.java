package image.jpa2x.repositories;

import image.persistence.entity.AppConfig;
import image.persistence.entity.enums.AppConfigEnum;
import org.hibernate.Session;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.List;

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
		return Integer.valueOf(photosPerPage);
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

	/**
	 * https://vladmihalcea.com/the-best-way-to-map-a-naturalid-business-key-with-jpa-and-hibernate/
	 */
	@Override
	public AppConfig findByEnumeratedName(AppConfigEnum appConfigEnum) {
		return this.em.unwrap(Session.class)
				.bySimpleNaturalId(AppConfig.class)
				.load(appConfigEnum.getValue());
	}

	@Override
	public String findValueByEnumeratedName(AppConfigEnum appConfigEnum) {
		return findByEnumeratedName(appConfigEnum).getValue();
	}

	/**
	 * Need to work with entities instead of sql directly updating the
	 * DB rows - otherwise 2nd level cache won't remain synchronized!
	 */
	@Override
	public void updateAll(List<AppConfig> appConfigs) {
		appConfigs.forEach(c -> updateValue(c.getValue(), c.getId()));
	}
}
