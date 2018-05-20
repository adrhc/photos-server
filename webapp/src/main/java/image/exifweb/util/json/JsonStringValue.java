package image.exifweb.util.json;

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
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	@JsonIgnore
	public Integer getValueAsInteger() {
		return new Integer(value);
	}

	@Override
	public int compareTo(JsonStringValue o) {
		if (o == null || o.value == null) {
			if (value == null) {
				return 0;
			} else {
				return 1;
			}
		} else if (value == null) {
			return -1;
		}
		return value.compareToIgnoreCase(o.value);
	}
}
