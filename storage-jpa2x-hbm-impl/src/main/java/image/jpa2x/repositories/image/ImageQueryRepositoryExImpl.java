package image.jpa2x.repositories.image;

import image.persistence.entity.Image;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public class ImageQueryRepositoryExImpl implements ImageQueryRepositoryEx {
	private final ImageRepository imageRepository;

	public ImageQueryRepositoryExImpl(ImageRepository imageRepository) {this.imageRepository = imageRepository;}

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
