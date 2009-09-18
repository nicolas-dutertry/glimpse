package org.glimpse.client;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("user-description")
public interface UserDescriptionService  extends RemoteService {
	UserDescription getUserDescription();
	void setUserDescription(UserDescription userDescription);
}
