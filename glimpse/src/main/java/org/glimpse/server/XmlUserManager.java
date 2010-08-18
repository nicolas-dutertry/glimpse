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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.TreeSet;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import javax.servlet.ServletContext;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.glimpse.client.UserDescription;
import org.glimpse.client.UserPreferences;
import org.glimpse.client.layout.ColumnDescription;
import org.glimpse.client.layout.ComponentDescription;
import org.glimpse.client.layout.PageDescription;
import org.glimpse.client.layout.TabDescription;
import org.glimpse.client.layout.ComponentDescription.Type;
import org.springframework.web.context.ServletContextAware;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class XmlUserManager implements UserManager, ServletContextAware {
	private static final Log logger = LogFactory.getLog(XmlUserManager.class);
	
	private Configuration configuration;
	
	private File usersDirectory;
	private File passwordsFile;
	private transient Map<String, String> connectionsCache = new ConcurrentHashMap<String, String>();
	
	public XmlUserManager(Configuration configuration) {
		this.configuration = configuration;
	}
	
	public void setServletContext(ServletContext servletContext) {
		this.usersDirectory = new File(configuration.getString(
				"users.directory",
				servletContext.getRealPath("/WEB-INF/users")));
		passwordsFile = new File(usersDirectory, "passwords");
	}
	
	public String connect(String login, String password)
			throws AuthenticationException {
		checkPassword(login, password);

		File userDir = new File(usersDirectory, login);

		String connectionId = RandomStringUtils.randomAlphanumeric(64);
		File connectionDir = new File(userDir, "connections");
		connectionDir.mkdirs();
		File connectionFile = new File(connectionDir, connectionId);
		try {
			connectionFile.createNewFile();
		} catch (IOException e) {
			logger.error("Unable to create user <" + login + "> directory");
			throw new AuthenticationException("Error");
		}

		connectionsCache.put(connectionId, login);

		return connectionId;

	}

	public void checkPassword(String login, String password)
			throws AuthenticationException {
		FileInputStream fis = null;

		try {
			if (passwordsFile.exists()) {
				Properties properties = new Properties();
				fis = new FileInputStream(passwordsFile);
				properties.load(fis);
				String rightPassword = properties.getProperty(login);
				if (StringUtils.isNotEmpty(rightPassword)
						&& rightPassword.equals(password)) {
					return;
				}
			}
		} catch (Exception e) {
			logger.error("Error", e);
		} finally {
			IOUtils.closeQuietly(fis);
		}
		throw new AuthenticationException("Unable to authenticate " + login);
	}

	public void disconnect(String connectionId) {
		connectionsCache.remove(connectionId);
		String userName = getUserId(connectionId);
		if (StringUtils.isNotBlank(userName)) {
			File userDir = new File(usersDirectory, userName);
			File connectionDir = new File(userDir, "connections");
			File connectionFile = new File(connectionDir, connectionId);
			if (connectionFile.exists()) {
				connectionFile.delete();
			}
		}
	}

	public String getUserId(String connectionId) {
		String userId = connectionsCache.get(connectionId);
		if (userId != null) {
			return userId;
		}

		File[] files = usersDirectory.listFiles();
		for (File userDir : files) {
			File connectionDir = new File(userDir, "connections");
			if (connectionDir.exists() && connectionDir.isDirectory()) {
				File[] connectionFiles = connectionDir.listFiles();
				for (File connectionFile : connectionFiles) {
					if (connectionFile.getName().equals(connectionId)) {
						userId = userDir.getName();
						connectionsCache.put(connectionId, userId);
						return userId;
					}
				}
			}
		}
		return null;
	}
	

	public synchronized void createUser(String userId, String password) {
		FileInputStream fis = null;
		FileOutputStream fos = null;
		try {
			Properties properties = new Properties();
			if(passwordsFile.exists()) {
				fis = new FileInputStream(passwordsFile);
				properties.load(fis);
				fis.close();
			}
			
			String oldPassword = properties.getProperty(userId);
			if(oldPassword != null) {
				throw new RuntimeException("User <" + userId + "> already exists");
			}
			
			properties.setProperty(userId, password);
			fos = new FileOutputStream(passwordsFile);
			properties.store(fos, "");			
		} catch (IOException e) {
			throw new RuntimeException(e);
		} finally {
			IOUtils.closeQuietly(fis);
			IOUtils.closeQuietly(fos);
		}
	}

	public synchronized void setUserPassword(String userId, String password) {
		FileInputStream fis = null;
		FileOutputStream fos = null;
		try {
			Properties properties = new Properties();
			if(passwordsFile.exists()) {
				fis = new FileInputStream(passwordsFile);
				properties.load(fis);
				fis.close();
			}
			
			String oldPassword = properties.getProperty(userId);
			if(oldPassword == null) {
				throw new RuntimeException("User <" + userId + "> not found");
			}
			
			properties.setProperty(userId, password);
			fos = new FileOutputStream(passwordsFile);
			properties.store(fos, "");			
		} catch (IOException e) {
			throw new RuntimeException(e);
		} finally {
			IOUtils.closeQuietly(fis);
			IOUtils.closeQuietly(fos);
		}
	}

	public Set<String> getUsers() {
		FileInputStream fis = null;
		try {
			Properties properties = new Properties();
			if(passwordsFile.exists()) {
				fis = new FileInputStream(passwordsFile);
				properties.load(fis);
				fis.close();
			}
			
			TreeSet<String> users = new TreeSet<String>();
			Enumeration<String> names = (Enumeration<String>)properties.propertyNames();
			while (names.hasMoreElements()) {
				String user = (String) names.nextElement();
				users.add(user);
			}
			return users;
		} catch (IOException e) {
			throw new RuntimeException(e);
		} finally {
			IOUtils.closeQuietly(fis);
		}
	}


	public UserDescription getDefaultUserDescription() {
		UserDescription userDescription = getUserDescription(UserDescription.GUEST_ID);
		return userDescription;
	}
	
	public UserDescription getUserDescription(String userId) {
		UserDescription userDescription = getExistingUserDescription(userId);
		if(userDescription == null) {
			userDescription = getExistingUserDescription(
					UserDescription.GUEST_ID);
			if(userDescription != null) {
				userDescription.setId(userId);
			} else {
				userDescription = new UserDescription(userId);
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
			
			userDescription.setAdministrator(
					"true".equals(doc.getDocumentElement().getAttribute("administrator")));
			
			String label = (String)xpath.evaluate("/user/label",
					doc, XPathConstants.STRING);
			userDescription.getPreferences().setLabel(label);
			
			String locale = (String)xpath.evaluate("/user/locale",
					doc, XPathConstants.STRING);
			userDescription.getPreferences().setLocale(locale);
			
			String theme = (String)xpath.evaluate("/user/theme",
					doc, XPathConstants.STRING);
			userDescription.getPreferences().setTheme(theme);
			
			return userDescription;			
		} catch(Exception e) {
			logger.error("Error while reading user description xml", e);
			return null;
		}
	}
	
	public void setUserPreferences(String userId,
			UserPreferences userPreferences) {
		UserDescription userDescription = getUserDescription(userId);
		userDescription.setPreferences(userPreferences);
		setUserDescription(userId, userDescription);
	}

	private void setUserDescription(String userId,
			UserDescription userDescription) {
		File userDir = new File(usersDirectory, userId);
		File userFile = new File(userDir, "description.xml");
		
		try {			
			DocumentBuilder builder =
				DocumentBuilderFactory.newInstance().newDocumentBuilder();
			Document doc = builder.newDocument();
			
			Element userElement = doc.createElement("user");
			userElement.setAttribute("administrator",
					Boolean.toString(userDescription.isAdministrator()));
			doc.appendChild(userElement);
			
			String label = userDescription.getPreferences().getLabel();
			if(label != null) {
				Element labelElement = doc.createElement("label");
				userElement.appendChild(labelElement);
				labelElement.appendChild(doc.createTextNode(label));
			}
			
			String locale = userDescription.getPreferences().getLocale();
			if(locale != null) {
				Element localeElement = doc.createElement("locale");
				userElement.appendChild(localeElement);
				localeElement.appendChild(doc.createTextNode(locale));
			}
			
			String theme = userDescription.getPreferences().getTheme();
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
	
	public PageDescription getUserPageDescription(String localeName, String userId) {
		PageDescription pageDescription = getExistingUserPageDescription(userId);
		if(pageDescription == null) {
			pageDescription = getExistingUserPageDescription(
					UserDescription.GUEST_ID + "_" + localeName);
		}
		if(pageDescription == null) {
			try {
				DocumentBuilder builder =
					DocumentBuilderFactory.newInstance().newDocumentBuilder();
				InputStream is = getClass().getResourceAsStream(
					"/org/glimpse/server/default-page-" + localeName + ".xml");
				if(is == null) {
					is = getClass().getResourceAsStream(
						"/org/glimpse/server/default-page.xml");
				}
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
	
	public PageDescription getDefaultPageDescription(String localeName) {
		return getUserPageDescription(localeName, UserDescription.GUEST_ID + "_" + localeName);
	}

	public void setDefaultPageDescription(String localeName, PageDescription pageDescription) {
		setUserPageDescription(UserDescription.GUEST_ID + "_" + localeName, pageDescription);
		
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

	public boolean isAdministrator(String userId) {
		File userDir = new File(usersDirectory, userId);
		File userFile = new File(userDir, "description.xml");
		
		if(!userFile.exists()) {
			return false;
		}
		
		try {
			DocumentBuilder builder =
				DocumentBuilderFactory.newInstance().newDocumentBuilder();			
			Document doc = builder.parse(userFile);
			
			return "true".equals(
					doc.getDocumentElement().getAttribute("administrator"));			
		} catch(Exception e) {
			logger.error("Error while reading user description xml", e);
			return false;
		}
	}

}
