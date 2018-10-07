package image.persistence.repositories;

import image.persistence.entity.Album;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AlbumRepository extends AlbumRepositoryCustom, PagingAndSortingRepository<Album, Integer> {}
