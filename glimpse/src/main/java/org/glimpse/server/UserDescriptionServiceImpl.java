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

import org.apache.commons.lang.StringUtils;
import org.glimpse.client.UserDescription;
import org.glimpse.client.UserDescriptionService;
import org.glimpse.client.UserPreferences;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

public class UserDescriptionServiceImpl extends RemoteServiceServlet implements
		UserDescriptionService {
	private static final long serialVersionUID = 1L;

	public UserDescription getUserDescription() {
		GlimpseManager glimpseManager = GlimpseManager.getInstance(getServletContext());
		UserManager userManager = glimpseManager.getUserManager();
		String userId = getUserId();
		if(userId == null) {
			return userManager.getDefaultUserDescription();
		} else {
			return userManager.getUserDescription(userId);
		}
	}

	public void setUserPreferences(UserPreferences userPreferences) {
		String userId = getUserId();
		if(userId != null) {
			GlimpseManager glimpseManager = GlimpseManager.getInstance(getServletContext());
			UserManager userManager = glimpseManager.getUserManager();			
			userManager.setUserPreferences(userId, userPreferences);
		}
	}
	
	private String getUserId() {
		String connectionId = GlimpseUtils.getConnectionId(getThreadLocalRequest());
		if(StringUtils.isNotEmpty(connectionId)) {
			GlimpseManager glimpseManager = GlimpseManager.getInstance(getServletContext());
			ConnectionManager connectionManager = glimpseManager.getConnectionManager();
			return connectionManager.getUserId(connectionId);
		} else {
			return null;
		}
	}

}
