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
	 * this implementation approach make sense only when
	 * 2nd level cache is present on Album.images collection!
	 * <p>
	 * competes with ImageRepository.findByAlbumId
	 * <p>
	 * it's very slow comparing with ImageRepository.findByAlbumId
	 */
	@Override
	public List<Image> getImages(Integer albumId) {
		List<Image> images = this.albumRepository.getById(albumId).getImages();
		images.size();// just initialize the collection
		return images;
	}
}
