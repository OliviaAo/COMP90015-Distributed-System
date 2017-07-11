package org.sw.comm;

import org.apache.wink.json4j.OrderedJSONObject;
import org.apache.wink.json4j.JSONArray;
import org.apache.wink.json4j.JSONException;
import org.apache.wink.json4j.JSONObject;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

/**
 * Resources can be files or URIs. Resources need to be stored, looked up and transmitted.
 * Shared files are file URIs. The EZShare server lists shared file that can be downloaded.
 * Other URIs, such as http, ftp, are for reference only.
 * This class is used to store attributes of resources. Attributes can't contain "\0" or
 * start / end with white space. 
 * @author Sheng Wu
 *
 */
public class Resource implements Cloneable {
	/**
	 * (owner, channel, uri) is PK. The info is kept secret by servers. The default channel 
	 * is public, others are private. The default owner means anyone can update the resource.
	 * Otherwise updates require the correct owner name to work. 
	 */
    private String name; // optional; default ""
    private String description; // optional; default ""
    private List<String> tags; //optional, array of Strings; default empty list
    private URI uri; //mandatory(unique)
    private String channel; //optional; default ""
    private String owner; //optional; default ""; can't be "*"
    private ServerBean serverBean; //optional; default ""
    private long size; // optional; file size(B)

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }

    public URI getUri() {
        return uri;
    }

    public void setUri(URI uri) {
        this.uri = uri;
    }

    public String getChannel() {
        return channel;
    }

    public void setChannel(String channel) {
        this.channel = channel;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public ServerBean getServerBean() {
        return serverBean;
    }

    public void setServerBean(ServerBean serverBean) {
        this.serverBean = serverBean;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public static boolean checkValidity(JSONObject resourceObject) {
        return (resourceObject.has("name") && resourceObject.has("tags") && resourceObject.has("description") && resourceObject.has("uri") && resourceObject.has("channel") && resourceObject.has("owner") && resourceObject.has("ezserver"));
    }

    public static OrderedJSONObject toJson(Resource resource) {
    	OrderedJSONObject jsonObject = new OrderedJSONObject();
    	JSONArray tagArray = new JSONArray();
    	if (resource.getTags()!=null){
    		for (String tag : resource.getTags()) {
    			try {
					tagArray.put(tag);
				} catch (JSONException e) { 
					e.printStackTrace();
				}
    		}
    	}
    	try {
    		jsonObject.put("name", resource.getName()==null?"":resource.getName());
    		jsonObject.put("tags", tagArray);
    		jsonObject.put("description", resource.getDescription()==null?"":resource.getDescription());
    		jsonObject.put("uri",  resource.getUri()==null?"":resource.getUri().toString());
    		jsonObject.put("channel",resource.getChannel()==null?"":resource.getChannel());
    		jsonObject.put("owner", resource.getOwner()==null?"":resource.getOwner());
    		jsonObject.put("ezserver",resource.getServerBean()==null?"":resource.getServerBean().toString());
    		if (resource.getSize()>0){
    			jsonObject.put("resourceSize",resource.getSize());
    		}
    	} catch (org.apache.wink.json4j.JSONException e) { 
    		e.printStackTrace();
    	}

    	return jsonObject;
    }

    public static Resource parseJson(JSONObject resourceObject) {
    	String name = "";
    	String description = "";
    	String owner = "";
    	String uriString = "";
    	URI uri = null;
    	String channel = "";
    	String ezServerString = "";
    	ServerBean serverBean = null;
    	List<String> tagList = new ArrayList<>();
    	
    	if(!checkValidity(resourceObject)){
    		return null;
    	} 
    	try {
    		name = resourceObject.getString("name");
    		description = resourceObject.getString("description");
    		uriString = resourceObject.getString("uri");
    		try {
    			uri = new URI(uriString);
    		} catch (URISyntaxException e) {
    			e.printStackTrace();
    		}
    		owner = resourceObject.getString("owner");
    		channel = resourceObject.getString("channel");
    		ezServerString = resourceObject.getString("ezserver");
    		if (!ezServerString.equals("")){
    			String ezHost = ezServerString.split(":")[0];
    			int port = Integer.parseInt(ezServerString.split(":")[1]);
    			serverBean = new ServerBean(ezHost, port);
    		}
    		JSONArray tagArray = resourceObject.getJSONArray("tags");
    		
    		for (int i = 0; i < tagArray.length(); i++) {
    			tagList.add(tagArray.getString(i));
    		}
    	} catch (JSONException e1) { 
    		e1.printStackTrace();
    	}

		Resource resource = new Resource();
		resource.setName(name);
		resource.setChannel(channel);
		resource.setDescription(description);
		resource.setOwner(owner);
		resource.setUri(uri);
		resource.setTags(tagList);
		resource.setServerBean(serverBean);
		if (resourceObject.has("resourceSize")){
			try {
				resource.setSize(resourceObject.getInt("resourceSize"));
			} catch (JSONException e) { 
				e.printStackTrace();
			}
		}
		return resource;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Resource)) return false;
        Resource resource=(Resource) obj;
        return resource.getOwner().equals(this.owner)&&resource.getChannel().equals(this.channel)&&resource.getUri().equals(this.uri);
    }

    @Override
    public Resource clone() throws CloneNotSupportedException {
        Resource copiedResource=new Resource();
        copiedResource.setName(new String(this.getName()));
        copiedResource.setDescription(new String(this.getDescription()));
        copiedResource.setChannel(new String(this.getChannel()));
        copiedResource.setOwner(new String(this.getOwner()));
        try {
            copiedResource.setUri(new URI(this.getUri().toString()));
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        if (this.getServerBean()!=null){
            copiedResource.setServerBean(new ServerBean(this.getServerBean().getHostname(), this.getServerBean().getPort()));
        }
        List<String> copiedTags = new ArrayList<>();
        if (this.getTags()!=null){
            List<String> tags = this.getTags();
            tags.forEach(tag -> copiedTags.add(tag));
        }
        copiedResource.setTags(copiedTags);
        copiedResource.setSize(this.size);
        return copiedResource;
    }
}
