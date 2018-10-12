package image.persistence.repositories;

import image.persistence.config.Junit5Jpa2xStageDbConfig;
import image.persistence.entity.Album;
import image.persistence.entity.IAlbumSupplier;
import exifweb.util.random.RandomBeansExtensionEx;
import io.github.glytching.junit.extension.random.Random;
import lombok.extern.slf4j.Slf4j;
import net.jcip.annotations.NotThreadSafe;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;

@ExtendWith(RandomBeansExtensionEx.class)
@NotThreadSafe
@Junit5Jpa2xStageDbConfig
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Slf4j
class AlbumRepositoryStageTest implements IAlbumSupplier {
	@Autowired
	private AlbumRepository albumRepository;

	@Random(type = Album.class, size = 50, excludes = {"id", "dirty", "images", "cover", "lastUpdate"})
	private List<Album> albums;

	@BeforeAll
	void givenAlbums() {
		this.albums.forEach(this.albumRepository::persist);
	}

	@AfterAll
	void afterAll() {
		this.albums.forEach(a -> this.albumRepository.deleteById(a.getId()));
	}

	@Test
	void findByDeletedFalseOrderByNameDesc() {
		List<String> descSortedNames = notDeletedAlbumNamesDesc(this.albums);
		List<Album> dbAlbums = this.albumRepository.findByDeletedFalseOrderByNameDesc();
		List<String> descDbSortedNames = notDeletedAlbumNames(dbAlbums);
		descDbSortedNames.retainAll(descSortedNames);
		assertThat("size", descDbSortedNames, hasSize(descSortedNames.size()));
		// fails with org.hibernate.dialect.H2Dialect (order not solved by H2)
		assertThat("same order", descDbSortedNames, equalTo(descSortedNames));
	}
}
