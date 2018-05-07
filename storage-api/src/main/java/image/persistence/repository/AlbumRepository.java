package image.persistence.repository;

import image.persistence.entity.Album;

import java.util.List;

/**
 * Created by adrianpetre on 23.02.2018.
 */
public interface AlbumRepository {
	List<Album> getAlbumsOrderedByName();

	Album createAlbum(String name);

	void createAlbum(Album album);

	void deleteAlbum(Integer id);

	Album getAlbumById(Integer id);

	Album getAlbumByName(String name);

	boolean putAlbumCover(Integer imageId);

	boolean removeAlbumCover(Integer albumId);

	boolean clearDirtyForAlbum(Integer albumId);
}
