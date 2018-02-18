package image.exifweb.album.page;

import image.cdm.album.page.AlbumPage;
import image.exifweb.image.ImageUtils;
import image.exifweb.system.persistence.repositories.AlbumPageRepository;
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

	public List<AlbumPage> getPage(int pageNr, String sort, String toSearch,
	                               boolean viewHidden, boolean viewOnlyPrintable,
	                               Integer albumId) {
		List<AlbumPage> thumbs = albumPageRepository.getPageFromDb(pageNr, sort,
				toSearch, viewHidden, viewOnlyPrintable, albumId);
		imageUtils.appendImageDimensions(thumbs);
		imageUtils.appendImagePaths(thumbs);
		return thumbs;
	}
}
