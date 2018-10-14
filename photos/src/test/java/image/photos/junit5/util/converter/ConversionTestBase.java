package image.photos.junit5.util.converter;

import image.photos.junit5.testconfig.Junit5PhotosInMemoryDbConfig;
import org.junit.jupiter.api.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.ConversionService;

@Junit5PhotosInMemoryDbConfig
@Tag("misc")
public abstract class ConversionTestBase {
	@Autowired
	protected ConversionService cs;
}
