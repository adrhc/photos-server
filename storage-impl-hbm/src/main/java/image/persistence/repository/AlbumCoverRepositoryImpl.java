package image.persistence.repository;

import image.persistence.entity.Album;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import javax.persistence.metamodel.EntityType;
import javax.persistence.metamodel.SingularAttribute;
import java.util.Date;

/**
 * Created by adr on 2/3/18.
 */
@Component
public class AlbumCoverRepositoryImpl implements AlbumCoverRepository {
	@Inject
	private SessionFactory sessionFactory;

	/**
	 * Is about when AlbumCover was last modified.
	 * A last-modified date change might set dirty to false
	 * so while album is AlbumCover is no longer dirty.
	 * <p>
	 * returning Timestamp because Timestamp.getTime() adds nanos
	 *
	 * @return
	 */
	@Override
	@Transactional(readOnly = true)
	public Date getAlbumCoversLastUpdateDate() {
		Session session = this.sessionFactory.getCurrentSession();
		CriteriaBuilder cb = session.getCriteriaBuilder();
		CriteriaQuery<Date> criteria = cb.createQuery(Date.class);
		Root<Album> root = criteria.from(Album.class);
		criteria.select(cb.greatest(root.get(lastUpdate())));
		Query<Date> q = session.createQuery(criteria);
		return q.setCacheable(true).getSingleResult();
	}

	private SingularAttribute<Album, Date> lastUpdate() {
		Session session = this.sessionFactory.getCurrentSession();
		EntityType<Album> type = session.getMetamodel().entity(Album.class);
		return type.getDeclaredSingularAttribute("lastUpdate", Date.class);
	}
}
