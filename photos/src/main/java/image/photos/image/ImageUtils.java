package image.photos.image;

import image.cdm.album.cover.AlbumCover;
import image.cdm.image.feature.IImageBasicInfo;
import image.cdm.image.feature.IImageDimensions;
import image.jpa2x.repositories.AppConfigRepository;
import image.jpa2x.repositories.ImageRepository;
import image.persistence.entity.Image;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.File;
import java.nio.file.Path;
import java.text.MessageFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by adr on 2/2/18.
 */
@Component
@Slf4j
public class ImageUtils {
	private MessageFormat fullPathFormatter = new MessageFormat("{0}/{1}");
	private MessageFormat relativePathFormatter = new MessageFormat("{0}/{1,number,#}/{2}");
	@Value("${thumbs.dir}")
	private String thumbsDir;
	@Value("${albums.dir}")
	private String albumsDir;
	@Value("${max.thumb.size}")
	private double maxThumbSize;
	@Value("${max.thumb.size}")
	private int maxThumbSizeInt;
	@Autowired
	private ImageRepository imageRepository;
	@Autowired
	private AppConfigRepository appConfigRepository;

	public void appendImagePaths(List<? extends IImageBasicInfo> imageBasicInfos) {
		for (IImageBasicInfo basicInfo : imageBasicInfos) {
			if (basicInfo.getImgName() == null) {
				continue;
			}
			appendImagePaths(basicInfo);
		}
	}

	private void appendImagePaths(IImageBasicInfo basicInfo) {
		String albumName = basicInfo.getAlbumName();
		String imgName = basicInfo.getImgName();

		Long thumbLastModified = basicInfo.getThumbLastModified().getTime();
		Long imageLastModified = basicInfo.getDateTime().getTime();

		basicInfo.setThumbPath(thumbPathFor(thumbLastModified, imgName, albumName));
		basicInfo.setImagePath(imagePathFor(imageLastModified, imgName, albumName));
	}

	public void appendImagePaths(AlbumCover albumCover, Long thumbLastModified) {
		String albumName = albumCover.getAlbumName();
		String imgName = albumCover.getImgName();

		albumCover.setThumbPath(thumbPathFor(thumbLastModified, imgName, albumName));
	}

	private String thumbPathFor(Long thumbLastModified, String imgName, String albumName) {
		String relativePath = this.relativePathFormatter.format(
				new Object[]{albumName, thumbLastModified, imgName});
		return this.fullPathFormatter.format(new Object[]{this.thumbsDir, relativePath});
	}

	private String imagePathFor(Long imageLastModif, String imgName, String albumName) {
		String relativePath = this.relativePathFormatter.format(
				new Object[]{albumName, imageLastModif, imgName});
		return this.fullPathFormatter.format(new Object[]{this.albumsDir, relativePath});
	}

	public void appendImageDimensions(List<? extends IImageDimensions> imageDimensions) {
		for (IImageDimensions entity : imageDimensions) {
			appendImageDimensions(entity);
		}
	}

	public void appendImageDimensions(IImageDimensions entity) {
		if (entity.getImageHeight() < entity.getImageWidth()) {
			entity.setImageHeight((int)
					Math.floor(this.maxThumbSize * entity.getImageHeight() / entity.getImageWidth()));
			entity.setImageWidth(this.maxThumbSizeInt);
		} else {
			entity.setImageWidth((int)
					Math.floor(this.maxThumbSize * entity.getImageWidth() / entity.getImageHeight()));
			entity.setImageHeight(this.maxThumbSizeInt);
		}
	}

	public String changeToOppositeExtensionCase(String fileName) {
		StringBuilder sb = new StringBuilder(fileName);
		int idx = sb.lastIndexOf(".");
		if (idx <= 0) {
			return fileName;
		}
		sb.append(fileName.substring(0, idx));
		String pointAndExtension = fileName.substring(idx);
		if (pointAndExtension.equals(pointAndExtension.toLowerCase())) {
			sb.append(pointAndExtension.toUpperCase());
		} else {
			sb.append(pointAndExtension.toLowerCase());
		}
		return sb.toString();
	}

	public boolean imageExistsInOtherAlbum(File imgFile, Date exifDateTimeOriginal, Integer albumId) {
		List<Image> image = this.imageRepository
				.findDuplicates(
						FilenameUtils.getBaseName(imgFile.getName()),
						exifDateTimeOriginal, albumId
				);
		return image.stream().anyMatch(i -> imgFile.length() == sizeOf(i));
	}

	private long sizeOf(Image image) {
		String relPath = imagePathFor(image.getImageMetadata().getDateTime().getTime(),
				image.getName(), image.getAlbum().getName());
		Path path = Path.of(this.appConfigRepository.getAlbumsPath(), relPath);
		return path.toFile().length();
	}
}
