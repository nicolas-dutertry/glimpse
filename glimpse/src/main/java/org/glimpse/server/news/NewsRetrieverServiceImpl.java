package org.glimpse.server.news;

import java.text.DateFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.glimpse.client.news.Entry;
import org.glimpse.client.news.NewsChannel;
import org.glimpse.client.news.NewsRetrieverService;
import org.glimpse.server.GlimpseManager;
import org.glimpse.server.Proxy;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

public class NewsRetrieverServiceImpl extends RemoteServiceServlet implements
		NewsRetrieverService {
	private static final long serialVersionUID = 1L;
	private static Log logger = LogFactory.getLog(NewsRetrieverServiceImpl.class);
	
	private enum Type {
		RSS,
		ATOM
	}

	public NewsChannel getNewsChannel(String url) {
		try {
			Document doc = getDocument(url);
			switch(getType(doc)) {
				case RSS :
					return new NewsChannel(getRSSLink(doc), getRSSTitle(doc),
							getRSSEntries(doc));			
				case ATOM :
					return new NewsChannel(getAtomLink(doc), getAtomTitle(doc),
							getAtomEntries(doc));
			}
		} catch(Exception e) {
			logger.error("Error while get Entries for <" + url + ">", e);
		}

		return null;
	}
	
	private Document getDocument(String url) throws Exception {
		GlimpseManager glimpseManager = GlimpseManager.getInstance(getServletContext());
		Proxy proxy = glimpseManager.getProxy(url);
		
		HttpClient client = new HttpClient();		
		if(proxy != null) {
			client.getHostConfiguration().setProxy(proxy.getHost(), proxy.getPort());
		}
		
		GetMethod method = new GetMethod(url);
		client.executeMethod(method);
		
		DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
		return builder.parse(method.getResponseBodyAsStream());
	}
	
	private List<Entry> getRSSEntries(Document doc) throws Exception {
		XPath xpath = XPathFactory.newInstance().newXPath();
		List<Entry> rssEntries = new LinkedList<Entry>();
		NodeList entries = (NodeList)xpath.evaluate("/rss/channel/item",
				doc,
				XPathConstants.NODESET);
		for(int i = 0; i < entries.getLength(); i++) {
			Entry rssEntry = new Entry();
			Element elmEntry = (Element)entries.item(i);
			String id = (String)xpath.evaluate("link", elmEntry, XPathConstants.STRING);
			String title = (String)xpath.evaluate("title", elmEntry, XPathConstants.STRING);
			String entryUrl = (String)xpath.evaluate("link", elmEntry, XPathConstants.STRING);
			String pubDate = (String)xpath.evaluate("pubDate", elmEntry, XPathConstants.STRING);
						
			rssEntry.setId(id);
			rssEntry.setTitle(title);
			rssEntry.setUrl(entryUrl);
			
			if(StringUtils.isNotBlank(pubDate)) {
				SimpleDateFormat formatter = new SimpleDateFormat("E, dd MMM yyyy HH:mm:ss Z");
				formatter.setTimeZone(TimeZone.getTimeZone("GMT"));
				formatter.setDateFormatSymbols(new DateFormatSymbols(Locale.ENGLISH));
				try {
					Date date = formatter.parse(pubDate);
					rssEntry.setDate(date);
				} catch(Exception e) {
					// unparsable date
					logger.error("Unable to parse date <" + pubDate + ">", e);
				}
			}
			
			rssEntries.add(rssEntry);
		}
		
		return rssEntries;
	}
	
	private List<Entry> getAtomEntries(Document doc) throws Exception {
		XPath xpath = XPathFactory.newInstance().newXPath();
		List<Entry> rssEntries = new LinkedList<Entry>();
		NodeList entries = (NodeList)xpath.evaluate("/feed/entry",
				doc,
				XPathConstants.NODESET);
		for(int i = 0; i < entries.getLength(); i++) {
			Entry rssEntry = new Entry();
			Element elmEntry = (Element)entries.item(i);
			String id = (String)xpath.evaluate("id", elmEntry, XPathConstants.STRING);
			String title = (String)xpath.evaluate("title", elmEntry, XPathConstants.STRING);
			String entryUrl = (String)xpath.evaluate("link/@href", elmEntry, XPathConstants.STRING);
			String updated = (String)xpath.evaluate("updated", elmEntry, XPathConstants.STRING);
			
			rssEntry.setId(id);
			rssEntry.setTitle(title);
			rssEntry.setUrl(entryUrl);
			
			if(StringUtils.length(updated) > 19) {
				try {
					Date date = null;
					if(updated.endsWith("Z")) {
						// No timezone
						SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
						formatter.setTimeZone(TimeZone.getTimeZone("GMT"));
						date = formatter.parse(updated.substring(0, 19));
					} else {
						String timezone = updated.substring(updated.length()-6);
						timezone = timezone.substring(0, 3) + timezone.substring(4);
						updated = updated.substring(0, 19) + timezone;
						SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");
						formatter.setTimeZone(TimeZone.getTimeZone("GMT"));
						date = formatter.parse(updated);
					}
					if(date != null) {
						rssEntry.setDate(date);
					}
				} catch(Exception e) {
					// unparsable date
					logger.error("Unable to parse date <" + updated + ">", e);
				}
			}
			
			rssEntries.add(rssEntry);
		}
		
		return rssEntries;
	}
	
	public String getTitle(String url) {
		try {
			return getTitle(getDocument(url));
		} catch(Exception e) {
			logger.error("Error while getting title for <" + url + ">", e);
		}
		return "";
	}
	
	private String getTitle(Document doc) throws XPathExpressionException {
		switch(getType(doc)) {
			case RSS :
				return getRSSTitle(doc);
			case ATOM :
				return getAtomTitle(doc);
		}
		return "";
	}
	
	private String getRSSTitle(Document doc) throws XPathExpressionException {
		XPath xpath = XPathFactory.newInstance().newXPath();
		return (String)xpath.evaluate("/rss/channel/title", doc, XPathConstants.STRING);
	}
	
	private String getAtomTitle(Document doc) throws XPathExpressionException {
		XPath xpath = XPathFactory.newInstance().newXPath();
		return (String)xpath.evaluate("/feed/title", doc, XPathConstants.STRING);
	}
	
	public String getLink(String url) {
		try {
			return getLink(getDocument(url));
		} catch(Exception e) {
			logger.error("Error while getting link for <" + url + ">", e);
		}
		return "";
	}
	
	private String getLink(Document doc) throws XPathExpressionException {
		switch(getType(doc)) {
			case RSS :
				return getRSSLink(doc);
			case ATOM :
				return getAtomLink(doc);
		}
		return "";
	}
	
	private String getRSSLink(Document doc) throws XPathExpressionException {
		XPath xpath = XPathFactory.newInstance().newXPath();
		return (String)xpath.evaluate("/rss/channel/link", doc, XPathConstants.STRING);
	}
	
	private String getAtomLink(Document doc) throws XPathExpressionException {
		XPath xpath = XPathFactory.newInstance().newXPath();
		return (String)xpath.evaluate("/feed/link[@rel='alternate']/@href", doc, XPathConstants.STRING);
	}
	
	public String getEntryContent(String url, String entryId) {
		try {
			return getEntryContent(getDocument(url), entryId);
		} catch(Exception e) {
			logger.error("Error while getting content for <" + url + "," + entryId + ">", e);
		}
		return "";
	}
	
	private String getEntryContent(Document doc, String entryId) throws XPathExpressionException {
		switch(getType(doc)) {
			case RSS :
				return getRSSEntryContent(doc, entryId);
			case ATOM :
				return getAtomEntryContent(doc, entryId);
		}
		return "";
	}
	
	private String getRSSEntryContent(Document doc, String entryId) throws XPathExpressionException {
		String content = "";
		XPath xpath = XPathFactory.newInstance().newXPath();
		NodeList entries = (NodeList)xpath.evaluate("/rss/channel/item", doc, XPathConstants.NODESET);
		for(int i = 0; i < entries.getLength(); i++) {
			Element elmEntry = (Element)entries.item(i);
			String id = (String)xpath.evaluate("link", elmEntry, XPathConstants.STRING);
			if(entryId.equals(id)) {
				String entryUrl = (String)xpath.evaluate("link", elmEntry, XPathConstants.STRING);
				content = (String)xpath.evaluate("description", elmEntry, XPathConstants.STRING);
				content += "<p><a href=\"" + entryUrl + "\" target=\"_blank\">Lire la suite</a></p>";
				break;
			}
		}
		return content;
	}
	
	private String getAtomEntryContent(Document doc, String entryId) throws XPathExpressionException {
		String content = "";
		XPath xpath = XPathFactory.newInstance().newXPath();
		NodeList entries = (NodeList)xpath.evaluate("/feed/entry", doc, XPathConstants.NODESET);
		for(int i = 0; i < entries.getLength(); i++) {
			Element elmEntry = (Element)entries.item(i);
			String id = (String)xpath.evaluate("id", elmEntry, XPathConstants.STRING);
			if(entryId.equals(id)) {
				content = (String)xpath.evaluate("content", elmEntry, XPathConstants.STRING);
				if(StringUtils.isBlank(content)) {
					String entryUrl = (String)xpath.evaluate("link/@href", elmEntry, XPathConstants.STRING);					
					content = (String)xpath.evaluate("summary", elmEntry, XPathConstants.STRING);
					content += "<p><a href=\"" + entryUrl + "\" target=\"_blank\">Lire la suite</a></p>";
				}
				break;
			}
		}
		return content;
	}
	
	private Type getType(Document doc) {
		return doc.getDocumentElement().getTagName().equals("feed") ? Type.ATOM : Type.RSS;
	}

}
