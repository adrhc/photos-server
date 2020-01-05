package exifweb.util.file;

import lombok.SneakyThrows;
import org.springframework.util.ResourceUtils;

import java.nio.file.Path;

public class ClassPathUtils {
	@SneakyThrows
	public static Path pathOf(String classPath) {
		return Path.of(ResourceUtils.getFile(classPath).getAbsolutePath());
	}
}
