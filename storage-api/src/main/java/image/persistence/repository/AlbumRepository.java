package image.persistence.repository;

import image.persistence.entity.Album;

import java.util.List;

/**
 * Created by adrianpetre on 23.02.2018.
 */
public interface AlbumRepository {
	List<Album> findByDeletedFalseOrderByNameDesc();

	Album createByName(String name);

	void persist(Album album);

	void deleteById(Integer id);

	Album getById(Integer id);

	Album findAlbumByName(String name);

	boolean putAlbumCover(Integer imageId);

	boolean removeAlbumCover(Integer albumId);

	boolean clearDirtyForAlbum(Integer albumId);
}
