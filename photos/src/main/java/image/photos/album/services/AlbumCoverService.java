package image.photos.album.services;

import image.cdm.album.cover.AlbumCover;
import image.jpa2x.repositories.AlbumRepository;
import image.persistence.entity.Album;
import image.persistence.entity.Image;
import image.photos.image.helpers.ImageHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by adr on 2/2/18.
 */
@Component
public class AlbumCoverService {
	//	private static final Logger logger = LoggerFactory.getLogger(AlbumCoverService.class);
	@Autowired
	private AlbumRepository albumRepository;
	@Autowired
	private ImageHelper imageHelper;

	public List<AlbumCover> getCovers() {
		return this.albumRepository
				.findByDeletedFalseOrderByNameDesc().stream()
				.map(this::convertAlbumToCover)
				.collect(Collectors.toList());
	}

	public AlbumCover getCoverById(Integer albumId) {
		Album album = this.albumRepository.getById(albumId);
		return convertAlbumToCover(album);
	}

	public AlbumCover getCoverByName(String albumName) {
		Album album = this.albumRepository.findByName(albumName);
		return convertAlbumToCover(album);
	}

	private AlbumCover convertAlbumToCover(Album album) {
		Image coverImg = album.getCover();
		AlbumCover ac;
		if (coverImg == null) {
			ac = new AlbumCover(album.getId(), album.getName(),
					album.isDirty(), album.getLastUpdate());
		} else {
			ac = new AlbumCover(album.getId(), album.getName(), coverImg.getName(),
					coverImg.getImageMetadata().getExifData().getImageHeight(),
					coverImg.getImageMetadata().getExifData().getImageWidth(),
					album.isDirty(), album.getLastUpdate());
			this.imageHelper.appendImageDimensions(ac);
			this.imageHelper.appendImagePaths(ac, coverImg.getImageMetadata().getThumbLastModified().getTime());
		}
		return ac;
	}
}
