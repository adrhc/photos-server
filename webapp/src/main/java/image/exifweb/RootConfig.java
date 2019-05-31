package image.exifweb;

import image.photos.PhotosConfig;
import org.springframework.context.annotation.*;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ControllerAdvice;
import subtitles.SubtitlesConfig;

/**
 * Created by adr on 2/16/18.
 * <p>
 * PropertySource: "classpath:" is mandatory!
 * Scanner is including everything other than the listed classes.
 */
@Configuration
@ComponentScan(excludeFilters = {
		@ComponentScan.Filter({Controller.class, ControllerAdvice.class}),
		@ComponentScan.Filter(
				type = FilterType.ASSIGNABLE_TYPE,
				value = {WebConfig.class, WebContextUtilities.class})})
@Import({SubtitlesConfig.class, PhotosConfig.class})
@PropertySource("classpath:/exifweb.properties")
public class RootConfig {}
