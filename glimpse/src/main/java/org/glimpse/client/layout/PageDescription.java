package org.glimpse.client.layout;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class PageDescription implements Serializable {
	private static final long serialVersionUID = 1L;
	
	private String userName;
	private List<TabDescription> tabDescriptions = new ArrayList<TabDescription>();
	
	public PageDescription() {
		this(null);
	}
	
	public PageDescription(String userName) {
		this.userName = userName;
	}
	
	public List<TabDescription> getTabDescriptions() {
		return new ArrayList<TabDescription>(tabDescriptions);
	}
	
	public void removeTabDescription(TabDescription tab) {
		tabDescriptions.remove(tab);
	}
	
	public void addTabDescription(TabDescription tab) {
		tabDescriptions.add(tab);
	}
	
	public String getUserName() {
		return userName;
	}
	
	public void setUserName(String userName) {
		this.userName = userName;
	}

}
