package org.glimpse.server;

public interface ConnectionManager {
	String connect(String login, String password) throws AuthenticationException;
	String getUserId(String connectionId);
	void disconnect(String connectionId);
}
