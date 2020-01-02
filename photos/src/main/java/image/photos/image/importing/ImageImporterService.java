package image.photos.image.importing;

import image.jpa2x.repositories.ImageRepository;
import image.persistence.entity.Album;
import image.persistence.entity.Image;
import image.persistence.entity.image.ImageMetadata;
import image.photos.image.ThumbHelper;
import image.photos.image.services.ExifExtractorService;
import image.photos.image.services.ImageService;
import image.photos.infrastructure.events.image.ImageEvent;
import image.photos.infrastructure.events.image.ImageEventTypeEnum;
import image.photos.infrastructure.events.image.ImageTopic;
import image.photos.infrastructure.filestore.FileStoreService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.nio.file.Path;
import java.util.Date;

@Component
@Slf4j
public class ImageImporterService {
	private final ExifExtractorService exifExtractorService;
	private final ImageService imageService;
	private final ThumbHelper thumbHelper;
	private final ImageRepository imageRepository;
	private final ImageTopic imageTopic;
	private final FileStoreService fileStoreService;

	public ImageImporterService(ExifExtractorService exifExtractorService, ImageService imageService, ThumbHelper thumbHelper, ImageRepository imageRepository, ImageTopic imageTopic, FileStoreService fileStoreService) {
		this.exifExtractorService = exifExtractorService;
		this.imageService = imageService;
		this.thumbHelper = thumbHelper;
		this.imageRepository = imageRepository;
		this.imageTopic = imageTopic;
		this.fileStoreService = fileStoreService;
	}

	/**
	 * @return true = file still exists, false = file no longer exists
	 */
	public boolean importImageFromFile(Path imgFile, Album album) {
		assert this.fileStoreService.isDirectory(imgFile) : "Wrong image file (is a directory):\n{}" + imgFile;
//		Image dbImage = this.imageRepository.findByNameAndAlbumId(imgFile.getName(), album.getId());
		Image dbImage = this.imageService.findByNameAndAlbumId(
				this.fileStoreService.fileName(imgFile), album.getId());
		if (dbImage == null) {
			// not found in DB? then add it
			return createImageFromFile(imgFile, album);
/*
		} else if (this.imageUtils.imageExistsInOtherAlbum(imgFile, album.getId())) {
			log.debug("Image {}\tto insert into album {} already exists in another album!",
					imgFile.getName(), album.getName());
			return false;
*/
		}

		if (this.fileStoreService.lastModifiedTime(imgFile) >
				dbImage.getImageMetadata().getDateTime().getTime()) {
			// check lastModified for image then extract EXIF and update
			return updateImageMetadataFromFile(imgFile, dbImage);
		} else {
			Date thumbLastModified = this.thumbHelper
					.getThumbLastModified(imgFile, dbImage.getImageMetadata().getDateTime());
			if (thumbLastModified.after(dbImage.getImageMetadata().getThumbLastModified())) {
				// check lastModified for thumb then update in DB lastModified date only
				updateThumbLastModifiedForImgFile(thumbLastModified, dbImage.getId());
			}
		}
		return true;
	}

	private void updateThumbLastModifiedForImgFile(Date thumbLastModified, Integer imageId) {
		Image updatedDbImg = this.imageRepository
				.updateThumbLastModifiedForImg(thumbLastModified, imageId);
		log.debug("updated thumb's lastModified for {}", updatedDbImg.getName());
		this.imageTopic.emit(ImageEvent.builder()
				.type(ImageEventTypeEnum.THUMB_LAST_MODIF_DATE_UPDATED)
				.image(updatedDbImg).build());
	}

	private boolean updateImageMetadataFromFile(Path imgFile, Image dbImage) {
		log.debug("update EXIF for {}/{}",
				imgFile.getFileName().toString(), dbImage.getName());
		ImageMetadata imageMetadata = this.exifExtractorService.extractMetadata(imgFile);
		if (imageMetadata == null) {
			log.info("{} no longer exists!", imgFile);
			return false;
		}
		Image imgWithUpdatedMetadata = this.imageRepository
				.updateImageMetadata(imageMetadata, dbImage.getId());
		this.imageTopic.emit(ImageEvent.builder()
				.type(ImageEventTypeEnum.EXIF_UPDATED)
				.image(imgWithUpdatedMetadata).build());
		return true;
	}

	private boolean createImageFromFile(Path imgFile, Album album) {
		ImageMetadata imageMetadata = this.exifExtractorService.extractMetadata(imgFile);
		if (imageMetadata == null) {
			log.info("{} no longer exists!", imgFile);
			return false;
		}
/*
		if (this.imageUtils.imageExistsInOtherAlbum(imgFile, album.getId())) {
			log.debug("Image {}\tto insert into album {} already exists in another album!",
					imgFile.getName(), album.getName());
			return false;
		}
*/
		log.debug("insert {}/{}", album.getName(), this.fileStoreService.fileName(imgFile));
		Image newImg = new Image();
		newImg.setImageMetadata(imageMetadata);
		newImg.setName(imgFile.getFileName().toString());
		newImg.setAlbum(album);
		this.imageRepository.persist(newImg);
		this.imageTopic.emit(ImageEvent.builder()
				.type(ImageEventTypeEnum.CREATED)
				.image(newImg).build());
		return true;
	}
}
