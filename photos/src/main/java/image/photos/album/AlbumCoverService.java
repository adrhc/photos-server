package image.photos.album;

import image.cdm.album.cover.AlbumCover;
import image.persistence.entity.Album;
import image.persistence.entity.Image;
import image.persistence.repository.AlbumRepository;
import image.photos.image.ImageUtils;
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
		return this.albumRepository.findByDeletedFalseOrderByNameDesc().stream()
				.map(this::convertAlbumToCover)
				.collect(Collectors.toList());
	}

	public AlbumCover getCoverById(Integer albumId) {
		Album album = this.albumRepository.getById(albumId);
		return convertAlbumToCover(album);
	}

	public AlbumCover getCoverByName(String albumName) {
		Album album = this.albumRepository.findAlbumByName(albumName);
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
			this.imageUtils.appendImageDimensions(ac);
			this.imageUtils.appendImagePaths(ac, cover.getImageMetadata().getThumbLastModified().getTime());
		}
		return ac;
	}
}
