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
package org.glimpse.client;

import java.io.Serializable;

public class UserAttributes implements Serializable {
	private static final long serialVersionUID = 1L;
	
	private boolean administrator;
	private UserPreferences preferences;
	
	public UserAttributes() {
		administrator = false;
		preferences = new UserPreferences();
	}	

	public void setAdministrator(boolean administrator) {
		this.administrator = administrator;
	}
	
	public boolean isAdministrator() {
		return administrator;
	}

	public void setPreferences(UserPreferences preferences) {
		this.preferences = preferences;
	}

	public UserPreferences getPreferences() {
		return preferences;
	}

}
