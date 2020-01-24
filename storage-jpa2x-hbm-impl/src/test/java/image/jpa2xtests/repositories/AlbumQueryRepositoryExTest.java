package image.jpa2xtests.repositories;

import exifweb.util.random.RandomBeansExtensionEx;
import image.cdm.album.page.AlbumPage;
import image.cdm.image.status.ImageFlagEnum;
import image.jpa2x.repositories.ESortType;
import image.jpa2x.repositories.album.AlbumRepository;
import image.jpa2x.repositories.appconfig.AppConfigRepository;
import image.jpa2xtests.config.Junit5Jpa2xInMemoryDbConfig;
import image.persistence.entity.Album;
import image.persistence.entity.Image;
import image.persistence.entity.enums.AppConfigEnum;
import image.persistence.entity.image.IImageFlagsUtils;
import image.persistence.entitytests.IAppConfigSupplier;
import io.github.glytching.junit.extension.random.Random;
import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static exifweb.util.SuppressExceptionUtils.ignoreExc;
import static image.jpa2x.repositories.album.AlbumRepository.NULL_ALBUM_ID;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(RandomBeansExtensionEx.class)
@Junit5Jpa2xInMemoryDbConfig
@Slf4j
class AlbumQueryRepositoryExTest implements IAppConfigSupplier, IImageFlagsUtils {
	private static final int PAGE_SIZE = 20;
	private final String T1_TO_SEARCH = "DSC_1555";
	@Autowired
	private AlbumRepository albumRepository;
	@Autowired
	private AppConfigRepository appConfigRepository;
	private Integer albumId;
	private Image hiddenImage;

	@BeforeAll
	void beforeAll(@Random(type = Image.class, size = 50, excludes = {"id", "lastUpdate",
			"deleted", "album"}) Stream<Image> imageStream,
			@Random(excludes = {"id", "lastUpdate", "deleted", "images"}) Album album) {
		// images: deleted = false, status = ImageFlagEnum.DEFAULT
		List<Image> images = imageStream
				.peek(i -> i.setAlbum(album))
				.collect(Collectors.toList());
		// all status types available
		Stream.of(ImageFlagEnum.values())
				.forEach(e -> images.get(10 + e.ordinal()).setFlags(this.of(e)));
		// one deleted image
		images.get(1).setDeleted(true);
		images.get(2).setDeleted(false);
		images.get(2).setName(this.T1_TO_SEARCH);
		images.get(2).setFlags(this.of(ImageFlagEnum.DEFAULT));
		this.hiddenImage = images.get(3);
		this.hiddenImage.setFlags(this.of(ImageFlagEnum.HIDDEN));
		images.get(4).setDeleted(false);
		images.get(4).setFlags(this.of(ImageFlagEnum.PRINTABLE));
		// album cover
		album.setCover(images.get(0));
		album.addImages(images);
		this.albumRepository.persist(album);
		this.albumId = album.getId();
		// required photos_per_page created
		this.appConfigRepository.persist(
				this.entityAppConfigOf(AppConfigEnum.photos_per_page, String.valueOf(PAGE_SIZE)));
	}

	@AfterAll
	void afterAll() {
		ignoreExc(() -> this.albumRepository.deleteById(this.albumId));
		ignoreExc(() -> this.appConfigRepository.deleteByEnumeratedName(AppConfigEnum.photos_per_page));
	}

	@Test
	void counting1Page() {
		int pageCount = this.albumRepository.countPages(
				this.T1_TO_SEARCH, false, false, this.albumId);
		log.debug("imageCount = {}, searching \"{}\", hidden = false, " +
						"viewOnlyPrintable = false, albumId = {}",
				pageCount, this.T1_TO_SEARCH, this.albumId);
		Assert.assertEquals(1, pageCount);
	}

	@Test
	void counting0Pages() {
		int pageCount = this.albumRepository.countPages(
				this.hiddenImage.getName(), false,
				false, this.albumId);
		log.debug("imageCount = {}, searching \"{}\", hidden = false, " +
						"viewOnlyPrintable = false, albumId = {}",
				pageCount, this.hiddenImage.getName(), this.albumId);
		Assert.assertEquals(0, pageCount);
	}

	@Test
	void countingAllPagesForAlbum() {
		int pageCount = this.albumRepository.countPages("",
				true, false, this.albumId);
		log.debug("imageCount = {}, searching \"{}\", hidden = true, " +
						"viewOnlyPrintable = false, albumId = {}",
				pageCount, "", this.albumId);
		Assert.assertEquals(3, pageCount);
	}

