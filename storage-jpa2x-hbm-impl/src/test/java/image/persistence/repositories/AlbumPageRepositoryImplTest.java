package image.persistence.repositories;

import exifweb.util.MiscUtils;
import image.cdm.album.page.AlbumPage;
import image.cdm.image.status.EImageStatus;
import image.persistence.config.Junit5Jpa2xInMemoryDbConfig;
import image.persistence.entity.Album;
import image.persistence.entity.IAppConfigSupplier;
import image.persistence.entity.Image;
import image.persistence.entity.enums.AppConfigEnum;
import image.persistence.entity.image.IImageFlagsUtils;
import image.persistence.repository.AlbumPageRepository;
import image.persistence.repository.ESortType;
import image.persistence.util.random.RandomBeansExtensionEx;
import io.github.glytching.junit.extension.random.Random;
import lombok.extern.slf4j.Slf4j;
import net.jcip.annotations.NotThreadSafe;
import org.junit.Assert;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static image.persistence.repository.AlbumPageRepository.NULL_ALBUM_ID;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(RandomBeansExtensionEx.class)
@NotThreadSafe
@Junit5Jpa2xInMemoryDbConfig
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Slf4j
public class AlbumPageRepositoryImplTest implements IAppConfigSupplier, IImageFlagsUtils, MiscUtils {
	public static final String T1_TO_SEARCH = "DSC_1555";
	private static final int PAGE_SIZE = 20;
	@Autowired
	private AlbumRepository albumRepository;
	@Autowired
	private AppConfigRepository appConfigRepository;
	@Autowired
	private AlbumPageRepository albumPageRepository;
	private Integer albumId;
	private Image hiddenImage;

	@BeforeAll
	void beforeAll(@Random(type = Image.class, size = 50, excludes = {"id", "lastUpdate",
			"deleted", "album"}) Stream<Image> imageStream,
	               @Random(excludes = {"id", "lastUpdate", "deleted", "images"}) Album album) {
		// images: deleted = false, status = EImageStatus.DEFAULT
		List<Image> images = imageStream
				.peek(i -> i.setAlbum(album))
				.collect(Collectors.toList());
		// all status types available
		Stream.of(EImageStatus.values())
				.forEach(e -> images.get(10 + e.ordinal()).setFlags(of(e)));
		// one deleted image
		images.get(1).setDeleted(true);
		images.get(2).setDeleted(false);
		images.get(2).setName(T1_TO_SEARCH);
		images.get(2).setFlags(of(EImageStatus.DEFAULT));
		this.hiddenImage = images.get(3);
		this.hiddenImage.setFlags(of(EImageStatus.HIDDEN));
		images.get(4).setDeleted(false);
		images.get(4).setFlags(of(EImageStatus.PRINTABLE));
		// album cover
		album.setCover(images.get(0));
		album.addImages(images);
		this.albumRepository.persist(album);
		this.albumId = album.getId();
		// required photos_per_page created
		this.appConfigRepository.persist(
				entityAppConfigOf(AppConfigEnum.photos_per_page, String.valueOf(PAGE_SIZE)));
	}

	@AfterAll
	void afterAll() {
		ignoreExc(() -> this.albumRepository.deleteById(this.albumId));
		ignoreExc(() -> this.appConfigRepository.deleteByEnumeratedName(AppConfigEnum.photos_per_page));
	}

	@Test
	void counting1Page() {
		int pageCount = this.albumPageRepository.countPages(
				T1_TO_SEARCH, false, false, this.albumId);
		log.debug("imageCount = {}, searching \"{}\", hidden = false, " +
						"viewOnlyPrintable = false, albumId = {}",
				pageCount, T1_TO_SEARCH, this.albumId);
		Assert.assertEquals(1, pageCount);
	}

	@Test
	void counting0Pages() {
		int pageCount = this.albumPageRepository.countPages(
				this.hiddenImage.getName(), false,
				false, this.albumId);
		log.debug("imageCount = {}, searching \"{}\", hidden = false, " +
						"viewOnlyPrintable = false, albumId = {}",
				pageCount, this.hiddenImage.getName(), this.albumId);
		Assert.assertEquals(0, pageCount);
	}

