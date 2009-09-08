package org.glimpse.client;

import org.glimpse.client.layout.PageDescription;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("page-description")
public interface PageDescriptionService extends RemoteService {
	PageDescription getPageDescription();
	void setPageDescription(PageDescription pageDescription);
}
