package image.persistence.repository;

import image.persistence.entity.Album;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Created by adrianpetre on 23.02.2018.
 */
public interface AlbumRepository {
    @Transactional
    List<Album> getAlbumsOrderedByName();

    @Transactional
    Album createAlbum(String name);

    @Transactional
    Album getAlbumById(Integer id);

    @Transactional
    Album getAlbumByName(String name);

    @Transactional
    boolean putAlbumCover(Integer imageId);

    @Transactional
    boolean removeAlbumCover(Integer albumId);

    @Transactional
    boolean clearDirtyForAlbum(Integer albumId);
}
