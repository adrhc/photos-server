package image.exifweb.web.json;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.io.Serializable;

/**
 * Created by adrian.petre on 10-06-2014.
 */
public class JsonStringValue implements Comparable<JsonStringValue>, Serializable {
	private String value;

	public JsonStringValue() {
	}

	public JsonStringValue(String value) {
		this.value = value;
	}

	public String getValue() {
		return this.value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	@JsonIgnore
	public Integer getValueAsInteger() {
		return Integer.valueOf(this.value);
	}

	@Override
	public int compareTo(JsonStringValue o) {
		if (o == null || o.value == null) {
			if (this.value == null) {
				return 0;
			} else {
				return 1;
			}
		} else if (this.value == null) {
			return -1;
		}
		return this.value.compareToIgnoreCase(o.value);
	}
}
