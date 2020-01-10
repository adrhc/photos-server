package image.photos.image.services;

import image.photos.util.function.UnsafeSupplier;
import lombok.AllArgsConstructor;
import lombok.Getter;

import static image.photos.image.services.ProcessingTypeEnum.HEAVY;
import static image.photos.image.services.ProcessingTypeEnum.LIGHTWEIGHT;

@Getter
@AllArgsConstructor
public class CategorizedUnsafeProcessing<T, E extends Throwable> {
	private UnsafeSupplier<T, E> unsafeSupplier;
	private ProcessingTypeEnum type;

	public static <T, E extends Throwable> CategorizedUnsafeProcessing<T, E>
	heavyImport(UnsafeSupplier<T, E> value) {
		return new CategorizedUnsafeProcessing<>(value, HEAVY);
	}

	public static <T, E extends Throwable> CategorizedUnsafeProcessing<T, E>
	lightweightImport(UnsafeSupplier<T, E> value) {
		return new CategorizedUnsafeProcessing<>(value, LIGHTWEIGHT);
	}

	public T getUnsafe() throws E {
		return this.unsafeSupplier.get();
	}
}
