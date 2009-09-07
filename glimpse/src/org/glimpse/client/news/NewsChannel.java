package org.glimpse.client.news;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

public class NewsChannel implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private String url;
	private String title;
	private List<Entry> entries;
	
	public NewsChannel() {		
	}
	
	public NewsChannel(String url, String title, List<Entry> entries) {
		this.url = url;
		this.title = title;
		this.entries = entries;
	}

	public String getUrl() {
		return url;
	}

	public String getTitle() {
		return title;
	}

	public List<Entry> getEntries() {
		return new LinkedList<Entry>(entries);
	}
	
	

}
