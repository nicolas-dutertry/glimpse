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
package org.glimpse.client.layout;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class PageDescription implements Serializable {
	private static final long serialVersionUID = 1L;
	
	private List<TabDescription> tabDescriptions = new ArrayList<TabDescription>();
	
	
	public List<TabDescription> getTabDescriptions() {
		return new ArrayList<TabDescription>(tabDescriptions);
	}
	
	public void removeTabDescription(TabDescription tab) {
		tabDescriptions.remove(tab);
	}
	
	public void addTabDescription(TabDescription tab) {
		tabDescriptions.add(tab);
	}

}
