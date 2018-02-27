package image.persistence.repository;

import image.cdm.image.ImageRating;
import image.cdm.image.ImageStatus;
import image.persistence.entity.Album;
import image.persistence.entity.Image;
import image.persistence.entity.image.ImageMetadata;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by adrianpetre on 29.01.2018.
 */
@Service
public class ImageRepositoryImpl implements ImageRepository {
	private static final Logger logger = LoggerFactory.getLogger(ImageRepositoryImpl.class);
	private static final SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss.SSS");

	@Inject
	private SessionFactory sessionFactory;

	/**
	 * Update only thumbLastModified but not ratings or status.
	 *
	 * @param thumbLastModified
	 * @param imageId
	 * @return
	 */
	@Override
	@Transactional
	public Image updateThumbLastModifiedForImg(Date thumbLastModified, Integer imageId) {
		Session session = this.sessionFactory.getCurrentSession();
		Image image = session.get(Image.class, imageId);
		image.getImageMetadata().setThumbLastModified(thumbLastModified);
		return image;
	}

	@Override
	@Transactional
	public boolean changeRating(ImageRating imageRating) {
//		logger.debug("BEGIN");
		Session session = this.sessionFactory.getCurrentSession();
		Image image = (Image) session.load(Image.class, imageRating.getId());
		if (image.getRating() == imageRating.getRating()) {
//			logger.debug("END (same rating {})", imageRating.getRating());
			return false;
		}
//		logger.debug("before setRating({})", imageRating.getRating());
		image.setRating(imageRating.getRating());
//		logger.debug("before album.setDirty(true), {}",
//				sdf.format(image.getAlbum().getLastUpdate()));
		image.getAlbum().setDirty(true);
//		logger.debug("END");
		return true;
	}

	@Override
	@Transactional
	public boolean changeStatus(ImageStatus imageStatus) {
		Session session = this.sessionFactory.getCurrentSession();
		Image image = session.load(Image.class, imageStatus.getId());
		if (image.getStatus() == imageStatus.getStatus()) {
			return false;
		}
		image.setStatus(imageStatus.getStatus());
		image.getAlbum().setDirty(true);
		return true;
	}

	@Override
	@Transactional
	public List<Image> getImagesByAlbumId(Integer albumId) {
		Session session = this.sessionFactory.getCurrentSession();
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

	@Override
	@Transactional
	public void persistImage(Image image) {
		this.sessionFactory.getCurrentSession().persist(image);
	}

	@Override
	@Transactional
	public boolean markDeleted(Integer imageId) {
		Image image = this.sessionFactory.getCurrentSession().get(Image.class, imageId);
		if (image.isDeleted()) {
			return false;
		}
		checkAndRemoveAlbumCover(image);
		image.setDeleted(true);
		return true;
	}

	@Override
	@Transactional
	public void deleteImage(Integer imageId) {
		Image image = this.sessionFactory.getCurrentSession().load(Image.class, imageId);
		this.sessionFactory.getCurrentSession().delete(image);
	}

	@Override
	@Transactional
	public void safelyDeleteImage(Integer imageId) {
		Image image = this.sessionFactory.getCurrentSession().load(Image.class, imageId);
		checkAndRemoveAlbumCover(image);
		this.sessionFactory.getCurrentSession().delete(image);
	}

	/**
	 * Remove album's cover (set it to null) when image is its album's cover.
	 *
	 * @param image
	 * @return whether change occurred or not in DB
	 */
	@Transactional(propagation = Propagation.MANDATORY)
	private void checkAndRemoveAlbumCover(Image image) {
		Album album = image.getAlbum();
		if (album.getCover() == null || !album.getCover().getId().equals(image.getId())) {
			// image is not cover for its album
			return;
		}
		if (album.getCover() == null) {
			// cover is already missing
			return;
		}
		// removing cover
		album.setCover(null);
	}

	@Override
	@Transactional
	public void changeName(String name, Integer imageId) {
		Image image = (Image) this.sessionFactory.getCurrentSession().get(Image.class, imageId);
		image.setName(name);
	}

	/**
	 * Update only metadata data but not ratings or status.
	 *
	 * @param imageMetadata
	 */
	@Override
	@Transactional
	public Image updateImageMetadata(ImageMetadata imageMetadata, Integer imageId) {
		Image dbImage = this.sessionFactory.getCurrentSession().load(Image.class, imageId);
		dbImage.setImageMetadata(imageMetadata);
		return dbImage;
	}

	/**
	 * Role:
	 * - search the imageId then leverage the Image cache
	 *
	 * @param name
	 * @param albumId
	 * @return
	 */
	@Override
	@Transactional
	public Image getImageByNameAndAlbumId(String name, Integer albumId) {
		Integer imageId = getImageIdByNameAndAlbumId(name, albumId);
		if (imageId == null) {
			return null;
		}
		return getImageById(imageId);
	}

	@Override
	@Transactional
	public Image getImageById(Integer imageId) {
		Session session = this.sessionFactory.getCurrentSession();
		return session.get(Image.class, imageId);
	}

	@Transactional(readOnly = true)
	private Integer getImageIdByNameAndAlbumId(String name, Integer albumId) {
		Session session = this.sessionFactory.getCurrentSession();
		Query q = session.createQuery("SELECT id FROM Image " +
				"WHERE name = :name AND album.id = :albumId");
		q.setString("name", name);
		q.setInteger("albumId", albumId);
		return (Integer) q.uniqueResult();
	}
}
