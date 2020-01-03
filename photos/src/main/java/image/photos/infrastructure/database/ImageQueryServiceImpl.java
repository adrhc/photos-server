package image.photos.infrastructure.database;

import image.jpa2x.repositories.AlbumRepository;
import image.jpa2x.repositories.ImageRepository;
import image.persistence.entity.Album;
import image.persistence.entity.Image;
import image.photos.album.helpers.AlbumHelper;
import image.photos.infrastructure.filestore.FileStoreService;
import org.apache.commons.io.FilenameUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.file.Path;
import java.util.List;

import static image.photos.image.helpers.ImageHelper.relativeFilePathFor;
import static image.photos.infrastructure.filestore.PathUtils.fileName;

@Service
@Transactional
public class ImageQueryServiceImpl implements ImageQueryService {
	private final ImageRepository imageRepository;
	private final AlbumRepository albumRepository;
	private final FileStoreService fileStoreService;
	private final AlbumHelper albumHelper;

	public ImageQueryServiceImpl(ImageRepository imageRepository, AlbumRepository albumRepository, FileStoreService fileStoreService, AlbumHelper albumHelper) {
		this.imageRepository = imageRepository;
		this.albumRepository = albumRepository;
		this.fileStoreService = fileStoreService;
		this.albumHelper = albumHelper;
	}

	/**
	 * competes with ImageQueryRepository.findByAlbumId
	 * <p>
	 * album.getImages() ignores 2nd level cache:
	 * select * from Image where FK_ALBUM=?
	 */
	@Override
	public List<Image> getImages(Integer albumId) {
		Album album = this.albumRepository.getById(albumId);
		List<Image> images = album.getImages();
		images.size();// initialize the lazy collection
		return images;
	}

	/*
	 * This implementation approach makes (some) sense only when
	 * 2nd level cache is present on Album.images collection!
	 * <p>
	 * competes with ImageRepository.findByNameAndAlbumId
	 * <p>
	 * runs very SLOW
	 */
/*
	@Override
	public Image findByNameAndAlbumId(String name, Integer albumId) {
		return this.albumRepository.getById(albumId).getImages().stream()
				.filter(i -> i.getName().equals(name)).findAny().orElse(null);
	}
*/

	/**
	 * this is the best approach when using 2nd level cache:
	 * take the imageId then load the Image
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
		String nameNoExt = FilenameUtils.getBaseName(fileName(imgFile));
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
