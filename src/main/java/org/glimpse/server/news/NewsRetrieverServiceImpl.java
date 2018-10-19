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
package org.glimpse.server.news;

import java.io.InputStream;
import java.util.LinkedList;
import java.util.List;

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.glimpse.client.news.Entry;
import org.glimpse.client.news.NewsChannel;
import org.glimpse.client.news.NewsRetrieverService;
import org.glimpse.server.news.sax.SaxServerNewsChannelBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class NewsRetrieverServiceImpl implements NewsRetrieverService {
	private static Log logger = LogFactory.getLog(NewsRetrieverServiceImpl.class);
	private static final ServerNewsChannelBuilder channelBuilder =
		new SaxServerNewsChannelBuilder();
	
    @Autowired
	private CloseableHttpClient client;
    
    @Autowired
	private CacheManager cacheManager;

	public NewsChannel getNewsChannel(String url, boolean refresh) {
		try {
			ServerNewsChannel serverChannel = getServerNewChannel(url, refresh);
			List<ServerEntry> serverEntries = serverChannel.getEntries();
			List<Entry> entries = new LinkedList<Entry>();
			for (ServerEntry serverEntry : serverEntries) {
				Entry entry = new Entry();
				entry.setId(serverEntry.getId());
				entry.setDate(serverEntry.getDate());
				entry.setTitle(serverEntry.getTitle());
				entry.setUrl(serverEntry.getUrl());
				entry.setEnclosures(serverEntry.getEnclosures());
				entries.add(entry);
			}
			
			return new NewsChannel(serverChannel.getUrl(), serverChannel.getTitle(),
							entries);
		} catch(Exception e) {
			logger.error("Error while getting Entries for <" + url + ">", e);
		}

		return null;
	}
	
	public String getEntryContent(String url, String entryId) {
		try {
			ServerNewsChannel serverChannel = getServerNewChannel(url, false);
			return serverChannel.getEntry(entryId).getContent();
		} catch(Exception e) {
			logger.error("Error while getting content for <" + url + "," + entryId + ">", e);
		}
		return "";
	}
	
	private ServerNewsChannel getServerNewChannel(String url, boolean refresh) throws Exception {
		ServerNewsChannel channel = null;
		
		Cache cache = cacheManager.getCache("newsCache");
		
		if(!refresh) {
			net.sf.ehcache.Element element = cache.get(url);
			if(element != null) {
				channel = (ServerNewsChannel)element.getValue();
			}
		}
		
		if(channel == null) {
            HttpGet method = new HttpGet(url);
            try(CloseableHttpResponse httpresponse = client.execute(method)) {
                channel = channelBuilder.buildChannel(httpresponse.getEntity().getContent());
            }
			
			if(channel != null) {
				cache.put(new net.sf.ehcache.Element(url, channel));
			}
		}
		
		return channel;
	}

}
