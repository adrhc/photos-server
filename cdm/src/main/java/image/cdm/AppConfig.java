package image.cdm;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class AppConfig implements ICdmEntity {
	private Integer id;
	private String name;
	private String value;
}
