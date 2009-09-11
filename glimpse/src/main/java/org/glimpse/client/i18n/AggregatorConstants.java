package org.glimpse.client.i18n;

import com.google.gwt.i18n.client.Constants;

public interface AggregatorConstants extends Constants {
	@DefaultStringValue("OK")
	String ok();
	
	@DefaultStringValue("Cancel")
	String cancel();
	
	@DefaultStringValue("Close")
	String close();
	
	@DefaultStringValue("Error")
	String error();
	
	@DefaultStringValue("Loading...")
	String loading();
	
	@DefaultStringValue("Add content")
	String addContent();
	
	@DefaultStringValue("Type")
	String type();
	
	@DefaultStringValue("News reader")
	String newsReader();
	
	@DefaultStringValue("Link")
	String link();
	
	@DefaultStringValue("HTML")
	String html();
	
	@DefaultStringValue("Title")
	String title();
	
	@DefaultStringValue("Number of columns")
	String numberOfColumns();
	
	@DefaultStringValue("Some component will be lost. Do you want to proceed anyway ?")
	String removeColumnWarning();
	
	@DefaultStringValue("Delete this tab")
	String deleteTab();
	
	@DefaultStringValue("Are you sure you want to delete this tab ?")
	String deleteTabConfirm();
	
	@DefaultStringValue("Are you sure you want to delete this component ?")
	String deleteComponentConfirm();
	
	@DefaultStringValue("url")
	String url();
	
	@DefaultStringValue("Open directly on site")
	String directOpen();
	
	@DefaultStringValue("Maximum number of elements")
	String maxEntries();
	
	@DefaultStringValue("previous")
	String previous();
	
	@DefaultStringValue("next")
	String next();
	
	@DefaultStringValue("reload")
	String refresh();
	
	@DefaultStringValue("options")
	String options();
	
	@DefaultStringValue("delete")
	String delete();
	
	@DefaultStringValue("move")
	String move();
}
