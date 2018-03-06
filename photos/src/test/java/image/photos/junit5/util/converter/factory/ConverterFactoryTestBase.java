package image.photos.junit5.util.converter.factory;

import image.photos.junit5.config.testconfig.Junit5InMemoryDbPhotosConfig;
import net.jcip.annotations.NotThreadSafe;
import org.junit.jupiter.api.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.ConversionService;

@NotThreadSafe
@Junit5InMemoryDbPhotosConfig
@Tag("misc")
public abstract class ConverterFactoryTestBase {
	@Autowired
	protected ConversionService cs;
}