	@Test
	void finding1Image() {
		List<AlbumPage> imagesForPage = this.albumRepository.getPage(1,
				ESortType.ASC, this.T1_TO_SEARCH, true, false, this.albumId);
		log.debug("imagesForPage.size = {}, sort ASC, searching \"{}\", hidden = true, " +
						"viewOnlyPrintable = false, albumId = {}", imagesForPage.size(),
				this.T1_TO_SEARCH, this.albumId);
		assertThat(imagesForPage, hasSize(1));
	}

	@Test
	void finding1FullPageForAlbum() {
		List<AlbumPage> imagesForPage = this.albumRepository.getPage(1,
				ESortType.ASC, "", true, false, this.albumId);
		log.debug("imagesForPage.size = {}, sort ASC, searching \"{}\", hidden = true, " +
						"viewOnlyPrintable = false, albumId = {}",
				imagesForPage.size(), "", this.albumId);
		assertThat(imagesForPage, hasSize(PAGE_SIZE));
	}

	@Test
	void finding1FullPageInAllAlbums() {
		List<AlbumPage> imagesForPage1 = this.albumRepository.getPage(1,
				ESortType.ASC, "", true, false, null);
		log.debug("imagesForPage.size = {}, sort ASC, searching \"\", hidden = true, " +
				"viewOnlyPrintable = false, albumId = null", imagesForPage1.size());
		assertThat(imagesForPage1, hasSize(PAGE_SIZE));
		List<AlbumPage> imagesForPage2 = this.albumRepository.getPage(1,
				ESortType.ASC, "", true, false, NULL_ALBUM_ID);
		log.debug("imagesForPage.size = {}, sort ASC, searching \"\", hidden = true, " +
				"viewOnlyPrintable = false, albumId = {}", imagesForPage2.size(), NULL_ALBUM_ID);
		assertThat(imagesForPage2, hasSize(PAGE_SIZE));
	}

	@Test
	void findPrintableInAllAlbums() {
		List<AlbumPage> imagesForPage1 = this.albumRepository.getPage(1,
				ESortType.ASC, "", false, true, null);
		log.debug("imagesForPage.size = {}, sort ASC, searching \"\", hidden = false, " +
				"viewOnlyPrintable = true, albumId = null", imagesForPage1.size());
		assertEquals(imagesForPage1.stream().filter(ap -> !ap.isPrintable()).count(), 0L);
		List<AlbumPage> imagesForPage2 = this.albumRepository.getPage(1,
				ESortType.ASC, "", false, true, NULL_ALBUM_ID);
		log.debug("imagesForPage.size = {}, sort ASC, searching \"\", hidden = false, " +
				"viewOnlyPrintable = true, albumId = {}", imagesForPage2.size(), NULL_ALBUM_ID);
		assertEquals(imagesForPage2.stream().filter(ap -> !ap.isPrintable()).count(), 0L);
	}

	@Test
	void findPrintableForAlbum() {
		List<AlbumPage> imagesForPage1 = this.albumRepository.getPage(1,
				ESortType.ASC, "", false, true, this.albumId);
		log.debug("imagesForPage.size = {}, sort ASC, searching \"\", hidden = false, " +
				"viewOnlyPrintable = true, albumId = {}", imagesForPage1.size(), this.albumId);
		assertEquals(imagesForPage1.stream().filter(ap -> !ap.isPrintable()).count(), 0L);
		assertThat(imagesForPage1, hasSize(greaterThanOrEqualTo(1)));
	}

	@Test
	void findNoHidden() {
		List<AlbumPage> imagesForPage = this.albumRepository.getPage(1,
				ESortType.ASC, this.hiddenImage.getName(),
				false, false, this.albumId);
		log.debug("imagesForPage.size = {}, sort ASC, searching \"{}\", hidden = false, " +
						"viewOnlyPrintable = false, albumId = {}", imagesForPage.size(),
				this.hiddenImage.getName(), this.albumId);
		assertThat(imagesForPage, hasSize(0));
	}

	@Test
	void getPageLastUpdate() {
		Optional<Date> lastUpdate = this.albumRepository.getPageLastUpdate(1,
				null, false, false, this.albumId);
		log.debug("lastUpdate = {}, sort ASC, searching \"{}\", hidden = false, " +
						"viewOnlyPrintable = false, albumId = {}", lastUpdate,
				this.hiddenImage.getName(), this.albumId);
		assertTrue(lastUpdate.isPresent());
	}
}
