package image.exifweb.image;

import image.exifweb.persistence.Image;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;

/**
 * Created by adrianpetre on 29.01.2018.
 */
@Service
public class ImageService {
    @Inject
    private SessionFactory sessionFactory;

    @Transactional
    public boolean removeById(Integer imageId) {
        Session session = sessionFactory.getCurrentSession();
        Query q = session.createQuery("DELETE FROM Image WHERE id = :imageId");
        q.setParameter("imageId", imageId);
        return q.executeUpdate() > 0;
    }

    @Transactional
    public Image getById(Integer imageId) {
        Session session = sessionFactory.getCurrentSession();
        return (Image) session.get(Image.class, imageId);
    }
}
