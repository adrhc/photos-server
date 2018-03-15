package image.photos.junit5.album;

import exifweb.util.MiscUtils;
import image.cdm.album.page.AlbumPage;
import image.cdm.image.status.EImageStatus;
import image.persistence.entity.Album;
import image.persistence.entity.IAppConfigSupplier;
import image.persistence.entity.IImageSupplier;
import image.persistence.entity.Image;
import image.persistence.entity.enums.AppConfigEnum;
import image.persistence.repository.AlbumRepository;
import image.persistence.repository.AppConfigRepository;
import image.persistence.repository.ESortType;
import image.persistence.repository.util.random.RandomBeansExtensionEx;
import image.persistence.util.IPositiveIntegerRandom;
import image.photos.album.AlbumPageService;
import image.photos.junit5.testconfig.Junit5PhotosStagingDbConfig;
import io.github.glytching.junit.extension.random.Random;
import net.jcip.annotations.NotThreadSafe;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;

@NotThreadSafe
@Junit5PhotosStagingDbConfig
@ExtendWith(RandomBeansExtensionEx.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class AlbumPageServiceTest implements IPositiveIntegerRandom, IAppConfigSupplier, IImageSupplier, MiscUtils {
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
	//	@Random(excludes = {"id", "dirty", "cover", "lastUpdate", "images.id",
//			"images.lastUpdate", "images.status", "images.deleted"})
	@Random(excludes = {"id", "dirty", "cover", "lastUpdate", "images"})
	private Album specialAlbum;
	@Random(excludes = {"id", "lastUpdate", "status", "deleted"})
	private Image hiddenImage;
	@Random(excludes = {"id", "lastUpdate", "status", "deleted"})
	private Image printableImage;

	@BeforeAll
	void setUpSpecialAlbum(@Random(type = Image.class, size = 5,
			excludes = {"id", "lastUpdate", "deleted", "status"}) List<Image> images) {
		this.specialAlbum.addImages(images);
		this.hiddenImage.setStatus(EImageStatus.HIDDEN.getValueAsByte());
		this.specialAlbum.addImage(this.hiddenImage);
		this.printableImage.setStatus(EImageStatus.PRINTABLE.getValueAsByte());
		this.specialAlbum.addImage(this.printableImage);
		this.albumRepository.createAlbum(this.specialAlbum);
	}

	@BeforeAll
	void setUp() {
		// add images to albums
		this.albums.forEach(a -> {
			a.addImages(randomInstanceList(randomPositiveInt(1, MAX_IMAGES_FOR_ALBUM), false, Image.class));
		});
		// set EImageStatus.DEFAULT for all images
		this.albums.stream().map(Album::getImages).flatMap(List<Image>::stream)
				.forEach(i -> i.setStatus(EImageStatus.DEFAULT.getValueAsByte()));
		// insert albums
		this.albums.forEach(this.albumRepository::createAlbum);
		// create photos_per_page app config
		this.appConfigRepository.createAppConfig(
				entityAppConfigOf(AppConfigEnum.photos_per_page, String.valueOf(PAGE_SIZE)));
	}

	@AfterAll
	void tearDown() {
		safeCall(() -> this.albums.forEach(a -> this.albumRepository.deleteAlbum(a.getId())));
		safeCall(() -> this.albumRepository.deleteAlbum(this.specialAlbum.getId()));
		safeCall(() -> this.appConfigRepository.deleteAppConfig(AppConfigEnum.photos_per_page));
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