package image.persistence.repositories;

import image.persistence.config.Junit5Jpa2xInMemoryDbConfig;
import image.persistence.entity.Album;
import image.persistence.entity.Image;
import image.persistence.repository.util.assertion.IImageAssertions;
import image.persistence.repository.util.random.RandomBeansExtensionEx;
import io.github.glytching.junit.extension.random.Random;
import net.jcip.annotations.NotThreadSafe;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;

import javax.inject.Inject;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsEqual.equalTo;

@ExtendWith(RandomBeansExtensionEx.class)
@NotThreadSafe
@Junit5Jpa2xInMemoryDbConfig
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ImageRepositoryTest implements IImageAssertions {
	@Inject
	private AlbumRepository albumRepository;
	@Inject
	private ImageRepository imageRepository;

	@Random(excludes = {"id", "lastUpdate", "cover", "images"})
	private Album album;

	/**
	 * Notice that ImageMetadata is generated too and will be used in tests!
	 */
	@BeforeAll
	void setUp(@Random(type = Image.class, size = 30,
			excludes = {"id", "lastUpdate", "album"})
			           List<Image> images) {
		// hibernate might proxy images collection so better
		// just copy images instead of directly using it
		this.album.addImages(images);
		this.albumRepository.save(this.album);
	}

	@AfterAll
	void tearDown() {
		this.albumRepository.delete(this.album);
	}

	@Test
	void persistImage(@Random(excludes = {"id", "lastUpdate", "album"}) Image image) {
		this.album.addImage(image);
		this.imageRepository.save(image);
		Image dbImage = this.imageRepository.findById(image.getId()).orElseThrow(AssertionError::new);
		// https://stackoverflow.com/questions/26242492/how-to-cache-results-of-a-spring-data-jpa-query-method-without-using-query-cache/
		assertThat(this.imageRepository.count(), equalTo(31L));
		assertThat(this.imageRepository.count(), equalTo(31L));
		assertThat(this.imageRepository.count(), equalTo(31L));
		assertImageEquals(image, dbImage);
	}
}
