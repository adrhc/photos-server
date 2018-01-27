package image.exifweb.gallery3;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: adr
 * Date: 5/14/14
 * Time: 9:43 PM
 * To change this template use File | Settings | File Templates.
 */
public class Gallery3ItemParent implements Serializable {
	private String url;
	private Map<String, String> entity = new HashMap<String, String>(3, 1);
	private Integer kidsCount;
	private List<Gallery3Item> members = new ArrayList<Gallery3Item>();
	private List<String> relationships = new ArrayList<String>();

	public Gallery3ItemParent(String url) {
		this.url = url;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public Map<String, String> getEntity() {
		return entity;
	}

	public void setEntity(Map<String, String> entity) {
		this.entity = entity;
	}

	public Integer getKidsCount() {
		return kidsCount;
	}

	public void setKidsCount(Integer kidsCount) {
		this.kidsCount = kidsCount;
	}

	public List<Gallery3Item> getMembers() {
		return members;
	}

	public void setMembers(List<Gallery3Item> members) {
		this.members = members;
	}

	public List<String> getRelationships() {
		return relationships;
	}

	public void setRelationships(List<String> relationships) {
		this.relationships = relationships;
	}
}
