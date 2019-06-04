package image.photos.image;

import image.jpa2x.repositories.AlbumRepository;
import image.persistence.entity.Image;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class ImageServiceImpl implements ImageService {
	@Autowired
	private AlbumRepository albumRepository;

	/**
	 * this implementation approach make sense only when
	 * 2nd level cache is present on Album.images collection!
	 */
	@Override
	public Image findByNameAndAlbumId(String name, Integer albumId) {
		return this.albumRepository.getById(albumId).getImages().stream()
				.filter(i -> i.getName().equals(name)).findAny().orElse(null);
	}
}
