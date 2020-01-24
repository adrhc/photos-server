package image.photos.album.converter;

import image.cdm.album.cover.AlbumCover;
import image.jpa2x.helper.ImageHelper;
import image.persistence.entity.Album;
import image.persistence.entity.Image;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class AlbumToCoverConverter implements Converter<Album, AlbumCover> {
	private final ImageHelper imageHelper;

	public AlbumToCoverConverter(ImageHelper imageHelper) {this.imageHelper = imageHelper;}

	@Override
	public AlbumCover convert(Album album) {
		Image coverImg = album.getCover();
		AlbumCover ac;
		if (coverImg == null) {
			ac = new AlbumCover(album.getId(), album.getName(),
					album.isDirty(), album.getLastUpdate());
		} else {
			ac = new AlbumCover(album.getId(), album.getName(), coverImg.getName(),
					coverImg.getImageMetadata().getExifData().getImageHeight(),
					coverImg.getImageMetadata().getExifData().getImageWidth(),
					album.isDirty(), album.getLastUpdate());
			this.imageHelper.appendImageDimensions(ac);
			this.imageHelper.appendImagePaths(ac, coverImg.getImageMetadata().getThumbLastModified().getTime());
		}
		return ac;
	}
}
