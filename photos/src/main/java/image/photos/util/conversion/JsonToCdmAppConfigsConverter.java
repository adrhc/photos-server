package image.photos.util.conversion;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import image.cdm.AppConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;

@Component
public class JsonToCdmAppConfigsConverter implements Converter<String, List<AppConfig>> {
	private static final Logger logger = LoggerFactory.getLogger(JsonToCdmAppConfigsConverter.class);

	@Autowired
	private ObjectMapper mapper;

	@Override
	public List<AppConfig> convert(String json) {
		try {
			return this.mapper.readValue(json, new TypeReference<List<AppConfig>>() {});
		} catch (IOException e) {
			logger.error(e.getMessage(), e);
		}
		return null;
	}
}
