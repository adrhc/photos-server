package image.exifweb.gallery3;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: adr
 * Date: 5/15/14
 * Time: 1:09 AM
 * To change this template use File | Settings | File Templates.
 */
public class Gallery3TreeParent implements Serializable {
    private String url;
    private List<Gallery3TreeEntity> entity = new ArrayList<Gallery3TreeEntity>();
    private List<String> members = new ArrayList<String>();
    private List<String> relationships = new ArrayList<String>();

    public Gallery3TreeParent(String url) {
        this.url = url;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public List<Gallery3TreeEntity> getEntity() {
        return entity;
    }

    public void setEntity(List<Gallery3TreeEntity> entity) {
        this.entity = entity;
    }

    public List<String> getMembers() {
        return members;
    }

    public void setMembers(List<String> members) {
        this.members = members;
    }

    public List<String> getRelationships() {
        return relationships;
    }

    public void setRelationships(List<String> relationships) {
        this.relationships = relationships;
    }
}
