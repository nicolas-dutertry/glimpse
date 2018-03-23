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
import org.glimpse.client.PageDescriptionService;
import org.glimpse.client.UserAttributes;
import org.glimpse.client.layout.PageDescription;
import org.glimpse.server.manager.UserManager;
import org.glimpse.spring.web.RemoteServiceUtil;
import org.springframework.beans.factory.annotation.Required;

public class PageDescriptionServiceImpl implements PageDescriptionService {	
	private UserManager userManager;
	
	public UserManager getUserManager() {
		return userManager;
	}

	@Required
	public void setUserManager(UserManager userManager) {
		this.userManager = userManager;
	}

	public PageDescription getDefaultPageDescription(String localeName) {
		return userManager.getDefaultPageDescription(localeName);
	}
	
	public PageDescription getPageDescription(String localeName) {
		String userId = getUserId();
		if(userId == null) {
			return userManager.getDefaultPageDescription(localeName);
		} else {
			return userManager.getUserPageDescription(localeName, userId);
		}
	}
	
	public void setDefaultPageDescription(String localeName, PageDescription pageDescription) {
		String userId = getUserId();
		if(userId != null) {
			UserAttributes userAttributes = userManager.getUserAttributes(userId);
			if(userAttributes != null && userAttributes.isAdministrator()) {
				userManager.setDefaultPageDescription(localeName, pageDescription);
			}
		}
	}
	
	public void setPageDescription(PageDescription pageDescription) {
		String userId = getUserId();
		if(userId != null) {
			userManager.setUserPageDescription(userId, pageDescription);
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
