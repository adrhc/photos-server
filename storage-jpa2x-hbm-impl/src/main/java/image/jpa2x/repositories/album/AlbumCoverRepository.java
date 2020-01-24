package image.jpa2x.repositories.album;

import image.cdm.album.cover.AlbumCover;

import java.util.List;

public interface AlbumCoverRepository {
	List<AlbumCover> getCovers();

	AlbumCover getCoverById(Integer albumId);

	AlbumCover getCoverByName(String albumName);
}
