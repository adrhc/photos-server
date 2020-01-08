package image.photos.image.services;

import image.photos.util.function.UnsafeSupplier;
import lombok.AllArgsConstructor;
import lombok.Getter;

import static image.photos.image.services.ImageImportProcTypeEnum.HEAVY;
import static image.photos.image.services.ImageImportProcTypeEnum.LIGHTWEIGHT;

@Getter
@AllArgsConstructor
public class ImageImportOperation<T, E extends Throwable> {
	private UnsafeSupplier<T, E> unsafeSupplier;
	private ImageImportProcTypeEnum type;

	public static <T, E extends Throwable> ImageImportOperation<T, E>
	heavyImport(UnsafeSupplier<T, E> value) {
		return new ImageImportOperation<>(value, HEAVY);
	}

	public static <T, E extends Throwable> ImageImportOperation<T, E>
	lightweightImport(UnsafeSupplier<T, E> value) {
		return new ImageImportOperation<>(value, LIGHTWEIGHT);
	}

	public T getUnsafe() throws E {
		return this.unsafeSupplier.get();
	}
}
