package org.glimpse.server;

public class SimpleConnectionManager implements ConnectionManager {

	public String connect(String login, String password)
			throws AuthenticationException {
		return login;
	}

	public void disconnect(String connectionId) {
		
	}

	public String getUserId(String connectionId) {
		return connectionId;
	}

}
