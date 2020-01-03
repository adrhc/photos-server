package image.infrastructure.messaging.image.registration;

import image.infrastructure.messaging.core.consumer.FilteredTypesSubscription;
import image.infrastructure.messaging.image.ImageEvent;
import image.infrastructure.messaging.image.ImageEventTypeEnum;
import lombok.NonNull;

import java.util.EnumSet;

public class FilteredTypesImageSubscription extends FilteredTypesSubscription<String, ImageEventTypeEnum, ImageEvent> {
	public FilteredTypesImageSubscription(String stamp, @NonNull EnumSet<ImageEventTypeEnum> eventTypes, @NonNull ImageSubscription registration) {
		super(stamp, eventTypes, registration);
	}

	public FilteredTypesImageSubscription(@NonNull EnumSet<ImageEventTypeEnum> eventTypes, @NonNull ImageSubscription registration) {
		super(eventTypes, registration);
	}
}
