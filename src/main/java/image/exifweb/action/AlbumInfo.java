package image.exifweb.action;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;

/**
 * Created with IntelliJ IDEA.
 * User: adrian.petre
 * Date: 11/8/13
 * Time: 6:00 PM
 * To change this template use File | Settings | File Templates.
 */
@Service
public class AlbumInfo {
    private static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-mm-dd");
    @Value("${album.image.file.name}")
    private String albumImageFileName;

    /**
     * Pentru a fi considerat album folderul acestuia trebuie sa inceapa cu yyyy-mm-dd.
     *
     * @param name
     * @return
     */
    public boolean isAlbum(String name) {
        try {
            sdf.parse(name.substring(0, 10));
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
