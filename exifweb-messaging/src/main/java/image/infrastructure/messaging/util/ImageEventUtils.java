package image.infrastructure.messaging.util;

import image.infrastructure.messaging.image.ImageEvent;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class ImageEventUtils {
	public static List<String> sortedNamesOf(List<ImageEvent> events) {
		return events.stream()
				.map(ev -> ev.getEntity().getName())
				.sorted(Comparator.naturalOrder())
				.collect(Collectors.toList());
	}
}
