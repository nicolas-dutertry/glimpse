package org.glimpse.client;

import java.io.Serializable;

public class UserDescription implements Serializable {
	private static final long serialVersionUID = 1L;
	public static final String GUEST_ID = "guest";
	
	private String id;
	private String label;
	private String locale;
	private String theme;
	
	public UserDescription() {		
	}
	
	public UserDescription(String id) {
		this.id = id;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public String getLocale() {
		return locale;
	}

	public void setLocale(String locale) {
		this.locale = locale;
	}

	public String getTheme() {
		return theme;
	}

	public void setTheme(String theme) {
		this.theme = theme;
	}

	public String getId() {
		return id;
	}
	
	
}
