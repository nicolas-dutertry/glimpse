package org.glimpse.client;

import org.glimpse.client.layout.PageDescription;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface PageDescriptionServiceAsync {
	void getPageDescription(AsyncCallback<PageDescription> callback);
	void setPageDescription(PageDescription pageDescription,
			AsyncCallback<Void> callback);
}
