package image.photos.album;

import image.persistence.entity.Image;

import java.util.List;

public interface AlbumService {
	List<Image> getImages(Integer albumId);
}
