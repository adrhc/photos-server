package image.exifweb.album.cover;

import image.exifweb.persistence.Album;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Projections;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.util.Date;

/**
 * Created by adr on 2/3/18.
 */
@Component
public class AlbumCoverRepo {
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
	@Transactional(readOnly = true)
	public Date getAlbumCoversLastUpdateDate() {
		Session session = sessionFactory.getCurrentSession();
		return (Date) session.createCriteria(Album.class)
				.setCacheable(true)
				.setProjection(Projections.max("lastUpdate"))
				.uniqueResult();
	}
}
