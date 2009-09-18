package org.glimpse.client.layout;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class PageDescription implements Serializable {
	private static final long serialVersionUID = 1L;
	
	private List<TabDescription> tabDescriptions = new ArrayList<TabDescription>();
	
	
	public List<TabDescription> getTabDescriptions() {
		return new ArrayList<TabDescription>(tabDescriptions);
	}
	
	public void removeTabDescription(TabDescription tab) {
		tabDescriptions.remove(tab);
	}
	
	public void addTabDescription(TabDescription tab) {
		tabDescriptions.add(tab);
	}

}
