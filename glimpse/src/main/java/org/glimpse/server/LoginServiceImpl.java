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
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.glimpse.client.LoginService;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

public class LoginServiceImpl extends RemoteServiceServlet  implements LoginService {
	private static final long serialVersionUID = 1L;
	private static final Log logger = LogFactory.getLog(LoginServiceImpl.class);

	public boolean connect(String login, String password, boolean remember) {
		
		ConnectionManager connectionManager =
			GlimpseManager.getInstance(getServletContext()).getConnectionManager();
		try {
			String connectionId = connectionManager.connect(login, password);
			GlimpseUtils.setConnectionId(getThreadLocalRequest(),
					getThreadLocalResponse(), connectionId, remember);
			if(StringUtils.isNotEmpty(connectionId)) {
				return true;
			}
		} catch (AuthenticationException e) {
			logger.error("Authentication failed", e);
		}
		return false;
	}

	public void disconnnect() {
		String connectionId = GlimpseUtils.getConnectionId(getThreadLocalRequest());
		if(StringUtils.isNotEmpty(connectionId)) {
			ConnectionManager connectionManager =
				GlimpseManager.getInstance(getServletContext()).getConnectionManager();
			connectionManager.disconnect(connectionId);
			GlimpseUtils.setConnectionId(getThreadLocalRequest(),
					getThreadLocalResponse(), "", false);
		}
	}

}
