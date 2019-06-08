package image.photos.album;

import image.jpa2x.repositories.AlbumRepository;
import image.persistence.entity.Image;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional
@Service
public class AlbumServiceImpl implements AlbumService {
	@Autowired
	private AlbumRepository albumRepository;

	/**
	 * this implementation approach make more sense when
	 * 2nd level cache is set on Album.images collection!
	 * <p>
	 * competes with ImageRepository.findByAlbumId
	 */
	@Override
	public List<Image> getImages(Integer albumId) {
		List<Image> images = this.albumRepository.getById(albumId).getImages();
		// just initialize the collection
		images.size();
		return images;
	}
}
