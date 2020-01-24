package image.photos.album.services;

import image.cdm.album.cover.AlbumCover;
import image.jpa2x.repositories.album.AlbumRepository;
import image.persistence.entity.Album;
import org.springframework.core.convert.ConversionService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by adr on 2/2/18.
 */
@Service
public class AlbumCoverService {
	private final ConversionService cs;
	private final AlbumRepository albumRepository;

	public AlbumCoverService(ConversionService cs, AlbumRepository albumRepository) {
		this.cs = cs;
		this.albumRepository = albumRepository;
	}

	public List<AlbumCover> getCovers() {
		return this.albumRepository
				.findByDeletedFalseOrderByNameDesc().stream()
				.map(it -> this.cs.convert(it, AlbumCover.class))
				.collect(Collectors.toList());
	}

	public AlbumCover getCoverById(Integer albumId) {
		Album album = this.albumRepository.getById(albumId);
		return this.cs.convert(album, AlbumCover.class);
	}

	public AlbumCover getCoverByName(String albumName) {
		Album album = this.albumRepository.findByName(albumName);
		return this.cs.convert(album, AlbumCover.class);
	}
}
