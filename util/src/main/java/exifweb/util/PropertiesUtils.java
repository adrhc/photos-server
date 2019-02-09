package exifweb.util;

import org.springframework.beans.factory.config.PropertiesFactoryBean;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import java.io.IOException;
import java.util.Arrays;
import java.util.Properties;

public class PropertiesUtils {
	public static Properties propertiesOf(String... paths) {
		Resource[] locations = Arrays.stream(paths)
				.map(ClassPathResource::new)
				.toArray(ClassPathResource[]::new);
		return propertiesOf(locations);
	}

	public static Properties propertiesOf(Resource... resources) {
		PropertiesFactoryBean properties = new PropertiesFactoryBean();
		properties.setLocations(resources);
		try {
			// force to load the properties
			properties.afterPropertiesSet();
			return properties.getObject();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
}
