package exifweb.util;

import org.springframework.beans.factory.config.PropertiesFactoryBean;
import org.springframework.core.io.ClassPathResource;

import java.util.Arrays;

public class PropertiesFactoryBeanEx extends PropertiesFactoryBean {
	public PropertiesFactoryBeanEx(String... paths) {
		super();
		this.setLocations(Arrays.stream(paths)
				.map(ClassPathResource::new).toArray(ClassPathResource[]::new));
	}
}
