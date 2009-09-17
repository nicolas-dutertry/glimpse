package org.glimpse.server;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import javax.servlet.ServletException;
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
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.glimpse.client.PageDescriptionService;
import org.glimpse.client.layout.ColumnDescription;
import org.glimpse.client.layout.ComponentDescription;
import org.glimpse.client.layout.PageDescription;
import org.glimpse.client.layout.TabDescription;
import org.glimpse.client.layout.ComponentDescription.Type;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

public class XmlPageDescriptionService extends RemoteServiceServlet implements
		PageDescriptionService {
	private static final long serialVersionUID = 1L;
	private static final Log logger = LogFactory.getLog(XmlPageDescriptionService.class);
	private static final String DEFAULT_XML_PAGE = "/WEB-INF/page.xml";
	
	private File usersDirectory;

	@Override
	public void init() throws ServletException {
		super.init();
		
		GlimpseManager glimpseManager = GlimpseManager.getInstance(getServletContext());
		Configuration configuration = glimpseManager.getConfiguration();
		usersDirectory = new File(configuration.getString("users.directory",
				getServletContext().getRealPath("/WEB-INF/users")));
		
		logger.debug("user directory : " + usersDirectory.getAbsolutePath());
	}

	public PageDescription getPageDescription() {
		try {
			DocumentBuilder builder =
				DocumentBuilderFactory.newInstance().newDocumentBuilder();
			
			File userXMLPage = getUserXMLPage();
			if(userXMLPage == null || !userXMLPage.exists()) {
				userXMLPage = getDefaultXMLPage();
			}
			Document doc = builder.parse(userXMLPage);
			
			return buildPage(doc);
			
		} catch(Exception e) {
			logger.error("Error while reading page description xml", e);
			return null;
		}
	}
	
	private File getUserXMLPage() {
		String connectionId = GlimpseUtils.getConnectionId(getThreadLocalRequest());
		if(StringUtils.isNotEmpty(connectionId)) {
			GlimpseManager glimpseManager = GlimpseManager.getInstance(getServletContext());
			ConnectionManager connectionManager = glimpseManager.getConnectionManager();
			String userName = connectionManager.getUserId(connectionId);
			if(userName != null) {
				return new File(usersDirectory, userName + "/page.xml");
			}
		}
		return null;
	}
	
	private File getDefaultXMLPage() {
		return new File(getServletContext().getRealPath(DEFAULT_XML_PAGE));
	}
	
	private PageDescription buildPage(Document doc) throws Exception {
		PageDescription page = new PageDescription();
		String connectionId = GlimpseUtils.getConnectionId(getThreadLocalRequest());
		if(StringUtils.isNotEmpty(connectionId)) {
			ConnectionManager connectionManager =
				GlimpseManager.getInstance(getServletContext()).getConnectionManager();
			String userName = connectionManager.getUserId(connectionId);
			page.setUserName(userName);
		}
		
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

	public void setPageDescription(PageDescription pageDescription) {
		try {
			File userXMLPage = getUserXMLPage();
			if(userXMLPage == null) {
				return;
			}
			
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
			
			if(!userXMLPage.getParentFile().exists()) {
				userXMLPage.getParentFile().mkdir();
			}
			
			Transformer transformer =
				TransformerFactory.newInstance().newTransformer();
			transformer.transform(new DOMSource(doc),
					new StreamResult(userXMLPage));
			
		} catch(Exception e) {
			logger.error("Error while updating page description xml", e);
		}
		
	}

}
