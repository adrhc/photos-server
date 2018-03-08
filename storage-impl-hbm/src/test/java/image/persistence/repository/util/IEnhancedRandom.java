package image.persistence.repository.util;

import io.github.benas.randombeans.EnhancedRandomBuilder;
import io.github.benas.randombeans.api.EnhancedRandom;

import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Supplier;

import static java.nio.charset.Charset.forName;

public interface IEnhancedRandom {
	EnhancedRandom random = EnhancedRandomBuilder.aNewEnhancedRandomBuilder()
			.objectPoolSize(100)
			.charset(forName("UTF-8"))
			.randomize(Integer.class, (Supplier<Integer>) () ->
					ThreadLocalRandom.current().nextInt(0, Integer.MAX_VALUE))
			.randomize(Long.class, (Supplier<Long>) () ->
					ThreadLocalRandom.current().nextLong(0, Long.MAX_VALUE))
			.stringLengthRange(3, 15)
			.collectionSizeRange(1, 20)
			.scanClasspathForConcreteTypes(true)
			.build();
}
