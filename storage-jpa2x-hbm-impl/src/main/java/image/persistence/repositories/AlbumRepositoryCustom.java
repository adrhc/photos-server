package image.persistence.repositories;

import image.persistence.entity.Album;

public interface AlbumRepositoryCustom {
	boolean putAlbumCover(Integer imageId);

	Album createByName(String name);

	boolean removeAlbumCover(Integer albumId);

	boolean clearDirtyForAlbum(Integer albumId);
}
