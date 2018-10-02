package image.exifweb.album.page;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
public class PageCount implements Serializable {
    private Number photosPerPage;
    private Number pageCount;
}
