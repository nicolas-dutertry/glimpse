package org.glimpse.client.news;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("news-retriever")
public interface NewsRetrieverService extends RemoteService {
	NewsChannel getNewsChannel(String url);
	String getEntryContent(String url, String entryId);
}
