package org.glimpse.client.layout;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class ColumnDescription implements Serializable {
	private static final long serialVersionUID = 1L;

	private List<ComponentDescription> componentDescriptions = new ArrayList<ComponentDescription>();
	
	public List<ComponentDescription> getComponentDescriptions() {
		return new ArrayList<ComponentDescription>(componentDescriptions);
	}
	
	public void addComponentDescription(ComponentDescription componentDescription) {
		componentDescriptions.add(componentDescription);
	}
	
	public void removeComponentDescription(ComponentDescription componentDescription) {
		componentDescriptions.remove(componentDescription);
	}
}
