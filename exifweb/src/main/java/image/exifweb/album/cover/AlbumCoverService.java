package image.exifweb.album.cover;

import image.cdm.album.cover.AlbumCover;
import image.exifweb.image.ImageUtils;
import image.exifweb.system.persistence.repositories.AlbumRepository;
import image.persistence.entity.Album;
import image.persistence.entity.Image;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by adr on 2/2/18.
 */
@Component
public class AlbumCoverService {
	//	private static final Logger logger = LoggerFactory.getLogger(AlbumCoverService.class);
	@Inject
	private AlbumRepository albumRepository;
	@Inject
	private ImageUtils imageUtils;

	public List<AlbumCover> getCovers() {
		return albumRepository.getAlbumsOrderedByName().stream()
				.map(this::convertAlbumToCover)
				.collect(Collectors.toList());
	}

	public AlbumCover getCoverById(Integer albumId) {
		Album album = albumRepository.getAlbumById(albumId);
		return convertAlbumToCover(album);
	}

	public AlbumCover getCoverByName(String albumName) {
		Album album = albumRepository.getAlbumByName(albumName);
		return convertAlbumToCover(album);
	}

	private AlbumCover convertAlbumToCover(Album album) {
		Image cover = album.getCover();
		AlbumCover ac;
		if (cover == null) {
			ac = new AlbumCover(album.getId(), album.getName(),
					album.isDirty(), album.getLastUpdate());
		} else {
			ac = new AlbumCover(album.getId(), album.getName(), cover.getName(),
					cover.getImageMetadata().getExifData().getImageHeight(),
					cover.getImageMetadata().getExifData().getImageWidth(),
					album.isDirty(), album.getLastUpdate());
			imageUtils.appendImageDimensions(ac);
			imageUtils.appendImagePaths(ac, cover.getImageMetadata().getThumbLastModified().getTime());
		}
		return ac;
	}
}
