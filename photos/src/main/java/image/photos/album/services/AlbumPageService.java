package image.photos.album.services;

import image.cdm.album.page.AlbumPage;
import image.jpa2x.repositories.ESortType;
import image.jpa2x.repositories.album.AlbumRepository;
import image.photos.image.helpers.ImageHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by adr on 2/8/18.
 */
@Service
public class AlbumPageService {
	@Autowired
	private ImageHelper imageHelper;
	@Autowired
	private AlbumRepository albumRepository;

	public List<AlbumPage> getPage(int pageNr, ESortType sort, String toSearch,
			boolean viewHidden, boolean viewOnlyPrintable,
			Integer albumId) {
		List<AlbumPage> thumbs = this.albumRepository.getPageFromDb(pageNr, sort,
				toSearch, viewHidden, viewOnlyPrintable, albumId);
		this.imageHelper.appendImageDimensions(thumbs);
		this.imageHelper.appendImagePaths(thumbs);
		return thumbs;
	}
}
