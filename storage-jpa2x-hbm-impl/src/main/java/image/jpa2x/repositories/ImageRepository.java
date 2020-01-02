package image.jpa2x.repositories;

import image.jpa2x.jpacustomizations.ICustomJpaRepository;
import image.persistence.entity.Image;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ImageRepository extends ImageQueryRepository,
		ImageRepositoryCustom, ICustomJpaRepository<Image, Integer> {
	/**
	 * Make no sense to cache because only Image.id is cached!
	 * <p>
	 * 2020.01.02, 2nd level cache active:
	 * 1th query:
	 * 1. select * from Image where image0_.name=? and image0_.FK_ALBUM=?
	 * 2. select *, ... from Album album0_ left outer join Image image1_ left outer join Album album2_ where album0_.id=?
	 * 2nd (same) query:
	 * 1. select * from Image where image0_.name=? and image0_.FK_ALBUM=?
	 * <p>
	 * competes with ImageServiceImpl.findByNameAndAlbumId
	 */
	Image findByNameAndAlbumId(String name, Integer albumId);

	/**
	 * MySql searches are case-insensitive!
	 * see Paul Wheeler answer at:
	 * https://stackoverflow.com/questions/5629111/how-can-i-make-sql-case-sensitive-string-comparison-on-mysql
	 */
	@Query("SELECT id FROM Image WHERE name = :name AND album.id = :albumId")
	Integer findIdByNameAndAlbumId(String name, Integer albumId);

	@Query("SELECT i FROM Image i WHERE " +
			"(" +
			"LOWER(i.name) LIKE CONCAT('%', LOWER(:nameNoExt), '%') OR  " +
			"LOWER(:nameNoExt) LIKE " +
			"CONCAT('%', LOWER(" +
			"   CASE WHEN LOCATE('.', i.name) <= 1 " +
			"   THEN i.name " + // e.g. ".jpeg"
			"   ELSE SUBSTRING(i.name, 1, LOCATE('.', i.name) - 1) END" + // e.g. "xxx" for xxx.y.jpeg
			"), '%')" +
			") " +
			"AND i.album.id <> :albumId")
	List<Image> findDuplicates(String nameNoExt, Integer albumId);

	int countByAlbum_Id(Integer albumId);
}
