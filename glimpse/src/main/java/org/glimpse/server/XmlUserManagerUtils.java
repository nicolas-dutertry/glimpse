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
package org.glimpse.server;

import java.util.HashMap;
import java.util.Map;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.glimpse.client.UserAttributes;
import org.glimpse.client.layout.ColumnDescription;
import org.glimpse.client.layout.ComponentDescription;
import org.glimpse.client.layout.PageDescription;
import org.glimpse.client.layout.TabDescription;
import org.glimpse.client.layout.ComponentDescription.Type;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class XmlUserManagerUtils {
	public static PageDescription buildPage(Document doc) throws Exception {
		PageDescription page = new PageDescription();		
		
		XPath xpath = XPathFactory.newInstance().newXPath();
		NodeList tabNodes = (NodeList)xpath.evaluate("/page/tab", doc, XPathConstants.NODESET);
		for(int i = 0; i < tabNodes.getLength(); i++) {
			Element tabElement = (Element)tabNodes.item(i);
			TabDescription tab = buildTab(doc, tabElement);
			page.addTabDescription(tab);
		}
		
		return page;
	}
	
	private static TabDescription buildTab(Document doc, Element tabElement) throws Exception {
		TabDescription tab = new TabDescription();
		tab.setTitle(tabElement.getAttribute("title"));
		
		XPath xpath = XPathFactory.newInstance().newXPath();
		NodeList columnNodes = (NodeList)xpath.evaluate("column", tabElement, XPathConstants.NODESET);
		for(int i = 0; i < columnNodes.getLength(); i++) {
			Element columnElement = (Element)columnNodes.item(i);
			ColumnDescription column = buildColumn(doc, columnElement);
			tab.addColumnDescription(column);
		}
		return tab;
	}
	
	private static ColumnDescription buildColumn(Document doc, Element columnElement) throws Exception {
		ColumnDescription column = new ColumnDescription();
		
		XPath xpath = XPathFactory.newInstance().newXPath();
		NodeList componentNodes = (NodeList)xpath.evaluate("component", columnElement, XPathConstants.NODESET);
		for(int i = 0; i < componentNodes.getLength(); i++) {
			Element componentElement = (Element)componentNodes.item(i);
			ComponentDescription component = buildComponent(doc, componentElement);
			column.addComponentDescription(component);
		}
		return column;
	}
	
	private static ComponentDescription buildComponent(Document doc, Element componentElement) throws Exception {
		ComponentDescription component = new ComponentDescription(
				Type.valueOf(componentElement.getAttribute("type")));
		Map<String, String> properties = new HashMap<String, String>();
		XPath xpath = XPathFactory.newInstance().newXPath();
		NodeList propertyNodes = (NodeList)xpath.evaluate("property", componentElement, XPathConstants.NODESET);
		for(int i = 0; i < propertyNodes.getLength(); i++) {
			Element propertyElement = (Element)propertyNodes.item(i);
			properties.put(propertyElement.getAttribute("name"),
					propertyElement.getAttribute("value"));
		}
		component.setProperties(properties);
		return component;
	}
	
	public static UserAttributes buildUserAttributes(Document doc) throws XPathExpressionException {
		UserAttributes userAttributes = new UserAttributes();
		XPath xpath = XPathFactory.newInstance().newXPath();
		
		userAttributes.setAdministrator(
				"true".equals(doc.getDocumentElement().getAttribute("administrator")));
		
		String label = (String)xpath.evaluate("/user/label",
				doc, XPathConstants.STRING);
		userAttributes.getPreferences().setLabel(label);
		
		String locale = (String)xpath.evaluate("/user/locale",
				doc, XPathConstants.STRING);
		userAttributes.getPreferences().setLocale(locale);
		
		String theme = (String)xpath.evaluate("/user/theme",
				doc, XPathConstants.STRING);
		userAttributes.getPreferences().setTheme(theme);
		
		return userAttributes;
	}
}
