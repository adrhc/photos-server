package image.cdm;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
public class AppConfig implements Serializable {
	private Integer id;
	private String name;
	private String value;
}
