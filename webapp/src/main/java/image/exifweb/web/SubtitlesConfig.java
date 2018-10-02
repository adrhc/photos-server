package image.exifweb.web;

import image.exifweb.RootConfig;
import image.exifweb.web.security.WebSecurityComponent;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.RestController;

@Configuration
@ComponentScan(basePackageClasses = RootConfig.class,
        basePackages = "subtitles",
        excludeFilters = {@ComponentScan.Filter(Controller.class),
                @ComponentScan.Filter(RestController.class),
                @ComponentScan.Filter(ControllerAdvice.class),
                @ComponentScan.Filter(WebSecurityComponent.class),
                @ComponentScan.Filter(Configuration.class)})
@PropertySource("classpath:/subs-extract-app-config.properties")
public class SubtitlesConfig {
}
