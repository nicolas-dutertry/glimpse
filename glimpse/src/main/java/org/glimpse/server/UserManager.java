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
package org.glimpse.server;

import org.glimpse.client.UserDescription;
import org.glimpse.client.UserPreferences;
import org.glimpse.client.layout.PageDescription;

public interface UserManager {
	UserDescription getUserDescription(String userId);
	UserDescription getDefaultUserDescription();
	
	void setUserPreferences(String userId, UserPreferences userPreferences);	
	
	PageDescription getUserPageDescription(String localeName, String userId);
	void setUserPageDescription(String userId, PageDescription pageDescription);
	PageDescription getDefaultPageDescription(String localeName);
	void setDefaultPageDescription(String localeName, PageDescription pageDescription);
}
