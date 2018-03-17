package image.persistence.repository.junit5;

import image.persistence.entity.Album;
import image.persistence.repository.AlbumCoverRepository;
import image.persistence.repository.AlbumRepository;
import image.persistence.repository.junit5.testconfig.Junit5HbmInMemoryDbConfig;
import image.persistence.repository.util.random.RandomBeansExtensionEx;
import io.github.glytching.junit.extension.random.Random;
import net.jcip.annotations.NotThreadSafe;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Date;

import static org.exparity.hamcrest.date.DateMatchers.sameOrAfter;
import static org.exparity.hamcrest.date.DateMatchers.sameOrBefore;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.both;

@ExtendWith(RandomBeansExtensionEx.class)
@NotThreadSafe
@Junit5HbmInMemoryDbConfig
class AlbumCoverRepositoryTest {
	@Autowired
	private AlbumRepository albumRepository;
	@Autowired
	private AlbumCoverRepository albumCoverRepository;
	private Date before;

	@BeforeEach
	void beforeEach(@Random(excludes = {"id", "lastUpdate", "images.id",
			"images.lastUpdate", "images.album"}) Album album) {
		this.before = new Date();
		album.getImages().forEach(i -> i.setAlbum(album));
		album.setCover(album.getImages().get(0));
		this.albumRepository.createAlbum(album);
	}

	@Test
	void getAlbumCoversLastUpdateDate() {
		Date date = this.albumCoverRepository.getAlbumCoversLastUpdateDate();
		assertThat(date, both(sameOrAfter(this.before))
				.and(sameOrBefore(new Date())));
	}
}
