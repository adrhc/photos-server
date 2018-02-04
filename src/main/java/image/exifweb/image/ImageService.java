package image.exifweb.image;

import image.exifweb.persistence.Image;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
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

	@Transactional
	public Image getById(Integer imageId) {
		Session session = sessionFactory.getCurrentSession();
		return (Image) session.get(Image.class, imageId);
	}

	@Transactional
	public boolean changeRating(ImageRating imageRating) {
		logger.debug("BEGIN");
		Session session = sessionFactory.getCurrentSession();
		Image image = (Image) session.load(Image.class, imageRating.getId());
		if (image.getRating() == imageRating.getRating()) {
			logger.debug("END (same rating {})", imageRating.getRating());
			return false;
		}
		logger.debug("before setRating({})", imageRating.getRating());
		image.setRating(imageRating.getRating());
		logger.debug("before album.setDirty(true)");
		image.getAlbum().setDirty(true);
		logger.debug("END");
		return true;
	}

	@Transactional
	public boolean changeStatus(ImageStatus imageStatus) {
		Session session = sessionFactory.getCurrentSession();
		Image image = (Image) session.load(Image.class, imageStatus.getId());
		if (image.getStatus().equals(imageStatus.getStatus())) {
			return false;
		}
		image.setStatus(imageStatus.getStatus());
		image.getAlbum().setDirty(true);
		return true;
	}

	@Transactional
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
}
