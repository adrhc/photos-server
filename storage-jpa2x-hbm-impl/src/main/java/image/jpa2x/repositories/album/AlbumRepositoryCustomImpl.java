package image.jpa2x.repositories.album;

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

	@Override
	public boolean removeAlbumCover(Integer albumId) {
		Album album = this.em.find(Album.class, albumId);
		// NPE when album is NULL
		if (album.getCover() == null) {
			return false;
		}
		album.setCover(null);
		return true;
	}

	@Override
	public boolean clearDirty(Integer albumId) {
		return this.changeDirtyFlag(false, albumId);
	}

	@Override
	public boolean markAsDirty(Integer albumId) {
		return this.changeDirtyFlag(true, albumId);
	}

	private boolean changeDirtyFlag(boolean dirty, Integer albumId) {
//		logger.debug("BEGIN");
		Album album = this.em.find(Album.class, albumId);
		// check solved by hibernate BytecodeEnhancement (+hibernate-enhance-maven-plugin)
		if (album.isDirty() == dirty) {
//			logger.debug("END dirty update cancelled (already {})", dirty);
			return false;
		}
		album.setDirty(dirty);
//		logger.debug("END dirty set to {}, {}", dirty, sdf.format(album.getLastUpdate()));
		return true;
	}
}
