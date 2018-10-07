package image.persistence.entity;

import image.persistence.repository.util.random.IEnhancedRandom;
import image.persistence.util.IPositiveIntegerRandom;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by adr on 2/25/18.
 */
public interface IAlbumSupplier extends IEnhancedRandom, IPositiveIntegerRandom {
	default String supplyAlbumName() {
		return "album-" + randomPositiveInt();
	}

	default List<String> notDeletedAlbumNames(List<Album> albums) {
		return albums.stream()
				.filter(a -> !a.isDeleted())
				.map(Album::getName)
				.collect(Collectors.toList());
	}

	default List<String> notDeletedAlbumNamesDesc(List<Album> albums) {
		return albums.stream()
				.filter(a -> !a.isDeleted())
				.map(Album::getName)
				.sorted((o1, o2) -> o2.toLowerCase().compareTo(o1.toLowerCase()))
				.collect(Collectors.toList());
	}
}
