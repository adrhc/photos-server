package image.persistence.repositories;

import image.persistence.entity.Album;
import image.persistence.entity.Image;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@Transactional
public class AlbumRepositoryCustomImpl implements AlbumRepositoryCustom {
	@PersistenceContext
	private EntityManager em;

	@Override
	public boolean putAlbumCover(Integer imageId) {
		Image newCover = this.em.find(Image.class, imageId);
		Album album = newCover.getAlbum();
		Image currentCover = album.getCover();
		if (currentCover == null) {
			album.setCover(newCover);
			album.setDirty(true);
			return true;
		}
		if (currentCover.getId().equals(imageId)) {
			return false;
		}
		album.setCover(newCover);
		album.setDirty(true);
		return true;
	}
}
