package image.jpa2x.repositories.album;

import image.cdm.album.page.AlbumPage;
import image.jpa2x.repositories.ESortType;

import java.util.Date;
import java.util.List;
import java.util.Optional;

/**
 * Created by adr on 2/22/18.
 */
public interface AlbumQueryRepositoryEx {
	/**
	 * Shows in addition to status=0 and printable also !deleted, hidden, personal, ugly, duplicate images.
	 * <p>
	 * image0_.status=IF(:viewHidden, image0_.status, image0_.status
	 * -(image0_.status & 1)-(image0_.status & 2)-(image0_.status & 4)-(image0_.status & 8))
	 */
	String VIEW_HIDDEN_SQL =
			"AND (:viewHidden = true OR i.flags.hidden = false AND i.flags.personal = false AND i.flags.ugly = false AND i.flags.duplicate = false) ";
	/**
	 * Shows only printable images.
	 */
	String VIEW_PRINTABLE_SQL = "AND (:viewOnlyPrintable = false OR i.flags.printable = true) ";

	int countPages(String toSearch, boolean viewHidden,
			boolean viewOnlyPrintable, Integer albumId);

	List<AlbumPage> getPage(int pageNr, ESortType sort, String toSearch,
			boolean viewHidden, boolean viewOnlyPrintable, Integer albumId);

	Optional<Date> getPageLastUpdate(int pageNr, String toSearch,
			boolean viewHidden, boolean viewOnlyPrintable, Integer albumId);
}
