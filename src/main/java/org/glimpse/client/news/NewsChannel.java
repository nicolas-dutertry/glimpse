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
package org.glimpse.client.news;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

public class NewsChannel implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private String url;
	private String title;
	private List<Entry> entries;
	
	public NewsChannel() {		
	}
	
	public NewsChannel(String url, String title, List<Entry> entries) {
		this.url = url;
		this.title = title;
		this.entries = entries;
	}

	public String getUrl() {
		return url;
	}

	public String getTitle() {
		return title;
	}

	public List<Entry> getEntries() {
		return new LinkedList<Entry>(entries);
	}
	
	

}
