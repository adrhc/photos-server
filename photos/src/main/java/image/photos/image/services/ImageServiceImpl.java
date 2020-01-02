package image.photos.image.services;

import image.jpa2x.repositories.AlbumRepository;
import image.jpa2x.repositories.ImageRepository;
import image.persistence.entity.Image;
import image.photos.album.helpers.AlbumHelper;
import image.photos.infrastructure.filestore.FileStoreService;
import org.apache.commons.io.FilenameUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.file.Path;
import java.util.List;

import static image.photos.image.helpers.ImageHelper.relativeFilePathFor;

@Service
@Transactional
public class ImageServiceImpl implements ImageService {
	private final ImageRepository imageRepository;
	private final AlbumRepository albumRepository;
	private final FileStoreService fileStoreService;
	private final AlbumHelper albumHelper;


	public ImageServiceImpl(ImageRepository imageRepository, AlbumRepository albumRepository, FileStoreService fileStoreService, AlbumHelper albumHelper) {
		this.imageRepository = imageRepository;
		this.albumRepository = albumRepository;
		this.fileStoreService = fileStoreService;
		this.albumHelper = albumHelper;
	}

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

	/**
	 * @return whether imgFile from albumId exists in other albums too
	 */
	@Override
	public boolean imageExistsInOtherAlbum(Path imgFile, Integer albumId) {
		String nameNoExt = FilenameUtils.getBaseName(this.fileStoreService.fileName(imgFile));
		List<Image> image = this.imageRepository.findDuplicates(nameNoExt, albumId);
		long imgFileSize = this.fileStoreService.fileSize(imgFile);
		return image.stream().anyMatch(i -> imgFileSize == fileSizeOf(i));
	}

	/**
	 * @return size of the image's file
	 */
	private long fileSizeOf(Image image) {
		String imgRelPath = relativeFilePathFor(image);
		Path imgFullPath = this.albumHelper.absolutePathOf(imgRelPath);
		return this.fileStoreService.fileSize(imgFullPath);
	}
}
