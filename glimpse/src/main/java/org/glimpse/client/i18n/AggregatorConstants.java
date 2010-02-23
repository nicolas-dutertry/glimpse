/*
 * Copyright (C) 2009 Nicolas Dutertry
 * 
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
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
	
	@DefaultStringValue("Login")
	String login();
	
	@DefaultStringValue("Logout")
	String logout();
	
	@DefaultStringValue("Login ID")
	String loginId();
	
	@DefaultStringValue("Password")
	String password();
	
	@DefaultStringValue("Remember me on this computer")
	String rememberMe();
	
	@DefaultStringValue("one minute ago")
	String oneMinuteAgo();
	
	@DefaultStringValue("one hour ago")
	String oneHourAgo();
	
	@DefaultStringValue("yesterday")
	String yesterday();
	
	@DefaultStringValue("one month ago")
	String oneMonthAgo();
	
	@DefaultStringValue("one year ago")
	String oneYearAgo();
	
	@DefaultStringValue("Options")
	String userOptions();
	
	@DefaultStringValue("Language")
	String language();
	
	@DefaultStringValue("Theme")
	String theme();
	
	@DefaultStringValue("Français")
	String francais();
	
	@DefaultStringValue("English")
	String english();
	
	@DefaultStringValue("Incorrect login/password")
	String incorrectLoginPassword();
	
	@DefaultStringValue("New tab")
	String newTab();
	
	@DefaultStringValue("Pages")
	String pages();
	
	@DefaultStringValue("My page")
	String myPage();
	
	@DefaultStringValue("Default page")
	String defaultPage();
	
	@DefaultStringValue("Podcast")
	String podcast();
	
	@DefaultStringValue("Default page administration")
	String defaultPageAdministration();
	
	@DefaultStringValue("Page locale")
	String pageLocale();
}
