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
import org.glimpse.spring.web.RemoteServiceUtil;
import org.springframework.beans.factory.annotation.Required;

public class UserDescriptionServiceImpl implements UserDescriptionService {
	private static final long serialVersionUID = 1L;
	
	private UserManager userManager;
	
	public UserManager getUserManager() {
		return userManager;
	}

	@Required
	public void setUserManager(UserManager userManager) {
		this.userManager = userManager;
	}

	public UserDescription getUserDescription() {
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
			userManager.setUserPreferences(userId, userPreferences);
		}
	}
	
	private String getUserId() {
		String connectionId = GlimpseUtils.getConnectionId(RemoteServiceUtil.getThreadLocalRequest());
		if(StringUtils.isNotEmpty(connectionId)) {
			return userManager.getUserId(connectionId);
		} else {
			return null;
		}
	}

}
