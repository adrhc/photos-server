package image.infrastructure.messaging.album.registration;

import image.infrastructure.messaging.album.AlbumEvent;
import image.infrastructure.messaging.album.AlbumEventTypeEnum;
import image.infrastructure.messaging.core.consumer.FilteredTypesSubscription;
import lombok.NonNull;

import java.util.EnumSet;

public class FilteredTypesAlbumSubscription extends FilteredTypesSubscription<String, AlbumEventTypeEnum, AlbumEvent> {
	public FilteredTypesAlbumSubscription(String stamp, @NonNull EnumSet<AlbumEventTypeEnum> eventTypes, @NonNull AlbumSubscription registration) {
		super(stamp, eventTypes, registration);
	}

	public FilteredTypesAlbumSubscription(@NonNull EnumSet<AlbumEventTypeEnum> eventTypes, @NonNull AlbumSubscription registration) {
		super(eventTypes, registration);
	}
}
