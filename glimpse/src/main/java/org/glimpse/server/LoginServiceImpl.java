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
			GlimpseUtils.setConnectionId(getThreadLocalResponse(), connectionId, remember);
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
			GlimpseUtils.setConnectionId(getThreadLocalResponse(), "", false);
		}
	}

}
