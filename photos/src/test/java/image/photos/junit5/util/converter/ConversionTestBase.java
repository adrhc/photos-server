package image.photos.junit5.util.converter;

import image.photos.junit5.testconfig.Junit5InMemoryDbPhotosConfig;
import net.jcip.annotations.NotThreadSafe;
import org.junit.jupiter.api.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.ConversionService;

@NotThreadSafe
@Junit5InMemoryDbPhotosConfig
@Tag("misc")
public abstract class ConversionTestBase {
	@Autowired
	protected ConversionService cs;
}
