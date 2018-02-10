package image.exifweb.image;

import image.exifweb.album.importer.ExifExtractorService;
import image.exifweb.album.importer.ImageMetadata;
import image.exifweb.persistence.Album;
import image.exifweb.persistence.Image;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by adrianpetre on 29.01.2018.
 */
@Service
public class ImageRepository {
	private static final Logger logger = LoggerFactory.getLogger(ImageRepository.class);
	private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy:MM:dd HH:mm:ss.SSS");

	@Inject
	private ExifExtractorService exifExtractorService;
	@Inject
	private SessionFactory sessionFactory;

	/**
	 * Update only thumbLastModified but not ratings or status.
	 *
	 * @param thumbLastModified
	 * @param imageId
	 * @return
	 */
	@Transactional
	public Image updateThumbLastModifiedForImg(Date thumbLastModified, Integer imageId) {
		Session session = sessionFactory.getCurrentSession();
		Image image = (Image) session.get(Image.class, imageId);
		image.getImageMetadata().setThumbLastModified(thumbLastModified);
		return image;
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
		logger.debug("before album.setDirty(true), {}",
				sdf.format(image.getAlbum().getLastUpdate()));
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

	@Transactional
	public Image createImage(String name, ImageMetadata imageMetadata, Album album) {
		Image image = new Image();
		image.setImageMetadata(imageMetadata);
		image.setName(name);
		image.setAlbum(album);
		sessionFactory.getCurrentSession().persist(image);
		return image;
	}

	/**
	 * Update only metadata data but not ratings or status.
	 *
	 * @param imageMetadata
	 */
	@Transactional
	public Image updateImageMetadata(ImageMetadata imageMetadata, Integer imageId) {
		Image dbImage = (Image) sessionFactory.getCurrentSession().load(Image.class, imageId);
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
	@Transactional
	public Image getImageByNameAndAlbumId(String name, Integer albumId) {
		Integer imageId = getImageIdByNameAndAlbumId(name, albumId);
		if (imageId == null) {
			return null;
		}
		return getImageById(imageId);
	}

	@Transactional
	public Image getImageById(Integer imageId) {
		Session session = sessionFactory.getCurrentSession();
		return (Image) session.get(Image.class, imageId);
	}

	@Transactional(readOnly = true)
	private Integer getImageIdByNameAndAlbumId(String name, Integer albumId) {
		Session session = sessionFactory.getCurrentSession();
		Query q = session.createQuery("SELECT id FROM Image " +
				"WHERE name = :name AND album.id = :albumId");
		q.setString("name", name);
		q.setInteger("albumId", albumId);
		return (Integer) q.uniqueResult();
	}
}
