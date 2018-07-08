package image.persistence.repository.junit5.staging;

import exifweb.util.MiscUtils;
import image.cdm.album.page.AlbumPage;
import image.cdm.image.status.EImageStatus;
import image.persistence.entity.Album;
import image.persistence.entity.IAppConfigSupplier;
import image.persistence.entity.Image;
import image.persistence.entity.enums.AppConfigEnum;
import image.persistence.entity.image.IImageFlagsUtils;
import image.persistence.repository.AlbumPageRepository;
import image.persistence.repository.AlbumRepository;
import image.persistence.repository.AppConfigRepository;
import image.persistence.repository.ESortType;
import image.persistence.repository.junit5.springconfig.Junit5HbmStagingJdbcDbConfig;
import image.persistence.repository.util.random.RandomBeansExtensionEx;
import io.github.glytching.junit.extension.random.Random;
import org.junit.Assert;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;

@ExtendWith(RandomBeansExtensionEx.class)
@Junit5HbmStagingJdbcDbConfig
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class AlbumPageRepositoryTest implements IAppConfigSupplier, MiscUtils, IImageFlagsUtils {
	private static final Logger logger = LoggerFactory.getLogger(AlbumPageRepositoryTest.class);
	private static final String T1_TO_SEARCH = "DSC_1555";
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
			"deleted", "status", "album"}) Stream<Image> imageStream,
	               @Random(excludes = {"id", "lastUpdate", "deleted", "images"}) Album album) {
		// images: deleted = false, status = EImageStatus.DEFAULT
		List<Image> images = imageStream.peek(i -> i.setAlbum(album))
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
		// album cover
		album.setCover(images.get(0));
		album.addImages(images);
		this.albumRepository.createAlbum(album);
		this.albumId = album.getId();
		// required photos_per_page created
		this.appConfigRepository.createAppConfig(
				entityAppConfigOf(AppConfigEnum.photos_per_page, String.valueOf(PAGE_SIZE)));
	}

	@Test
	void counting1Page() {
		int pageCount = this.albumPageRepository.getPageCount(
				T1_TO_SEARCH, false, false, this.albumId);
		logger.debug("imageCount = {}, searching \"{}\", hidden = false, " +
						"viewOnlyPrintable = false, albumId = {}",
				pageCount, T1_TO_SEARCH, this.albumId);
		Assert.assertEquals(1, pageCount);
	}

	@Test
	void counting0Pages() {
		int pageCount = this.albumPageRepository.getPageCount(
				this.hiddenImage.getName(), false,
				false, this.albumId);
		logger.debug("imageCount = {}, searching \"{}\", hidden = false, " +
						"viewOnlyPrintable = false, albumId = {}",
				pageCount, this.hiddenImage.getName(), this.albumId);
		Assert.assertEquals(0, pageCount);
	}

	@Test
	void countingAllPagesForAlbum() {
		int pageCount = this.albumPageRepository.getPageCount("",
				true, false, this.albumId);
		logger.debug("imageCount = {}, searching \"{}\", hidden = false, " +
						"viewOnlyPrintable = false, albumId = {}",
				pageCount, "", this.albumId);
		Assert.assertEquals(3, pageCount);
	}

	@Test
	void finding1Image() {
		List<AlbumPage> imagesForPage = this.albumPageRepository.getPageFromDb(1,
				ESortType.ASC, T1_TO_SEARCH, true, false, this.albumId);
		logger.debug("imagesForPage.size = {}, sort ASC, searching \"{}\", hidden = false, " +
						"viewOnlyPrintable = false, albumId = {}", imagesForPage.size(),
				T1_TO_SEARCH, this.albumId);
		assertThat(imagesForPage, hasSize(1));
	}

	@Test
	void finding1FullPageOfImages() {
		List<AlbumPage> imagesForPage = this.albumPageRepository.getPageFromDb(1,
				ESortType.ASC, "", true, false, this.albumId);
		logger.debug("imagesForPage.size = {}, sort ASC, searching \"{}\", hidden = false, " +
						"viewOnlyPrintable = false, albumId = {}",
				imagesForPage.size(), "", this.albumId);
		assertThat(imagesForPage, hasSize(PAGE_SIZE));
	}

	@Test
	void findNoHidden() {
		List<AlbumPage> imagesForPage = this.albumPageRepository.getPageFromDb(1,
				ESortType.ASC, this.hiddenImage.getName(),
				false, false, this.albumId);
		logger.debug("imagesForPage.size = {}, sort ASC, searching \"{}\", hidden = false, " +
						"viewOnlyPrintable = false, albumId = {}", imagesForPage.size(),
				this.hiddenImage.getName(), this.albumId);
		assertThat(imagesForPage, hasSize(0));
	}

	@AfterAll
	void afterAll() {
		ignoreExc(() -> this.albumRepository.deleteAlbumById(this.albumId));
		ignoreExc(() -> this.appConfigRepository.deleteAppConfig(AppConfigEnum.photos_per_page));
	}
}
