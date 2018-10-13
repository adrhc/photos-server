package image.photos.album;

import image.cdm.album.page.AlbumPage;
import image.persistence.repository.AlbumPageRepository;
import image.persistence.repository.ESortType;
import image.photos.image.ImageUtils;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.List;

/**
 * Created by adr on 2/8/18.
 */
@Service
public class AlbumPageService {
	@Inject
	private ImageUtils imageUtils;
	@Inject
	private AlbumPageRepository albumPageRepository;

	public List<AlbumPage> getPage(int pageNr, ESortType sort, String toSearch,
			boolean viewHidden, boolean viewOnlyPrintable,
			Integer albumId) {
		List<AlbumPage> thumbs = this.albumPageRepository.getPageFromDb(pageNr, sort,
				toSearch, viewHidden, viewOnlyPrintable, albumId);
		this.imageUtils.appendImageDimensions(thumbs);
		this.imageUtils.appendImagePaths(thumbs);
		return thumbs;
	}
}
