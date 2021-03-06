package image.jpa2x.repositories.album;

import image.cdm.album.cover.AlbumCover;
import image.jpa2x.converter.AlbumToCoverConverter;
import image.persistence.entity.Album;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by adr on 2/2/18.
 */
@Repository
public class AlbumCoverRepositoryImpl implements AlbumCoverRepository {
	private final AlbumToCoverConverter converter;
	private final AlbumRepository albumRepository;

	public AlbumCoverRepositoryImpl(AlbumToCoverConverter converter, AlbumRepository albumRepository) {
		this.converter = converter;
		this.albumRepository = albumRepository;
	}

	@Override
	public List<AlbumCover> getCovers() {
		return this.albumRepository
				.findByDeletedFalseOrderByNameDesc().stream()
				.map(this.converter::convert)
				.collect(Collectors.toList());
	}

	@Override
	public AlbumCover getCoverById(Integer albumId) {
		Album album = this.albumRepository.getById(albumId);
		return this.converter.convert(album);
	}

	@Override
	public AlbumCover getCoverByName(String albumName) {
		Album album = this.albumRepository.findByName(albumName);
		return this.converter.convert(album);
	}
}
