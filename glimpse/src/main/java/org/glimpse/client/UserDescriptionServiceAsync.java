package org.glimpse.client;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface UserDescriptionServiceAsync {
	void getUserDescription(AsyncCallback<UserDescription> callback);
	void setUserDescription(UserDescription userDescription,
			AsyncCallback<Void> callback);
}
