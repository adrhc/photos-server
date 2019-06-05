package image.photos.image;

import image.jpa2x.repositories.AlbumRepository;
import image.jpa2x.repositories.ImageRepository;
import image.persistence.entity.Image;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class ImageServiceImpl implements ImageService {
	@Autowired
	private AlbumRepository albumRepository;
	@Autowired
	private ImageRepository imageRepository;

	/**
	 * this implementation approach make sense only when
	 * 2nd level cache is present on Album.images collection!
	 * <p>
	 * competes with ImageRepository.findByNameAndAlbumId
	 */
/*
	@Override
	public Image findByNameAndAlbumId(String name, Integer albumId) {
		return this.albumRepository.getById(albumId).getImages().stream()
				.filter(i -> i.getName().equals(name)).findAny().orElse(null);
	}
*/

	/**
	 * this is the best approach:
	 * take the imageId then load the Image data (which could be from 2nd level cache)
	 * <p>
	 * competes with ImageRepository.findByNameAndAlbumId
	 */
	@Override
	public Image findByNameAndAlbumId(String name, Integer albumId) {
		Integer imageId = this.imageRepository.findIdByNameAndAlbumId(name, albumId);
		return this.imageRepository.getById(imageId);
	}
}
