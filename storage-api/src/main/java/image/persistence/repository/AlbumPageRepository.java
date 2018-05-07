package image.persistence.repository;

import image.cdm.album.page.AlbumPage;

import java.util.List;

/**
 * Created by adr on 2/22/18.
 */
public interface AlbumPageRepository {
	int getPageCount(String toSearch, boolean viewHidden,
	                 boolean viewOnlyPrintable, Integer albumId);

	List<AlbumPage> getPageFromDb(int pageNr, ESortType sort, String toSearch,
	                              boolean viewHidden, boolean viewOnlyPrintable,
	                              Integer albumId);
}
