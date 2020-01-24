package image.jpa2xtests.repositories;

import exifweb.util.random.RandomBeansExtensionEx;
import image.jpa2x.repositories.album.AlbumRepository;
import image.jpa2xtests.config.Junit5Jpa2xStageDbConfig;
import image.persistence.entity.Album;
import image.persistence.entitytests.IAlbumSupplier;
import io.github.glytching.junit.extension.random.Random;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;

@ExtendWith(RandomBeansExtensionEx.class)
@Junit5Jpa2xStageDbConfig
@Slf4j
class AlbumRepositoryStageTest implements IAlbumSupplier {
	@Autowired
	private AlbumRepository albumRepository;

	@Random(type = Album.class, size = 50, excludes = {"id", "dirty", "images", "cover", "lastUpdate"})
	private List<Album> albums;

	@BeforeAll
	void beforeAll() {
		this.albums.forEach(this.albumRepository::persist);
	}

	@AfterAll
	void afterAll() {
		this.albums.forEach(a -> this.albumRepository.deleteById(a.getId()));
	}

	@Test
	void findByDeletedFalseOrderByNameDesc() {
		List<String> descSortedNames = this.notDeletedAlbumNamesDesc(this.albums);
		List<Album> dbAlbums = this.albumRepository.findByDeletedFalseOrderByNameDesc();
		List<String> descDbSortedNames = this.notDeletedAlbumNames(dbAlbums);
		descDbSortedNames.retainAll(descSortedNames);
		assertThat("size", descDbSortedNames, hasSize(descSortedNames.size()));
		// fails with org.hibernate.dialect.H2Dialect (order not solved by H2)
		assertThat("same order", descDbSortedNames, equalTo(descSortedNames));
	}
}
