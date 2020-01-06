package image.jpa2x.repositories;

public interface AlbumRepositoryCustom {
	boolean putAlbumCover(Integer imageId);

	boolean removeAlbumCover(Integer albumId);

	boolean clearDirty(Integer albumId);

	boolean markAsDirty(Integer albumId);
}
