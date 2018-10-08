package image.persistence.repositories;

import image.cdm.image.status.EImageStatus;
import image.persistence.config.Junit5Jpa2xInMemoryDbConfig;
import image.persistence.entity.Album;
import image.persistence.entity.IAppConfigSupplier;
import image.persistence.entity.Image;
import image.persistence.entity.enums.AppConfigEnum;
import image.persistence.entity.image.IImageFlagsUtils;
import image.persistence.repository.AlbumPageRepository;
import image.persistence.repository.util.random.RandomBeansExtensionEx;
import io.github.glytching.junit.extension.random.Random;
import lombok.extern.slf4j.Slf4j;
import net.jcip.annotations.NotThreadSafe;
import org.junit.Assert;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@ExtendWith(RandomBeansExtensionEx.class)
@NotThreadSafe
@Junit5Jpa2xInMemoryDbConfig
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Slf4j
class AlbumPageRepositoryImplTest implements IAppConfigSupplier, IImageFlagsUtils {
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
}
