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
package org.glimpse.server.model;

import java.io.Serializable;
import java.util.List;


import org.glimpse.client.layout.ComponentDescription.Type;

public class ServerComponentDescription implements Serializable {
	private static final long serialVersionUID = 1L;
	
	private String type;
	
	private List<ServerComponentProperty> properties;
	
	public ServerComponentDescription() {
		type = Type.NEWS.toString();
	}
	
	public ServerComponentDescription(String type) {
		this.type = type;
	}
	
	public String getType() {
		return type;
	}
	
	public void setType(String type) {
		this.type = type;
	}
	
	public List<ServerComponentProperty> getProperties() {
		return properties;
	}
	
	public void setProperties(List<ServerComponentProperty> properties) {
		this.properties = properties;
	}
}
