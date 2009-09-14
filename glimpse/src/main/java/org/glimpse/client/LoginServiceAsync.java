package org.glimpse.client;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface LoginServiceAsync {
	void connect(String login, String password, boolean remember, AsyncCallback<Boolean> callback);
	void disconnnect(AsyncCallback<Void> callback);
}
