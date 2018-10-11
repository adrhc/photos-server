package image.persistence.services;

import image.persistence.config.Junit5Jpa2xInMemoryDbConfig;
import image.persistence.entity.Album;
import image.persistence.entity.IAppConfigSupplier;
import image.persistence.entity.Image;
import image.persistence.entity.enums.AppConfigEnum;
import image.persistence.repositories.AlbumRepository;
import image.persistence.repositories.AppConfigRepository;
import image.persistence.repository.AlbumPageRepository;
import image.persistence.repository.CacheStatisticsRepository;
import image.persistence.repository.util.random.RandomBeansExtensionEx;
import image.persistence.util.IPositiveIntegerRandom;
import io.github.glytching.junit.extension.random.Random;
import net.jcip.annotations.NotThreadSafe;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;

import javax.inject.Inject;
import java.util.List;

@ExtendWith(RandomBeansExtensionEx.class)
@Junit5Jpa2xInMemoryDbConfig
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@NotThreadSafe
class CacheStatisticsRepositoryTest implements IPositiveIntegerRandom, IAppConfigSupplier {
	@Inject
	private CacheStatisticsRepository cacheStatisticsRepository;
	@Inject
	private AppConfigRepository appConfigRepository;
	@Inject
	private AlbumRepository albumRepository;
	@Inject
	private AlbumPageRepository albumPageRepository;

	@Random(type = Album.class, size = 5, excludes = {"id", "dirty", "images", "cover", "lastUpdate"})
	private List<Album> albums;

	@BeforeAll
	void givenAlbums() {
		// add images to albums
		this.albums.forEach(a -> {
			a.addImages(randomInstanceList(randomPositiveInt(1, 10), false, Image.class));
		});
		// insert albums
		this.albums.forEach(this.albumRepository::persist);
		// create photos_per_page app config
		this.appConfigRepository.persist(entityAppConfigOf(AppConfigEnum.photos_per_page, "5"));
	}

	@AfterAll
	void afterAll() {
		this.albums.forEach(a -> this.albumRepository.deleteById(a.getId()));
	}

	@Test
	void getSecondLevelCacheStatistics() {
		// cache loading
		this.albumRepository.getById(this.albums.get(0).getId());
		this.albumPageRepository.countPages(null, false, false, AlbumPageRepository.NULL_ALBUM_ID);
		this.cacheStatisticsRepository.getSecondLevelCacheStatistics(Album.class.getName());
	}
}
