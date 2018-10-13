package image.exifweb.album.page;

public interface PageCount {
	Number getPhotosPerPage();

	Number getPageCount();

	void setPhotosPerPage(Number photosPerPage);

	void setPageCount(Number pageCount);
}
