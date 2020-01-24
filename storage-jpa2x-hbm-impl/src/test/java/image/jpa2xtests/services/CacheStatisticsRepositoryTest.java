package image.jpa2xtests.services;

import exifweb.util.random.IPositiveIntegerRandom;
import exifweb.util.random.RandomBeansExtensionEx;
import image.jpa2x.repositories.album.AlbumPageRepository;
import image.jpa2x.repositories.album.AlbumRepository;
import image.jpa2x.repositories.appconfig.AppConfigRepository;
import image.jpa2x.services.CacheStatisticsRepository;
import image.jpa2xtests.config.Junit5Jpa2xInMemoryDbConfig;
import image.persistence.entity.Album;
import image.persistence.entity.Image;
import image.persistence.entity.enums.AppConfigEnum;
import image.persistence.entitytests.IAppConfigSupplier;
import io.github.glytching.junit.extension.random.Random;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@ExtendWith(RandomBeansExtensionEx.class)
@Junit5Jpa2xInMemoryDbConfig
class CacheStatisticsRepositoryTest implements IPositiveIntegerRandom, IAppConfigSupplier {
	@Autowired
	private CacheStatisticsRepository cacheStatisticsRepository;
	@Autowired
	private AppConfigRepository appConfigRepository;
	@Autowired
	private AlbumRepository albumRepository;
	@Autowired
	private AlbumPageRepository albumPageRepository;

	@Random(type = Album.class, size = 5, excludes = {"id", "dirty", "images", "cover", "lastUpdate"})
	private List<Album> albums;

	@BeforeAll
	void beforeAll() {
		// add images to albums
		this.albums.forEach(a -> {
			a.addImages(this.randomInstanceList(this.randomPositiveInt(1, 10), false, Image.class));
		});
		// insert albums
		this.albums.forEach(this.albumRepository::persist);
		// create photos_per_page app config
		this.appConfigRepository.persist(this.entityAppConfigOf(AppConfigEnum.photos_per_page, "5"));
	}

	@AfterAll
	void afterAll() {
		this.albums.forEach(a -> this.albumRepository.deleteById(a.getId()));
	}

	@Test
	@Disabled("CacheStatisticsRepository is not implemented yet!")
	void getSecondLevelCacheStatistics() {
		// cache loading
		this.albumRepository.getById(this.albums.get(0).getId());
		this.albumPageRepository.countPages(null, false, false, AlbumPageRepository.NULL_ALBUM_ID);
		this.cacheStatisticsRepository.getSecondLevelCacheStatistics(Album.class.getName());
	}
}
