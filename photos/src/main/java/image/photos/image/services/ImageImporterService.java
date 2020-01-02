package image.photos.image.services;

import image.persistence.entity.Album;
import image.persistence.entity.Image;
import image.persistence.entity.image.ImageMetadata;
import image.photos.image.helpers.ThumbHelper;
import image.photos.infrastructure.database.ImageCUDService;
import image.photos.infrastructure.database.ImageQueryService;
import image.photos.infrastructure.filestore.FileStoreService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.nio.file.Path;
import java.util.Date;

@Component
@Slf4j
public class ImageImporterService {
	private final ExifExtractorService exifExtractorService;
	private final ImageQueryService imageQueryService;
	private final ThumbHelper thumbHelper;
	private final ImageCUDService imageCUDService;
	private final FileStoreService fileStoreService;

	public ImageImporterService(ExifExtractorService exifExtractorService, ImageQueryService imageQueryService, ThumbHelper thumbHelper, ImageCUDService imageCUDService, FileStoreService fileStoreService) {
		this.exifExtractorService = exifExtractorService;
		this.imageQueryService = imageQueryService;
		this.thumbHelper = thumbHelper;
		this.imageCUDService = imageCUDService;
		this.fileStoreService = fileStoreService;
	}

	/**
	 * @return true = file still exists, false = file no longer exists
	 */
	public boolean importImageFromFile(Path imgFile, Album album) {
		assert this.fileStoreService.isDirectory(imgFile) : "Wrong image file (is a directory):\n{}" + imgFile;
//		Image dbImage = this.imageRepository.findByNameAndAlbumId(imgFile.getName(), album.getId());
		Image dbImage = this.imageQueryService.findByNameAndAlbumId(
				this.fileStoreService.fileName(imgFile), album.getId());
		if (dbImage == null) {
			// not found in DB? then add it
			return createImageFromFile(imgFile, album);
		}

		var dbImageLastModified = dbImage.getImageMetadata().getDateTime();
		var imageLastModifiedFromFile = this.fileStoreService.lastModifiedTime(imgFile);

		// check whether image-file is updated
		if (imageLastModifiedFromFile > dbImageLastModified.getTime()) {
			// extractMetadata updates thumb lastModified date too!
			ImageMetadata updatedImageMetadata =
					this.exifExtractorService.extractMetadata(imgFile);
			if (updatedImageMetadata == null) {
				log.info("{} no longer exists!", imgFile);
				return false;
			}
			this.imageCUDService.updateImageMetadata(updatedImageMetadata, dbImage.getId());
			return true;
		}

		// check whether thumb-file is updated
		var dbThumbLastModified = dbImage.getImageMetadata().getThumbLastModified();
		Date thumbLastModifiedFromFile = this.thumbHelper
				.thumbLastModified(imgFile, dbThumbLastModified);
		if (thumbLastModifiedFromFile.after(dbThumbLastModified)) {
			this.imageCUDService.updateThumbLastModified(thumbLastModifiedFromFile, dbImage.getId());
		}

		return true;
	}

	private boolean createImageFromFile(Path imgFile, Album album) {
		ImageMetadata imageMetadata = this.exifExtractorService.extractMetadata(imgFile);
		if (imageMetadata == null) {
			log.info("{} no longer exists!", imgFile);
			return false;
		}
		log.debug("insert {}/{}", album.getName(), this.fileStoreService.fileName(imgFile));
		Image newImg = new Image();
		newImg.setImageMetadata(imageMetadata);
		newImg.setName(imgFile.getFileName().toString());
		newImg.setAlbum(album);
		this.imageCUDService.persist(newImg);
		return true;
	}
}
