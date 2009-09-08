package org.glimpse.client.news;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface NewsRetrieverServiceAsync {	
	void getNewsChannel(String url, AsyncCallback<NewsChannel> callback);
	void getEntryContent(String url, String entryId, AsyncCallback<String> callback);
}
