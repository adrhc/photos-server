package image.persistence.repository.util.random;

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
			.overrideDefaultInitialization(true)
			.charset(forName("UTF-8"))
			.randomize(Integer.class, (Supplier<Integer>) () ->
					ThreadLocalRandom.current().nextInt(0, Integer.MAX_VALUE))
			.randomize(Long.class, (Supplier<Long>) () ->
					ThreadLocalRandom.current().nextLong(0, Long.MAX_VALUE))
			.stringLengthRange(3, 15)
			.collectionSizeRange(1, 50)
			.scanClasspathForConcreteTypes(true)
			.build();

	default <T> T randomInstance(boolean withId, Class<T> clazz) {
		Optional<T> randomT = randomInstanceStream(1, withId, clazz).findAny();
		assert randomT.isPresent() : "random " + clazz.getSimpleName() + " is null!";
		return randomT.get();
	}

	default <T> List<T> randomInstanceList(int amount, boolean withId, Class<T> clazz) {
		return randomInstanceStream(amount, withId, clazz).collect(Collectors.toList());
	}

	default <T> Stream<T> randomInstanceStream(int amount, boolean withId, Class<T> clazz) {
		if (withId) {
			return IEnhancedRandom.random.objects(
					clazz, amount, "lastUpdate");
		} else {
			return IEnhancedRandom.random.objects(
					clazz, amount, "id", "lastUpdate");
		}
	}
}
