package image.exifweb;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

@Configuration
@ComponentScan(basePackageClasses = WebMvcConfig.class,
        useDefaultFilters = false,
        includeFilters = {@ComponentScan.Filter(Controller.class),
                @ComponentScan.Filter(RestController.class),
                @ComponentScan.Filter(ControllerAdvice.class)})
@EnableWebMvc
@EnableGlobalMethodSecurity(prePostEnabled = true, securedEnabled = true)
public class WebMvcConfig {
}
