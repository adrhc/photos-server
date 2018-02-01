package image.exifweb.image;

import image.exifweb.persistence.Album;
import image.exifweb.persistence.Image;
import org.hibernate.CacheMode;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.util.Date;
import java.util.List;

/**
 * Created by adrianpetre on 29.01.2018.
 */
@Service
public class ImageService {
	private static final Logger logger = LoggerFactory.getLogger(ImageService.class);

	@Inject
	private SessionFactory sessionFactory;

	@Transactional
	public void updateThumbLastModifiedForImg(Date thumbLastModified, Integer imageId) {
		Session session = sessionFactory.getCurrentSession();
		Image image = (Image) session.load(Integer.class, imageId);
		image.setThumbLastModified(thumbLastModified);
	}

	/**
	 * We should already be in a transactional context!
	 *
	 * @param image must be a persistent one
	 * @return
	 */
	@Transactional(propagation = Propagation.MANDATORY)
	public void removeNoTx(Image image) {
		Session session = sessionFactory.getCurrentSession();
		session.delete(image);
	}

	@Transactional(readOnly = true)
	public Image getById(Integer imageId) {
		Session session = sessionFactory.getCurrentSession();
		return (Image) session.get(Image.class, imageId);
	}

	@Transactional
	public void changeRating(ImageRating imageRating) {
		logger.debug("BEGIN");
		Session session = sessionFactory.getCurrentSession();
		logger.debug("old session.cacheMode: {}", session.getCacheMode().toString());
		Image image = (Image) session.get(Image.class, imageRating.getId());
		logger.debug("before setRating({})", imageRating.getRating());
		image.setRating(imageRating.getRating());
		logger.debug("before image.getAlbum");
		Album album = image.getAlbum();
		logger.debug("album:\n\tid: {}\n\tname: {}\n\tdirty: {}\n\tlastUpdate: {}",
				album.getId(), album.getName(), album.isDirty(), album.getLastUpdate());
		logger.debug("before album.setDirty");
		album.setDirty(true);
		logger.debug("END");
	}

	@Transactional
	public void changeStatus(ImageStatus imageStatus) {
		Session session = sessionFactory.getCurrentSession();
		Image image = (Image) session.load(Image.class, imageStatus.getId());
		image.setStatus(imageStatus.getStatus());
		image.getAlbum().setDirty(true);
	}

	@Transactional(readOnly = true)
	public List<Image> getImagesByAlbumId(Integer albumId) {
		Session session = sessionFactory.getCurrentSession();
		// gets album and cover too
//		Criteria ic = session.createCriteria(Image.class)
//				.createAlias("album", "a")
//				.add(Restrictions.eq("a.id", albumId));
		// gets only the image
		Criteria ic = session.createCriteria(Image.class).setCacheable(true)
				.add(Restrictions.eq("album.id", albumId));
		// gets album and cover too
//		Criteria ic = session.createCriteria(Image.class)
//				.createCriteria("album").add(Restrictions.eq("id", albumId));
		return ic.list();
		// gets only the image
//		return session.createQuery("FROM Image WHERE album.id = :albumId")
//				.setParameter("albumId", albumId).list();
	}

	@Transactional(readOnly = true)
	public List<Integer> getImageIdsByAlbumId(Integer albumId) {
		Session session = sessionFactory.getCurrentSession();
		Criteria ic = session.createCriteria(Image.class).setCacheable(true)
				.add(Restrictions.eq("album.id", albumId))
				.setProjection(Projections.id());
		return ic.list();
	}
}
