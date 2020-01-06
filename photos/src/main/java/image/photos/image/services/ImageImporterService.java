package image.photos.image.services;

import image.infrastructure.messaging.image.ImageEvent;
import image.persistence.entity.Album;
import image.persistence.entity.Image;
import image.persistence.entity.image.ImageMetadata;
import image.photos.image.helpers.ThumbHelper;
import image.photos.infrastructure.database.ImageCUDService;
import image.photos.infrastructure.database.ImageQueryService;
import image.photos.infrastructure.filestore.FileStoreService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.FileNotFoundException;
import java.nio.file.Path;
import java.util.Date;
import java.util.Optional;

import static image.photos.infrastructure.filestore.PathUtils.fileName;

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
	 * @return true = DB synced with the imgFile, false = nothing changed
	 */
	public Optional<ImageEvent> importFromFile(Path imgFile, Album album) throws FileNotFoundException {
		assert !this.fileStoreService.isDirectory(imgFile) :
				"Wrong image file (is a directory):\n{}" + imgFile;

		// load Image from DB (if any)
		Image dbImage = this.imageQueryService
				.findByNameAndAlbumId(fileName(imgFile), album.getId());

		if (dbImage == null) {
			// create db-image
			return Optional.of(createFromFile(imgFile, album));
		} else {
			// update db-image
			return updateFromFile(imgFile, dbImage);
		}
	}

	private ImageEvent createFromFile(Path imgFile, Album album) throws FileNotFoundException {
		log.debug("insert {}/{}", album.getName(), fileName(imgFile));
		Image image = new Image();
		image.setImageMetadata(this.exifExtractorService.extractMetadata(imgFile));
		image.setName(fileName(imgFile));
		image.setAlbum(album);
		return this.imageCUDService.persist(image);
	}

	private Optional<ImageEvent> updateFromFile(Path imgFile, Image image) throws FileNotFoundException {
		var dbImageLastModified = image.getImageMetadata().getDateTime();
		var imageLastModifiedFromFile = this.fileStoreService.lastModifiedTime(imgFile);

		// update ImageMetadata (thumbLastModified too) if image-file is newer
		if (imageLastModifiedFromFile > dbImageLastModified.getTime()) {
			// extractMetadata updates thumb lastModified date too!
			ImageMetadata updatedImageMetadata =
					this.exifExtractorService.extractMetadata(imgFile);
			return Optional.of(this.imageCUDService.updateImageMetadata(updatedImageMetadata, image.getId()));
		}

		// update thumbLastModified if thumb-file is newer
		var dbThumbLastModified = image.getImageMetadata().getThumbLastModified();
		Date thumbLastModifiedFromFile = this.thumbHelper
				.thumbLastModified(imgFile, dbThumbLastModified);
		if (thumbLastModifiedFromFile.after(dbThumbLastModified)) {
			return Optional.of(this.imageCUDService.updateThumbLastModified(thumbLastModifiedFromFile, image.getId()));
		}

		return Optional.empty();
	}
}
