package exifweb.util.random;

import io.github.glytching.junit.extension.random.RandomBeansExtension;
import org.apache.commons.lang3.reflect.FieldUtils;

public class RandomBeansExtensionEx extends RandomBeansExtension
		implements IEnhancedRandom {
	public RandomBeansExtensionEx() throws IllegalAccessException {
		super();
		FieldUtils.writeField(this, "random", IEnhancedRandom.random, true);
	}
}
