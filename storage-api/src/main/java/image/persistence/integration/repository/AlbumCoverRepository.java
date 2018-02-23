package image.persistence.integration.repository;

import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

/**
 * Created by adr on 2/22/18.
 */
public interface AlbumCoverRepository {
	@Transactional(readOnly = true)
	Date getAlbumCoversLastUpdateDate();
}
