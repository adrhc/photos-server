package image.photos.infrastructure.database;

import image.jpa2x.repositories.ImageRepository;
import image.persistence.entity.Image;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
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
