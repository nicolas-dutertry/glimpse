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
package org.glimpse.server.manager.orm;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import org.glimpse.client.layout.ColumnDescription;
import org.glimpse.client.layout.ComponentDescription;
import org.glimpse.client.layout.PageDescription;
import org.glimpse.client.layout.TabDescription;
import org.glimpse.client.layout.ComponentDescription.Type;
import org.glimpse.server.model.ServerColumnDescription;
import org.glimpse.server.model.ServerComponentDescription;
import org.glimpse.server.model.ServerComponentProperty;
import org.glimpse.server.model.ServerTabDescription;
import org.glimpse.server.model.User;

public class OrmUserManagerUtils {
	public static PageDescription buildPage(List<ServerTabDescription> serverTabDescriptions) {
		PageDescription pageDescription = new PageDescription();
		for (ServerTabDescription serverTabDescription : serverTabDescriptions) {
			TabDescription tabDescription = new TabDescription();
			pageDescription.addTabDescription(tabDescription);
			tabDescription.setTitle(serverTabDescription.getTitle());
			List<ServerColumnDescription> serverColumnDescriptions = serverTabDescription.getColumnDescriptions();
			for (ServerColumnDescription serverColumnDescription : serverColumnDescriptions) {
				ColumnDescription columnDescription = new ColumnDescription();
				tabDescription.addColumnDescription(columnDescription);
				List<ServerComponentDescription> serverComponentDescriptions = serverColumnDescription.getComponentDescriptions();
				for (ServerComponentDescription serverComponentDescription : serverComponentDescriptions) {
					ComponentDescription componentDescription = new ComponentDescription(
							Type.valueOf(serverComponentDescription.getType()));
					columnDescription.addComponentDescription(componentDescription);
					Map<String, String> properties = new HashMap<String, String>();					
					List<ServerComponentProperty> serverComponentProperties = serverComponentDescription.getProperties();
					for (ServerComponentProperty serverComponentProperty : serverComponentProperties) {
						properties.put(serverComponentProperty.getPropertyName(),
								serverComponentProperty.getPropertyValue());
					}
					componentDescription.setProperties(properties);
				}
			}
		}
		
		return pageDescription;
	}
	
	public static List<ServerTabDescription> buildServerTabDescriptions(User user, PageDescription pageDescription) {
		List<ServerTabDescription> serverTabDescriptions = new LinkedList<ServerTabDescription>();
		
		List<TabDescription> tabDescriptions = pageDescription.getTabDescriptions();
		for (TabDescription tabDescription : tabDescriptions) {
			ServerTabDescription serverTabDescription = new ServerTabDescription();
			serverTabDescriptions.add(serverTabDescription);
			serverTabDescription.setUser(user);
			serverTabDescription.setTitle(tabDescription.getTitle());
			
			List<ServerColumnDescription> serverColumnDescriptions =
				new LinkedList<ServerColumnDescription>();
			List<ColumnDescription> columnDescriptions = tabDescription.getColumnDescriptions();
			for (ColumnDescription columnDescription : columnDescriptions) {
				ServerColumnDescription serverColumnDescription =
					new ServerColumnDescription();
				serverColumnDescriptions.add(serverColumnDescription);
				serverColumnDescription.setTabDescription(serverTabDescription);
				
				List<ServerComponentDescription> serverComponentDescriptions =
					new LinkedList<ServerComponentDescription>();
				List<ComponentDescription> componentDescriptions =
					columnDescription.getComponentDescriptions();
				for (ComponentDescription componentDescription : componentDescriptions) {
					ServerComponentDescription serverComponentDescription =
						new ServerComponentDescription(
							componentDescription.getType().toString());
					serverComponentDescriptions.add(serverComponentDescription);
					serverComponentDescription.setColumnDescription(serverColumnDescription);
					
					List<ServerComponentProperty> serverComponentProperties =
						new LinkedList<ServerComponentProperty>();					
					Map<String, String> properties = componentDescription.getProperties();
					Set<Entry<String, String>> entries = properties.entrySet();
					for (Entry<String, String> entry : entries) {
						ServerComponentProperty serverComponentProperty = new ServerComponentProperty();
						serverComponentProperty.setComponentDescription(serverComponentDescription);
						serverComponentProperty.setPropertyName(entry.getKey());
						serverComponentProperty.setPropertyValue(entry.getValue());
						serverComponentProperties.add(serverComponentProperty);						
					}
					serverComponentDescription.setProperties(serverComponentProperties);
				}
				serverColumnDescription.setComponentDescriptions(serverComponentDescriptions);
			}
			serverTabDescription.setColumnDescriptions(serverColumnDescriptions);
		}
		
		return serverTabDescriptions;
	}
}
