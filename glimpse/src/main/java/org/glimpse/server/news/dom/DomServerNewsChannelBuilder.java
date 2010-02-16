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
package org.glimpse.server.news.dom;

import java.io.InputStream;
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

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.glimpse.client.news.Enclosure;
import org.glimpse.server.news.ServerEntry;
import org.glimpse.server.news.ServerNewsChannel;
import org.glimpse.server.news.ServerNewsChannelBuilder;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class DomServerNewsChannelBuilder implements ServerNewsChannelBuilder {
	private static Log logger = LogFactory.getLog(DomServerNewsChannelBuilder.class);

	public ServerNewsChannel buildChannel(InputStream is) throws Exception {
		Document doc = createDocument(is);
		switch(getType(doc)) {
			case RSS :
				return new ServerNewsChannel(getRSSLink(doc), getRSSTitle(doc), getRSSEntries(doc));
			case ATOM :
				return new ServerNewsChannel(getAtomLink(doc), getAtomTitle(doc), getAtomEntries(doc));
		}
		return null;
	}
	
	private Document createDocument(InputStream is) throws Exception {
		DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
		return builder.parse(is);
	}
	
	private List<ServerEntry> getRSSEntries(Document doc) throws Exception {
		XPath xpath = XPathFactory.newInstance().newXPath();
		List<ServerEntry> rssEntries = new LinkedList<ServerEntry>();
		NodeList entries = (NodeList)xpath.evaluate("/rss/channel/item",
				doc,
				XPathConstants.NODESET);
		for(int i = 0; i < entries.getLength(); i++) {
			ServerEntry rssEntry = new ServerEntry();
			Element elmEntry = (Element)entries.item(i);
			String id = (String)xpath.evaluate("link", elmEntry, XPathConstants.STRING);
			String title = (String)xpath.evaluate("title", elmEntry, XPathConstants.STRING);
			String entryUrl = (String)xpath.evaluate("link", elmEntry, XPathConstants.STRING);
			String pubDate = (String)xpath.evaluate("pubDate", elmEntry, XPathConstants.STRING);
			
			NodeList enclosureList = (NodeList)xpath.evaluate("enclosure", elmEntry, XPathConstants.NODESET);
			List<Enclosure> enclosures = new LinkedList<Enclosure>();
			for(int j = 0; j < enclosureList.getLength(); j++) {
				Element elmEnclosure = (Element)enclosureList.item(j);
				Enclosure enclosure = new Enclosure();
				enclosure.setUrl(elmEnclosure.getAttribute("url"));
				enclosure.setType(elmEnclosure.getAttribute("type"));
				enclosures.add(enclosure);
			}
			
			String content = (String)xpath.evaluate("description", elmEntry, XPathConstants.STRING);
			content += "<p><a href=\"" + entryUrl + "\" target=\"_blank\">Lire la suite</a></p>";
			
			if(StringUtils.isBlank(id)) {
				id = pubDate;
			}
			if(StringUtils.isBlank(entryUrl)) {
				entryUrl = getRSSLink(doc);
			}
			
			rssEntry.setId(id);
			rssEntry.setTitle(title);
			rssEntry.setUrl(entryUrl);
			rssEntry.setEnclosures(enclosures);
			rssEntry.setContent(content);
			
			if(StringUtils.isNotBlank(pubDate)) {
				SimpleDateFormat formatter = new SimpleDateFormat("E, dd MMM yyyy HH:mm:ss Z");
				formatter.setTimeZone(TimeZone.getTimeZone("GMT"));
				formatter.setDateFormatSymbols(new DateFormatSymbols(Locale.ENGLISH));
				try {
					Date date = formatter.parse(pubDate);
					rssEntry.setDate(date);
				} catch(Exception e) {
					// unparsable date
					logger.warn("Unable to parse date <" + pubDate + ">", e);
				}
			}
			
			if(StringUtils.isNotBlank(id) && StringUtils.isNotBlank(title) && StringUtils.isNotBlank(entryUrl)) {
				rssEntries.add(rssEntry);
			}
		}
		
		return rssEntries;
	}
	
	private List<ServerEntry> getAtomEntries(Document doc) throws Exception {
		XPath xpath = XPathFactory.newInstance().newXPath();
		List<ServerEntry> rssEntries = new LinkedList<ServerEntry>();
		NodeList entries = (NodeList)xpath.evaluate("/feed/entry",
				doc,
				XPathConstants.NODESET);
		for(int i = 0; i < entries.getLength(); i++) {
			ServerEntry rssEntry = new ServerEntry();
			Element elmEntry = (Element)entries.item(i);
			String id = (String)xpath.evaluate("id", elmEntry, XPathConstants.STRING);
			String title = (String)xpath.evaluate("title", elmEntry, XPathConstants.STRING);
			
			String entryUrl = null;
			List<Enclosure> enclosures = new LinkedList<Enclosure>();
			NodeList linkList = (NodeList)xpath.evaluate("link", elmEntry, XPathConstants.NODESET);
			if(linkList != null) {
				for(int j = 0; j < linkList.getLength(); j++) {
					Element elmLink = (Element)linkList.item(j);
					String rel = elmLink.getAttribute("rel"); 
					if(StringUtils.isBlank(rel) || rel.equals("alternate")) {
						entryUrl = elmLink.getAttribute("href");
					} else if(rel.equals("enclosure")) {
						Enclosure enclosure = new Enclosure();
						enclosure.setUrl(elmLink.getAttribute("href"));
						enclosure.setType(elmLink.getAttribute("type"));
						enclosures.add(enclosure);
					}
				}
			}
			String updated = (String)xpath.evaluate("updated", elmEntry, XPathConstants.STRING);
			
			String content = (String)xpath.evaluate("content", elmEntry, XPathConstants.STRING);
			if(StringUtils.isBlank(content)) {
				content = (String)xpath.evaluate("summary", elmEntry, XPathConstants.STRING);
				content += "<p><a href=\"" + entryUrl + "\" target=\"_blank\">Lire la suite</a></p>";
			}
			
			rssEntry.setId(id);
			rssEntry.setTitle(title);
			rssEntry.setUrl(entryUrl);
			rssEntry.setEnclosures(enclosures);
			rssEntry.setContent(content);
			
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
					logger.warn("Unable to parse date <" + updated + ">", e);
				}
			}
			
			if(StringUtils.isNotBlank(id) && StringUtils.isNotBlank(title) && StringUtils.isNotBlank(entryUrl)) {
				rssEntries.add(rssEntry);
			}
		}
		
		return rssEntries;
	}
	
	private String getRSSTitle(Document doc) throws XPathExpressionException {
		XPath xpath = XPathFactory.newInstance().newXPath();
		return (String)xpath.evaluate("/rss/channel/title", doc, XPathConstants.STRING);
	}
	
	private String getAtomTitle(Document doc) throws XPathExpressionException {
		XPath xpath = XPathFactory.newInstance().newXPath();
		return (String)xpath.evaluate("/feed/title", doc, XPathConstants.STRING);
	}
	
	private String getRSSLink(Document doc) throws XPathExpressionException {
		XPath xpath = XPathFactory.newInstance().newXPath();
		return (String)xpath.evaluate("/rss/channel/link", doc, XPathConstants.STRING);
	}
	
	private String getAtomLink(Document doc) throws XPathExpressionException {
		XPath xpath = XPathFactory.newInstance().newXPath();
		return (String)xpath.evaluate("/feed/link[@rel='alternate']/@href", doc, XPathConstants.STRING);
	}
	
	private ServerNewsChannel.Type getType(Document doc) {
		return doc.getDocumentElement().getTagName().equals("feed") ? ServerNewsChannel.Type.ATOM : ServerNewsChannel.Type.RSS;
	}

}
