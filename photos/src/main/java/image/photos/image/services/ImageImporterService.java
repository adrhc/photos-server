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
import java.util.function.Supplier;

import static image.jpa2x.util.ImageUtils.imageNameFrom;
import static image.photos.image.services.CategorizedUnsafeProcessing.heavyImport;
import static image.photos.image.services.CategorizedUnsafeProcessing.lightweightImport;

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
	 * @return CategorizedUnsafeProcessing(s) containing the DB operation to be performed
	 */
	public Optional<CategorizedUnsafeProcessing<Supplier<ImageEvent>,
			FileNotFoundException>> importFromFile(Path imgFile, Album album) {
		assert !this.fileStoreService.isDirectory(imgFile) :
				"Wrong image file (is a directory):\n{}" + imgFile;

		// load Image from DB (if any)
		Image dbImage = this.imageQueryService
				.findByNameAndAlbumId(imageNameFrom(imgFile), album.getId());

		if (dbImage == null) {
			// create db-image
			return Optional.of(heavyImport(() -> this.createFromFile(imgFile, album)));
		} else {
			// update db-image
			return this.updateFromFile(imgFile, dbImage);
		}
	}

	private Supplier<ImageEvent> createFromFile(Path imgFile, Album album) throws FileNotFoundException {
		String imageName = imageNameFrom(imgFile);
		log.debug("{}/{}", album.getName(), imageName);
		Image image = new Image();
		image.setImageMetadata(this.exifExtractorService.extractMetadata(imgFile));
		image.setName(imageName);
		image.setAlbum(album);
		// returns DB operation only
		return () -> this.imageCUDService.persist(image);
	}

	private Optional<CategorizedUnsafeProcessing<Supplier<ImageEvent>,
			FileNotFoundException>> updateFromFile(Path imgFile, Image image) {
		var dbImageLastModified = image.getImageMetadata().getDateTime();
		var imageLastModifiedFromFile = this.fileStoreService.lastModifiedTime(imgFile);

		// update ImageMetadata (thumbLastModified too) if image-file is newer
		if (imageLastModifiedFromFile > dbImageLastModified.getTime()) {
			// extractMetadata updates thumb lastModified date too!
			return Optional.of(heavyImport(() -> {
				ImageMetadata updatedImageMetadata =
						this.exifExtractorService.extractMetadata(imgFile);
				// returns DB operation only
				return () -> this.imageCUDService
						.updateImageMetadata(updatedImageMetadata, image.getId());
			}));
		}

		// update thumbLastModified if thumb-file is newer
		var dbThumbLastModified = image.getImageMetadata().getThumbLastModified();
		Date thumbLastModifiedFromFile = this.thumbHelper
				.thumbLastModified(imgFile, dbThumbLastModified);
		if (thumbLastModifiedFromFile.after(dbThumbLastModified)) {
			// returns DB operation only
			return Optional.of(lightweightImport(() -> () -> this.imageCUDService
					.updateThumbLastModified(thumbLastModifiedFromFile, image.getId())));
		}

		return Optional.empty();
	}
}
