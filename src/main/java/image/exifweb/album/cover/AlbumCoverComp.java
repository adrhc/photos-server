package image.exifweb.album.cover;

import image.exifweb.image.ImageUtils;
import image.exifweb.persistence.Album;
import image.exifweb.persistence.view.AlbumCover;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
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
	//	private static final Logger logger = LoggerFactory.getLogger(AlbumCoverComp.class);
	@Inject
	private SessionFactory sessionFactory;
	@Inject
	private ImageUtils imageUtils;

	/**
	 * Evict allCovers cache on any album change.
	 * <p>
	 * When any image changes then album in marked dirty (which is an album change).
	 * Album dirty flag is used in GUI to highlight must-regenerate-json albums.
	 */
	@Cacheable(value = "covers", key = "'allCovers'")
	public List<AlbumCover> getAllCovers() {
		List<AlbumCover> covers = loadAllCovers();
		imageUtils.appendImageDimensions(covers);
		return covers;
	}

	/**
	 * setCacheable(true) usage requires evict on any Album.class update or album cover (Image.class)
	 */
	@Transactional(readOnly = true)
	private List<AlbumCover> loadAllCovers() {
		return sessionFactory.getCurrentSession()
				.createCriteria(AlbumCover.class)
				.addOrder(Order.desc("albumName")).list();
	}

	/**
	 * Is about when AlbumCover was last modified.
	 * A last-modified date change might set dirty to false
	 * so while album is AlbumCover is no longer dirty.
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
