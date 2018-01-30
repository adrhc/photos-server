package image.exifweb.image;

import image.exifweb.persistence.Image;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.util.List;

/**
 * Created by adrianpetre on 29.01.2018.
 */
@Service
public class ImageService {
	@Inject
	private SessionFactory sessionFactory;

	@Transactional
	public boolean removeById(Integer imageId) {
		Session session = sessionFactory.getCurrentSession();
		Query q = session.createQuery("DELETE FROM Image WHERE id = :imageId");
		q.setParameter("imageId", imageId);
		return q.executeUpdate() > 0;
	}

	/**
	 * We should already be in a transactional context!
	 *
	 * @param image must be a persistent one
	 * @return
	 */
	public void remove(Image image) {
		Session session = sessionFactory.getCurrentSession();
		session.delete(image);
	}

	@Transactional
	public Image getById(Integer imageId) {
		Session session = sessionFactory.getCurrentSession();
		return (Image) session.get(Image.class, imageId);
	}

	@Transactional
	public List<Image> getImagesByAlbumId(Integer albumId) {
		Session session = sessionFactory.getCurrentSession();
		// gets album and cover too
//		Criteria ic = session.createCriteria(Image.class)
//				.createAlias("album", "a")
//				.add(Restrictions.eq("a.id", albumId));
		// gets only the image
		Criteria ic = session.createCriteria(Image.class)
				.add(Restrictions.eq("album.id", albumId));
		// gets album and cover too
//		Criteria ic = session.createCriteria(Image.class)
//				.createCriteria("album").add(Restrictions.eq("id", albumId));
		return ic.list();
		// gets only the image
//		return session.createQuery("FROM Image WHERE album.id = :albumId")
//				.setParameter("albumId", albumId).list();
	}

	@Transactional
	public List<Integer> getImageIdsByAlbumId(Integer albumId) {
		Session session = sessionFactory.getCurrentSession();
		Criteria ic = session.createCriteria(Image.class)
				.add(Restrictions.eq("album.id", albumId))
				.setProjection(Projections.id());
		return ic.list();
	}
}
