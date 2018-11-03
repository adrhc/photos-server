package image.photos.junit5.album;

import exifweb.util.MiscUtils;
import exifweb.util.random.IPositiveIntegerRandom;
import exifweb.util.random.RandomBeansExtensionEx;
import image.cdm.album.page.AlbumPage;
import image.cdm.image.status.EImageStatus;
import image.jpa2x.repositories.AlbumRepository;
import image.jpa2x.repositories.AppConfigRepository;
import image.persistence.entity.Album;
import image.persistence.entity.Image;
import image.persistence.entity.enums.AppConfigEnum;
import image.persistence.entitytests.IAppConfigSupplier;
import image.persistence.entitytests.IImageSupplier;
import image.persistence.entitytests.image.IImageFlagsUtils;
import image.persistence.repository.ESortType;
import image.photos.album.AlbumPageService;
import image.photos.junit5.testconfig.Junit5PhotosStagingDbConfig;
import io.github.glytching.junit.extension.random.Random;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;

@Junit5PhotosStagingDbConfig
@ExtendWith(RandomBeansExtensionEx.class)
class AlbumPageServiceTest implements IPositiveIntegerRandom, IAppConfigSupplier, IImageSupplier, MiscUtils, IImageFlagsUtils {
	private static final int PAGE_SIZE = 20;
	private static final int MAX_IMAGES_FOR_ALBUM = 30;

	@Autowired
	private AppConfigRepository appConfigRepository;
	@Autowired
	private AlbumRepository albumRepository;
	@Autowired
	private AlbumPageService albumPageService;

	@Random(type = Album.class, size = 5, excludes = {"id", "dirty", "cover", "lastUpdate", "images"})
	private List<Album> albums;
	@Random(excludes = {"id", "dirty", "cover", "lastUpdate", "images.id",
			"images.lastUpdate", "images.flags", "images.deleted", "images.album"})
	private Album specialAlbum;
	@Random(excludes = {"id", "lastUpdate", "flags", "deleted", "album"})
	private Image hiddenImage;
	@Random(excludes = {"id", "lastUpdate", "flags", "deleted", "album"})
	private Image printableImage;

	@BeforeAll
	void beforeAll1() {
		this.specialAlbum.getImages().forEach(i -> {
			i.setAlbum(this.specialAlbum);
			i.setFlags(of(EImageStatus.DEFAULT));
		});
		this.hiddenImage.setFlags(of(EImageStatus.HIDDEN));
		this.specialAlbum.addImage(this.hiddenImage);
		this.printableImage.setFlags(of(EImageStatus.PRINTABLE));
		this.specialAlbum.addImage(this.printableImage);
		this.albumRepository.persist(this.specialAlbum);
	}

	@BeforeAll
	void beforeAll2() {
		// add images to albums
		this.albums.forEach(a -> {
			a.addImages(randomInstanceList(randomPositiveInt(1, MAX_IMAGES_FOR_ALBUM), false, Image.class));
		});
		// set EImageStatus.DEFAULT for all images
		this.albums.stream().map(Album::getImages).flatMap(List<Image>::stream)
				.forEach(i -> i.setFlags(of(EImageStatus.DEFAULT)));
		// insert albums
		this.albums.forEach(this.albumRepository::persist);
		// create photos_per_page app config
		this.appConfigRepository.persist(
				entityAppConfigOf(AppConfigEnum.photos_per_page, String.valueOf(PAGE_SIZE)));
	}

	@AfterAll
	void afterAll() {
		ignoreExc(() -> this.albums.forEach(a -> this.albumRepository.deleteById(a.getId())));
		ignoreExc(() -> this.albumRepository.deleteById(this.specialAlbum.getId()));
		ignoreExc(() -> this.appConfigRepository.deleteByEnumeratedName(AppConfigEnum.photos_per_page));
	}

	@Test
	void finding1Image() {
		Image image = pickRandomlyAnImage(this.specialAlbum);
		List<AlbumPage> imagesForPage = this.albumPageService.getPage(1,
				ESortType.ASC, image.getName(), true, false, this.specialAlbum.getId());
		assertThat(imagesForPage, hasSize(1));
	}

	@Test
	void finding1FullPageOfImages() {
		Album album = pickRandomlyAnAlbum();
		List<AlbumPage> imagesForPage = this.albumPageService.getPage(1, ESortType.ASC,
				"", true, false, album.getId());
		int notDeletedCount = (int) album.getImages().stream().filter(i -> !i.isDeleted()).count();
		assertThat(imagesForPage, hasSize(Math.min(notDeletedCount, PAGE_SIZE)));
	}

	@Test
	void findNoHidden() {
		List<AlbumPage> imagesForPage = this.albumPageService.getPage(1,
				ESortType.ASC, this.hiddenImage.getName(), false,
				false, this.specialAlbum.getId());
		assertThat(imagesForPage, hasSize(0));
	}

	@Test
	void findOnlyPrintable() {
		List<AlbumPage> imagesForPage = this.albumPageService.getPage(1, ESortType.ASC,
				"", true, true, this.specialAlbum.getId());
		assertThat(imagesForPage, hasSize(1));
	}

	private Album pickRandomlyAnAlbum() {
		return this.albums.get(randomPositiveInt(this.albums.size()));
	}

	private Image pickRandomlyAnImage(Album album) {
		List<Image> images = album.getImages();
		return images.get(randomPositiveInt(images.size()));
	}
}
