package org.glimpse.server;

import java.io.File;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.glimpse.client.UserDescription;
import org.glimpse.client.layout.ColumnDescription;
import org.glimpse.client.layout.ComponentDescription;
import org.glimpse.client.layout.PageDescription;
import org.glimpse.client.layout.TabDescription;
import org.glimpse.client.layout.ComponentDescription.Type;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class XmlUserManager implements UserManager {
	private static final Log logger = LogFactory.getLog(XmlUserManager.class);
	
	private File usersDirectory;
	
	public XmlUserManager(File usersDirectory) {
		this.usersDirectory = usersDirectory;
	}

	public UserDescription getDefaultUserDescription() {
		UserDescription userDescription = getUserDescription(UserDescription.ADMIN_ID);
		userDescription.setId(UserDescription.GUEST_ID);
		return userDescription;
	}
	
	public UserDescription getUserDescription(String userId) {
		UserDescription userDescription = getExistingUserDescription(userId);
		if(userDescription == null) {
			userDescription = getExistingUserDescription(
					UserDescription.ADMIN_ID);
			if(userDescription != null) {
				userDescription.setId(userId);
			} else {
				userDescription = new UserDescription(userId);
				userDescription.setTheme("default");
			}
		}
		return userDescription;
	}

	private UserDescription getExistingUserDescription(String userId) {
		File userDir = new File(usersDirectory, userId);
		File userFile = new File(userDir, "description.xml");
		
		if(!userFile.exists()) {
			return null;
		}
		
		try {
			DocumentBuilder builder =
				DocumentBuilderFactory.newInstance().newDocumentBuilder();			
			Document doc = builder.parse(userFile);
			
			UserDescription userDescription = new UserDescription(userId);
			XPath xpath = XPathFactory.newInstance().newXPath();
			
			String label = (String)xpath.evaluate("/user/label",
					doc, XPathConstants.STRING);
			userDescription.setLabel(label);
			
			String locale = (String)xpath.evaluate("/user/locale",
					doc, XPathConstants.STRING);
			userDescription.setLocale(locale);
			
			String theme = (String)xpath.evaluate("/user/theme",
					doc, XPathConstants.STRING);
			userDescription.setTheme(theme);
			
			return userDescription;			
		} catch(Exception e) {
			logger.error("Error while reading user description xml", e);
			return null;
		}
	}

	public void setUserDescription(String userId,
			UserDescription userDescription) {
		File userDir = new File(usersDirectory, userId);
		File userFile = new File(userDir, "description.xml");
		
		try {			
			DocumentBuilder builder =
				DocumentBuilderFactory.newInstance().newDocumentBuilder();
			Document doc = builder.newDocument();
			
			Element userElement = doc.createElement("user");
			doc.appendChild(userElement);
			
			String label = userDescription.getLabel();
			if(label != null) {
				Element labelElement = doc.createElement("label");
				userElement.appendChild(labelElement);
				labelElement.appendChild(doc.createTextNode(label));
			}
			
			String locale = userDescription.getLocale();
			if(locale != null) {
				Element localeElement = doc.createElement("locale");
				userElement.appendChild(localeElement);
				localeElement.appendChild(doc.createTextNode(locale));
			}
			
			String theme = userDescription.getTheme();
			if(theme != null) {
				Element themeElement = doc.createElement("theme");
				userElement.appendChild(themeElement);
				themeElement.appendChild(doc.createTextNode(theme));
			}
			
			if(!userDir.exists()) {
				userDir.mkdir();
			}
			
			Transformer transformer =
				TransformerFactory.newInstance().newTransformer();
			transformer.transform(new DOMSource(doc),
					new StreamResult(userFile));
		} catch(Exception e) {
			logger.error("Error while updating user description xml", e);
		}

	}
	
	public PageDescription getUserPageDescription(String userId) {
		PageDescription pageDescription = getExistingUserPageDescription(userId);
		if(pageDescription == null) {
			pageDescription = getExistingUserPageDescription(UserDescription.ADMIN_ID);
		}
		if(pageDescription == null) {
			try {
				DocumentBuilder builder =
					DocumentBuilderFactory.newInstance().newDocumentBuilder();
				InputStream is = getClass().getClassLoader().getResourceAsStream(
						"/org/glimpse/server/default-page.xml");
				Document doc = builder.parse(is);
				
				return buildPage(doc);
				
			} catch(Exception e) {
				logger.error("Error while reading page description xml", e);
				return null;
			}
		}
		return pageDescription;
	}

	private PageDescription getExistingUserPageDescription(String userId) {
		File userDir = new File(usersDirectory, userId);
		File pageFile = new File(userDir, "page.xml");
		
		if(!pageFile.exists()) {
			return null;
		}
		
		try {
			DocumentBuilder builder =
				DocumentBuilderFactory.newInstance().newDocumentBuilder();			
			Document doc = builder.parse(pageFile);
			
			return buildPage(doc);
			
		} catch(Exception e) {
			logger.error("Error while reading page description xml", e);
			return null;
		}
	}
	
	public PageDescription getDefaultPageDescription() {
		return getUserPageDescription(UserDescription.ADMIN_ID);
	}

	public void setUserPageDescription(String userId,
			PageDescription pageDescription) {
		File userDir = new File(usersDirectory, userId);
		File pageFile = new File(userDir, "page.xml");
		
		try {			
			DocumentBuilder builder =
				DocumentBuilderFactory.newInstance().newDocumentBuilder();
			Document doc = builder.newDocument();
			
			Element pageElement = doc.createElement("page");
			doc.appendChild(pageElement);
			
			List<TabDescription> tabDescriptions = pageDescription.getTabDescriptions();
			for (TabDescription tabDescription : tabDescriptions) {
				Element tabElement = doc.createElement("tab");
				tabElement.setAttribute("title", tabDescription.getTitle());
				
				List<ColumnDescription> columnDescriptions = tabDescription.getColumnDescriptions();
				for (ColumnDescription columnDescription : columnDescriptions) {
					Element columnElement = doc.createElement("column");
					
					List<ComponentDescription> componentDescriptions =
						columnDescription.getComponentDescriptions();
					for (ComponentDescription componentDescription : componentDescriptions) {
						Element componentElement = doc.createElement("component");
						componentElement.setAttribute("type", componentDescription.getType().toString());
						
						Map<String, String> properties = componentDescription.getProperties();
						Set<Entry<String, String>> entries = properties.entrySet();
						for (Entry<String, String> entry : entries) {
							Element propertyElement = doc.createElement("property");
							propertyElement.setAttribute("name", entry.getKey());
							propertyElement.setAttribute("value", entry.getValue());
							componentElement.appendChild(propertyElement);
						}						
						columnElement.appendChild(componentElement);
					}					
					tabElement.appendChild(columnElement);
				}				
				pageElement.appendChild(tabElement);
			}
			
			if(!userDir.exists()) {
				userDir.mkdir();
			}
			
			Transformer transformer =
				TransformerFactory.newInstance().newTransformer();
			transformer.transform(new DOMSource(doc),
					new StreamResult(pageFile));
			
		} catch(Exception e) {
			logger.error("Error while updating page description xml", e);
		}
	}
	
	private PageDescription buildPage(Document doc) throws Exception {
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
	
	private TabDescription buildTab(Document doc, Element tabElement) throws Exception {
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
	
	private ColumnDescription buildColumn(Document doc, Element columnElement) throws Exception {
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
	
	private ComponentDescription buildComponent(Document doc, Element componentElement) throws Exception {
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

}
