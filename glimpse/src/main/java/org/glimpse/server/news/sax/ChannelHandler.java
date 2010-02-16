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
package org.glimpse.server.news.sax;

import java.text.DateFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.glimpse.client.news.Enclosure;
import org.glimpse.server.news.ServerEntry;
import org.glimpse.server.news.ServerNewsChannel;
import org.glimpse.server.news.ServerNewsChannel.Type;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

class ChannelHandler extends DefaultHandler {
	private static Log logger = LogFactory.getLog(ChannelHandler.class);
	
	private Type type;
	private String url;
	private String title;
	private List<ServerEntry> entries;
	
	private List<String> elementsStack;
	private StringBuffer buffer;
	private ServerEntry currentEntry;
	private String currentContent;
	private String currentSummary;
	private Enclosure currentEnclosure;
	
	public ServerNewsChannel getChannel() {
		return new ServerNewsChannel(url, title, entries);
	}

	@Override
	public void startDocument() throws SAXException {
		entries = new LinkedList<ServerEntry>();
		elementsStack = new LinkedList<String>();		
	}

	@Override
	public void startElement(String uri,
            String localName,
            String qName,
            Attributes attributes)
			throws SAXException {
		elementsStack.add(qName);
		buffer = new StringBuffer();
		
		if(type == null) {
			type = qName.equals("feed") ? Type.ATOM : Type.RSS;
		} else if(type.equals(Type.ATOM)){
			String path = getCurrentPath();
			if(path.equals("/feed/link")) {
				if("alternate".equals(attributes.getValue("rel"))) {
					url = attributes.getValue("href");
				}
			} else if(path.equals("/feed/entry")) {
				currentEntry = new ServerEntry();
			} else if(path.equals("/feed/entry/link")) {
				String rel = attributes.getValue("rel"); 
				if(StringUtils.isBlank(rel) || rel.equals("alternate")) {
					currentEntry.setUrl(attributes.getValue("href"));
				} else if(rel.equals("enclosure")) {
					Enclosure enclosure = new Enclosure();
					enclosure.setUrl(attributes.getValue("href"));
					enclosure.setType(attributes.getValue("type"));
					currentEntry.addEnclosure(enclosure);
				}
			} 
		} else if(type.equals(Type.RSS)){
			String path = getCurrentPath();
			if(path.equals("/rss/channel/item")) {
				currentEntry = new ServerEntry();
			} else if(path.equals("/rss/channel/item/enclosure")) {
				currentEnclosure = new Enclosure();
				currentEnclosure.setUrl(attributes.getValue("url"));
				currentEnclosure.setType(attributes.getValue("type"));
				currentEntry.addEnclosure(currentEnclosure);
			}
		}
	}

	@Override
	public void endElement(String uri,
            String localName,
            String qName) throws SAXException {
		if(Type.ATOM.equals(type)) {
			String path = getCurrentPath();
			if(path.equals("/feed/title")) {
				title = buffer.toString();
			} else if(path.equals("/feed/entry")) {
				String content = currentContent;
				if(StringUtils.isBlank(content)) {
					content = currentSummary == null ? "" : currentSummary;
					content += "<p><a href=\"" + currentEntry.getUrl() + "\" target=\"_blank\">Lire la suite</a></p>";
				}
				currentEntry.setContent(content);
	
				if(StringUtils.isNotBlank(currentEntry.getId()) &&
						StringUtils.isNotBlank(currentEntry.getTitle()) &&
						StringUtils.isNotBlank(currentEntry.getUrl())) {
					entries.add(currentEntry);
				}
				
				currentEntry = null;
				currentContent = null;
				currentSummary = null;			
			} else if(path.equals("/feed/entry/id")) {
				currentEntry.setId(buffer.toString());
			}  else if(path.equals("/feed/entry/title")) {
				currentEntry.setTitle(buffer.toString());
			} else if(path.equals("/feed/entry/updated")) {
				String updated = buffer.toString();
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
							currentEntry.setDate(date);
						}
					} catch(Exception e) {
						// unparsable date
						logger.warn("Unable to parse date <" + updated + ">", e);
					}
				}
			} else if(path.equals("/feed/entry/content")) {
				currentContent = buffer.toString();
			}  else if(path.equals("/feed/entry/summary")) {
				currentSummary = buffer.toString();
			}
		} else if(Type.RSS.equals(type)) {
			String path = getCurrentPath();
			if(path.equals("/rss/channel/title")) {
				title = buffer.toString();
			} else if(path.equals("/rss/channel/link")) {
				url = buffer.toString();
			} else if(path.equals("/rss/channel/item")) {
				String content = currentSummary == null ? "" : currentSummary;
				content += "<p><a href=\"" + currentEntry.getUrl() + "\" target=\"_blank\">Lire la suite</a></p>";
				currentEntry.setContent(content);
				
				if(StringUtils.isBlank(currentEntry.getId()) && currentEntry.getDate() != null) {
					currentEntry.setId(currentEntry.getDate().toString());
				}
				
				if(StringUtils.isBlank(currentEntry.getUrl())) {
					currentEntry.setUrl(url);
				}
				
				if(StringUtils.isNotBlank(currentEntry.getId()) &&
						StringUtils.isNotBlank(currentEntry.getTitle()) &&
						StringUtils.isNotBlank(currentEntry.getUrl())) {
					entries.add(currentEntry);
				}
				
				currentEntry = null;
				currentContent = null;
				currentSummary = null;			
			} else if(path.equals("/rss/channel/item/link")) {
				currentEntry.setId(buffer.toString());
				currentEntry.setUrl(buffer.toString());
			}  else if(path.equals("/rss/channel/item/title")) {
				currentEntry.setTitle(buffer.toString());
			} else if(path.equals("/rss/channel/item/pubDate")) {
				String pubDate = buffer.toString();
				if(StringUtils.isNotBlank(pubDate)) {
					SimpleDateFormat formatter = new SimpleDateFormat("E, dd MMM yyyy HH:mm:ss Z");
					formatter.setTimeZone(TimeZone.getTimeZone("GMT"));
					formatter.setDateFormatSymbols(new DateFormatSymbols(Locale.ENGLISH));
					try {
						Date date = formatter.parse(pubDate);
						currentEntry.setDate(date);
					} catch(Exception e) {
						// unparsable date
						logger.warn("Unable to parse date <" + pubDate + ">", e);
					}
				}
			} else if(path.equals("/rss/channel/item/description")) {
				currentSummary = buffer.toString();
			}  else if(path.equals("/rss/channel/item/enclosure/url")) {
				currentEnclosure.setUrl(buffer.toString());
			}  else if(path.equals("/rss/channel/item/enclosure/type")) {
				currentEnclosure.setType(buffer.toString());
			}
		}
		
		String s = elementsStack.remove(elementsStack.size()-1);
		assert s.equals(qName);
	}
	
	@Override
	public void characters(char[] ch,
            int start,
            int length)
			throws SAXException {
		buffer.append(ch, start, length);
	}

	private String getCurrentPath() {
		StringBuilder builder = new StringBuilder();
		for (String element : elementsStack) {
			builder.append("/").append(element);
		}
		return builder.toString();
	}
	
}
