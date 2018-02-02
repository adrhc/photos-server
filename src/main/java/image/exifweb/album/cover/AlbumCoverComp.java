package image.exifweb.album.cover;

import image.exifweb.image.ImageUtils;
import image.exifweb.persistence.Album;
import image.exifweb.persistence.view.AlbumCover;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.util.Date;
import java.util.List;

/**
 * Created by adr on 2/2/18.
 */
@Component
public class AlbumCoverComp {
	private static final Logger logger = LoggerFactory.getLogger(AlbumCoverComp.class);
	@Inject
	private SessionFactory sessionFactory;
	@Inject
	private ImageUtils imageUtils;

	@Transactional(readOnly = true)
	public Date getAlbumCoversLastUpdateDate() {
//		logger.debug("BEGIN");
		Session session = sessionFactory.getCurrentSession();
		return (Date) session.createCriteria(Album.class)
				.setCacheable(true)
				.setProjection(Projections.max("lastUpdate"))
				.uniqueResult();
	}

	/**
	 * Evict allCovers cache on any album change.
	 * <p>
	 * When any image changes then album in marked dirty (which is an album change).
	 * Album dirty marker is used in GUI to highlight must-regenerate-json albums
	 *
	 * @return
	 */
	@Cacheable(value = "covers", key = "'allCovers'")
	public List<AlbumCover> getAllCovers() {
		logger.debug("BEGIN");
		List<AlbumCover> covers = loadAllCovers();
		imageUtils.appendImageDimensions(covers);
		imageUtils.appendImageURIs(covers);
		return covers;
	}

	@Transactional(readOnly = true)
	private List<AlbumCover> loadAllCovers() {
		// .setCacheable(true) -> requires evict on any Album.class update or album cover (Image.class)
		return sessionFactory.getCurrentSession()
				.createCriteria(AlbumCover.class)
				.addOrder(Order.desc("albumName")).list();
	}
}
