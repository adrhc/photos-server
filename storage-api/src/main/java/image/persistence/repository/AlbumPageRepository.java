package image.persistence.repository;

import image.cdm.album.page.AlbumPage;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Created by adr on 2/22/18.
 */
public interface AlbumPageRepository {
	@Transactional(readOnly = true)
	int getPageCount(String toSearch, boolean viewHidden,
	                 boolean viewOnlyPrintable, Integer albumId);

	@Transactional(readOnly = true)
	List<AlbumPage> getPageFromDb(int pageNr, ESortType sort, String toSearch,
	                              boolean viewHidden, boolean viewOnlyPrintable,
	                              Integer albumId);
}
