package org.glimpse.client.news;

import java.io.Serializable;

public class Entry implements Serializable {
	private static final long serialVersionUID = 1L;
	
	private String id;
	private String title;
	private String url;
	
	public String getId() {
		return id;
	}
	
	public void setId(String id) {
		this.id = id;
	}
	
	public String getTitle() {
		return title;
	}
	
	public void setTitle(String title) {
		this.title = title;
	}
	
	public String getUrl() {
		return url;
	}
	
	public void setUrl(String url) {
		this.url = url;
	}
}
