package image.photos.image;

import image.jpa2x.repositories.AlbumRepository;
import image.jpa2x.repositories.ImageRepository;
import image.persistence.entity.Image;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class ImageServiceImpl implements ImageService {
	@Autowired
	private ImageRepository imageRepository;
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

	/**
	 * this implementation approach make sense only when
	 * 2nd level cache is present on Album.images collection!
	 * <p>
	 * competes with ImageRepository.findByNameAndAlbumId
	 * <p>
	 * run very SLOW
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
		// not cached query
		Integer imageId = this.imageRepository.findIdByNameAndAlbumId(name, albumId);
		if (imageId == null) {
			return null;
		}
		// Image is cached by id
		return this.imageRepository.getById(imageId);
	}
}
