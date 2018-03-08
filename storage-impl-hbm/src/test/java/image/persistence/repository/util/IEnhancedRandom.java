package image.persistence.repository.util;

import io.github.benas.randombeans.EnhancedRandomBuilder;
import io.github.benas.randombeans.api.EnhancedRandom;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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

	default <T> T randomAppConfig(boolean withId, Class<T> clazz) {
		Optional<T> randomT = randomAppConfigStream(1, withId, clazz).findAny();
		assert randomT.isPresent() : "random " + clazz.getSimpleName() + " is null!";
		return randomT.get();
	}

	default <T> List<T> randomAppConfigList(int amount, boolean withId, Class<T> clazz) {
		return randomAppConfigStream(amount, withId, clazz).collect(Collectors.toList());
	}

	default <T> Stream<T> randomAppConfigStream(int amount, boolean withId, Class<T> clazz) {
		if (withId) {
			return IEnhancedRandom.random.objects(
					clazz, amount, "lastUpdate");
		} else {
			return IEnhancedRandom.random.objects(
					clazz, amount, "id", "lastUpdate");
		}
	}
}
