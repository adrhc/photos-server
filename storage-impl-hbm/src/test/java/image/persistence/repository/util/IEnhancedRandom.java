package image.persistence.repository.util;

import io.github.benas.randombeans.EnhancedRandomBuilder;
import io.github.benas.randombeans.api.EnhancedRandom;

import static java.nio.charset.Charset.forName;

public interface IEnhancedRandom {
	EnhancedRandom random = EnhancedRandomBuilder.aNewEnhancedRandomBuilder()
			.objectPoolSize(100)
			.charset(forName("UTF-8"))
			.stringLengthRange(3, 15)
			.collectionSizeRange(1, 20)
			.scanClasspathForConcreteTypes(true)
			.build();
}
