package image.jpa2x.repositories.album;

public interface AlbumUpdateRepository {
	boolean putAlbumCover(Integer imageId);

	boolean removeAlbumCover(Integer albumId);

	boolean clearDirty(Integer albumId);

	boolean markAsDirty(Integer albumId);
}
