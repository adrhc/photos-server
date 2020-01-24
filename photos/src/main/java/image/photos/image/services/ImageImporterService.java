package image.photos.image.services;

import image.infrastructure.messaging.image.ImageEvent;
import image.infrastructure.messaging.image.ImageEventTypeEnum;
import image.persistence.entity.Album;
import image.persistence.entity.Image;
import image.persistence.entity.image.ImageMetadata;
import image.photos.image.helpers.ThumbHelper;
import image.photos.infrastructure.database.ImageQueryRepositoryEx;
import image.photos.infrastructure.database.ImageUpdateRepositoryEx;
import image.photos.infrastructure.filestore.FileStoreService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Date;
import java.util.Optional;

import static image.jpa2x.util.ImageUtils.imageNameFrom;

@Component
@Slf4j
public class ImageImporterService {
	private final ExifExtractorService exifExtractorService;
	private final ImageQueryRepositoryEx imageQueryRepositoryEx;
	private final ThumbHelper thumbHelper;
	private final ImageUpdateRepositoryEx imageUpdateRepositoryEx;
	private final FileStoreService fileStoreService;

	public ImageImporterService(ExifExtractorService exifExtractorService,
			ImageQueryRepositoryEx imageQueryRepositoryEx,
			ThumbHelper thumbHelper, ImageUpdateRepositoryEx imageUpdateRepositoryEx,
			FileStoreService fileStoreService) {
		this.exifExtractorService = exifExtractorService;
		this.imageQueryRepositoryEx = imageQueryRepositoryEx;
		this.thumbHelper = thumbHelper;
		this.imageUpdateRepositoryEx = imageUpdateRepositoryEx;
		this.fileStoreService = fileStoreService;
	}

	public Optional<ImageEvent> importFromFile(Path imgFile, Album album) throws IOException {
		assert !this.fileStoreService.isDirectory(imgFile) :
				"Wrong image file (is a directory):\n{}" + imgFile;

		// load Image from DB (if any)
		Image dbImage = this.imageQueryRepositoryEx
				.findByNameAndAlbumId(imageNameFrom(imgFile), album.getId());

		if (dbImage == null) {
			// create db-image
			return Optional.of(this.createFromFile(imgFile, album));
		} else {
			// update db-image
			return this.updateFromFile(imgFile, dbImage);
		}
	}

	private ImageEvent createFromFile(Path imgFile, Album album) throws IOException {
		String imageName = imageNameFrom(imgFile);
		log.debug("{}/{}", album.getName(), imageName);
		Image image = new Image();
		image.setImageMetadata(this.exifExtractorService.extractMetadata(imgFile));
		image.setName(imageName);
		image.setAlbum(album);
		// returns DB operation only
		return this.imageUpdateRepositoryEx.persist(image);
	}

	private Optional<ImageEvent> updateFromFile(Path imgFile, Image image) throws IOException {
		var dbImageLastModified = image.getImageMetadata().getDateTime();
		var imageLastModifiedFromFile = this.fileStoreService.lastModifiedTime(imgFile);

		// update ImageMetadata (thumbLastModified too) if image-file is newer
		if (imageLastModifiedFromFile > dbImageLastModified.getTime()) {
			// extractMetadata updates thumb lastModified date too!
			ImageMetadata updatedImageMetadata =
					this.exifExtractorService.extractMetadata(imgFile);
			// returns DB operation only
			return Optional.of(this.imageUpdateRepositoryEx
					.updateImageMetadata(updatedImageMetadata, image.getId()));
		}

		// update thumbLastModified if thumb-file is newer
		var dbThumbLastModified = image.getImageMetadata().getThumbLastModified();
		Date thumbLastModifiedFromFile = this.thumbHelper
				.thumbLastModified(imgFile, dbThumbLastModified);
		if (thumbLastModifiedFromFile.after(dbThumbLastModified)) {
			// returns DB operation only
			return Optional.of(this.imageUpdateRepositoryEx
					.updateThumbLastModified(thumbLastModifiedFromFile, image.getId()));
		}

		return Optional.of(ImageEvent.of(image, ImageEventTypeEnum.NOTHING));
	}
}
