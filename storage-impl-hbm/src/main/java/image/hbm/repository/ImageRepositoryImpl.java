package image.hbm.repository;

import image.cdm.image.ImageRating;
import image.cdm.image.status.ImageStatus;
import image.persistence.entity.Album;
import image.persistence.entity.Image;
import image.persistence.entity.image.IImageFlagsUtils;
import image.persistence.entity.image.ImageFlags;
import image.persistence.entity.image.ImageMetadata;
import image.persistence.repository.ImageRepository;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.util.Date;
import java.util.List;

/**
 * Created by adrianpetre on 29.01.2018.
 */
@Component
public class ImageRepositoryImpl implements ImageRepository, IImageFlagsUtils {
	@Autowired
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
		Image image = session.load(Image.class, imageRating.getImageId());
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
		Image image = session.load(Image.class, imageStatus.getImageId());
		ImageFlags imageFlags = this.of(imageStatus.getStatus());
		if (image.getFlags().equals(imageFlags)) {
			return false;
		}
		image.setFlags(imageFlags);
		image.getAlbum().setDirty(true);
		return true;
	}

	@Override
	@Transactional
	public List<Image> findByAlbumId(Integer albumId) {
		Session session = this.sessionFactory.getCurrentSession();
		// gets album and cover too
//		Criteria ic = session.createCriteria(Image.class)
//				.createAlias("album", "a")
//				.add(Restrictions.eq("a.id", albumId));
		// gets only the image
		CriteriaBuilder cb = session.getCriteriaBuilder();
		CriteriaQuery<Image> criteria = cb.createQuery(Image.class);
		Root<Image> root = criteria.from(Image.class);
		criteria.select(root);
		TypedQuery<Image> q = session.createQuery(criteria).setCacheable(true);
/*
		Criteria ic = session.createCriteria(Image.class)
				.add(Restrictions.eq("album.id", albumId))
				.setCacheable(true);
*/
		// gets album and cover too
//		Criteria ic = session.createCriteria(Image.class)
//				.createCriteria("album").add(Restrictions.eq("id", albumId));
		return q.getResultList();
		// gets only the image
//		return session.createQuery("FROM Image WHERE album.id = :albumId")
//				.setParameter("albumId", albumId).list();
	}

	@Override
	@Transactional
	public void persist(Image image) {
		this.sessionFactory.getCurrentSession().persist(image);
	}

	@Override
	@Transactional
	public boolean markDeleted(Integer imageId) {
		Image image = this.sessionFactory.getCurrentSession().get(Image.class, imageId);
		if (image.isDeleted()) {
			return false;
		}
		this.checkAndRemoveAlbumCoverAndFromAlbumImages(image, true);
		image.setDeleted(true);
		return true;
	}

	@Override
	@Transactional
	public void deleteById(Integer imageId) {
		Image image = this.sessionFactory.getCurrentSession().load(Image.class, imageId);
		this.sessionFactory.getCurrentSession().delete(image);
	}

	@Override
	@Transactional
	public void safelyDeleteImage(Integer imageId) {
		Image image = this.sessionFactory.getCurrentSession().load(Image.class, imageId);
		this.checkAndRemoveAlbumCoverAndFromAlbumImages(image, false);
	}

	@Override
	@Transactional
	public void changeName(String name, Integer imageId) {
		Image image = this.sessionFactory.getCurrentSession().get(Image.class, imageId);
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
		Image image = this.sessionFactory.getCurrentSession().load(Image.class, imageId);
		image.setImageMetadata(imageMetadata);
		return image;
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
	public Image findByNameAndAlbumId(String name, Integer albumId) {
		Integer imageId = this.getImageIdByNameAndAlbumId(name, albumId);
		if (imageId == null) {
			return null;
		}
		return this.getById(imageId);
	}

	@Override
	@Transactional
	public Image getById(Integer imageId) {
		Session session = this.sessionFactory.getCurrentSession();
		return session.get(Image.class, imageId);
	}

	/**
	 * Remove album's cover (set it to null) when image is its album's cover.
	 *
	 * @param persistentImage
	 * @return whether change occurred or not in DB
	 */
	private void checkAndRemoveAlbumCoverAndFromAlbumImages(Image persistentImage, boolean onlyMarkAsDeleted) {
		Album album = persistentImage.getAlbum();
		if (!onlyMarkAsDeleted) {
			album.getImages().remove(persistentImage);
		}
		if (album.getCover() == null) {
			// cover is already missing
			return;
		}
		if (!album.getCover().getId().equals(persistentImage.getId())) {
			// image is not cover for its album
			return;
		}
		// removing cover
		album.setCover(null);
	}

	private Integer getImageIdByNameAndAlbumId(String name, Integer albumId) {
		Session session = this.sessionFactory.getCurrentSession();
		Query q = session.createQuery("SELECT id FROM Image " +
				"WHERE name = :name AND album.id = :albumId");
		q.setString("name", name);
		q.setInteger("albumId", albumId);
		return (Integer) q.uniqueResult();
	}
}
