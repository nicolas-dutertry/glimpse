package org.glimpse.client.layout;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class ComponentDescription implements Serializable {
	private static final long serialVersionUID = 1L;
	
	public enum Type {
		NEWS
	}
	
	private Type type;
	private Map<String, String> properties = new HashMap<String, String>();
	
	public ComponentDescription() {
		type = Type.NEWS;
	}
	
	public ComponentDescription(Type type) {
		this.type = type;
	}
	
	public Type getType() {
		return type;
	}
	
	public Map<String, String> getProperties() {
		return new HashMap<String, String>(properties);
	}
	
	public void setProperties(Map<String, String> properties) {
		this.properties = new HashMap<String, String>(properties);
	}
}
