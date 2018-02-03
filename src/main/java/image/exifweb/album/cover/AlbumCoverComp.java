package image.exifweb.album.cover;

import image.exifweb.album.AlbumService;
import image.exifweb.image.ImageUtils;
import image.exifweb.persistence.Album;
import image.exifweb.persistence.Image;
import image.exifweb.persistence.view.AlbumCover;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Projections;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by adr on 2/2/18.
 */
@Component
public class AlbumCoverComp {
	//	private static final Logger logger = LoggerFactory.getLogger(AlbumCoverComp.class);
	@Inject
	private SessionFactory sessionFactory;
	@Inject
	private AlbumService albumService;
	@Inject
	private ImageUtils imageUtils;

	public List<AlbumCover> getAllCovers() {
		return albumService.getAlbums().stream()
				.map(a -> {
					Image cover = a.getCover();
					AlbumCover ac;
					if (cover == null) {
						ac = new AlbumCover(a.getId(), a.getName(), null, 0, 0, a.isDirty());
					} else {
						ac = new AlbumCover(a.getId(), a.getName(), cover.getName(),
								cover.getImageHeight(), cover.getImageWidth(), a.isDirty());
						imageUtils.appendImageDimensions(ac);
						imageUtils.appendImagePaths(ac, cover.getThumbLastModified().getTime());
					}
					return ac;
				})
				.collect(Collectors.toList());
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