	@Test
	void countingAllPagesForAlbum() {
		int pageCount = this.albumPageRepository.countPages("",
				true, false, this.albumId);
		log.debug("imageCount = {}, searching \"{}\", hidden = true, " +
						"viewOnlyPrintable = false, albumId = {}",
				pageCount, "", this.albumId);
		Assert.assertEquals(3, pageCount);
	}

	@Test
	void finding1Image() {
		List<AlbumPage> imagesForPage = this.albumPageRepository.getPageFromDb(1,
				ESortType.ASC, T1_TO_SEARCH, true, false, this.albumId);
		log.debug("imagesForPage.size = {}, sort ASC, searching \"{}\", hidden = true, " +
						"viewOnlyPrintable = false, albumId = {}", imagesForPage.size(),
				T1_TO_SEARCH, this.albumId);
		assertThat(imagesForPage, hasSize(1));
	}

	@Test
	void finding1FullPageForAlbum() {
		List<AlbumPage> imagesForPage = this.albumPageRepository.getPageFromDb(1,
				ESortType.ASC, "", true, false, this.albumId);
		log.debug("imagesForPage.size = {}, sort ASC, searching \"{}\", hidden = true, " +
						"viewOnlyPrintable = false, albumId = {}",
				imagesForPage.size(), "", this.albumId);
		assertThat(imagesForPage, hasSize(PAGE_SIZE));
	}

	@Test
	void finding1FullPageInAllAlbums() {
		List<AlbumPage> imagesForPage1 = this.albumPageRepository.getPageFromDb(1,
				ESortType.ASC, "", true, false, null);
		log.debug("imagesForPage.size = {}, sort ASC, searching \"\", hidden = true, " +
				"viewOnlyPrintable = false, albumId = null", imagesForPage1.size());
		assertThat(imagesForPage1, hasSize(PAGE_SIZE));
		List<AlbumPage> imagesForPage2 = this.albumPageRepository.getPageFromDb(1,
				ESortType.ASC, "", true, false, NULL_ALBUM_ID);
		log.debug("imagesForPage.size = {}, sort ASC, searching \"\", hidden = true, " +
				"viewOnlyPrintable = false, albumId = {}", imagesForPage2.size(), NULL_ALBUM_ID);
		assertThat(imagesForPage2, hasSize(PAGE_SIZE));
	}

	@Test
	void findPrintableInAllAlbums() {
		List<AlbumPage> imagesForPage1 = this.albumPageRepository.getPageFromDb(1,
				ESortType.ASC, "", false, true, null);
		log.debug("imagesForPage.size = {}, sort ASC, searching \"\", hidden = false, " +
				"viewOnlyPrintable = true, albumId = null", imagesForPage1.size());
		assertEquals(imagesForPage1.stream().filter(ap -> !ap.isPrintable()).count(), 0L);
		List<AlbumPage> imagesForPage2 = this.albumPageRepository.getPageFromDb(1,
				ESortType.ASC, "", false, true, NULL_ALBUM_ID);
		log.debug("imagesForPage.size = {}, sort ASC, searching \"\", hidden = false, " +
				"viewOnlyPrintable = true, albumId = {}", imagesForPage2.size(), NULL_ALBUM_ID);
		assertEquals(imagesForPage2.stream().filter(ap -> !ap.isPrintable()).count(), 0L);
	}

	@Test
	void findPrintableForAlbum() {
		List<AlbumPage> imagesForPage1 = this.albumPageRepository.getPageFromDb(1,
				ESortType.ASC, "", false, true, this.albumId);
		log.debug("imagesForPage.size = {}, sort ASC, searching \"\", hidden = false, " +
				"viewOnlyPrintable = true, albumId = {}", imagesForPage1.size(), this.albumId);
		assertEquals(imagesForPage1.stream().filter(ap -> !ap.isPrintable()).count(), 0L);
		assertThat(imagesForPage1, hasSize(greaterThanOrEqualTo(1)));
	}

	@Test
	void findNoHidden() {
		List<AlbumPage> imagesForPage = this.albumPageRepository.getPageFromDb(1,
				ESortType.ASC, this.hiddenImage.getName(),
				false, false, this.albumId);
		log.debug("imagesForPage.size = {}, sort ASC, searching \"{}\", hidden = false, " +
						"viewOnlyPrintable = false, albumId = {}", imagesForPage.size(),
				this.hiddenImage.getName(), this.albumId);
		assertThat(imagesForPage, hasSize(0));
	}
}
