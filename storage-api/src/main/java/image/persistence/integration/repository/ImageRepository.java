package image.persistence.integration.repository;

import image.cdm.image.ImageRating;
import image.cdm.image.ImageStatus;
import image.persistence.entity.Image;
import image.persistence.entity.image.ImageMetadata;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

/**
 * Created by adrianpetre on 23.02.2018.
 */
public interface ImageRepository {
    @Transactional
    Image updateThumbLastModifiedForImg(Date thumbLastModified, Integer imageId);

    @Transactional
    boolean changeRating(ImageRating imageRating);

    @Transactional
    boolean changeStatus(ImageStatus imageStatus);

    @Transactional
    List<Image> getImagesByAlbumId(Integer albumId);

    @Transactional
    void persistImage(Image image);

    @Transactional
    boolean markDeleted(Integer imageId);

    @Transactional
    void deleteImage(Integer imageId);

    @Transactional
    void changeName(String name, Integer imageId);

    @Transactional
    Image updateImageMetadata(ImageMetadata imageMetadata, Integer imageId);

    @Transactional
    Image getImageByNameAndAlbumId(String name, Integer albumId);

    @Transactional
    Image getImageById(Integer imageId);
}
