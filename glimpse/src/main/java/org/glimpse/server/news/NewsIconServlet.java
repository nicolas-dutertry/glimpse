package org.glimpse.server.news;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.glimpse.server.GlimpseManager;
import org.glimpse.server.Proxy;

public class NewsIconServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;
	private static final Log logger = LogFactory.getLog(NewsIconServlet.class);

	@Override
	protected void service(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		String url = request.getParameter("url");
		int i = url.indexOf(':');
		i = url.indexOf('/', i+3);
		if(i == -1) {
			i = url.length();
		}
		url = url.substring(0, i) + "/favicon.ico";
		
		HttpClient client = new HttpClient();
		
		GlimpseManager glimpseManager = GlimpseManager.getInstance(getServletContext());
		Proxy proxy = glimpseManager.getProxy(url);
		if(proxy != null) {
			client.getHostConfiguration().setProxy(proxy.getHost(), proxy.getPort());
		}
		
		GetMethod method = new GetMethod(url);
		int responseCode = -1;
		try {			
			responseCode = client.executeMethod(method);
		} catch (Exception e) {
			logger.error("Unable to get new icon for <" + url + ">", e);
		}
		
		if(responseCode == 200) {
			IOUtils.copy(method.getResponseBodyAsStream(), response.getOutputStream());
		} else {
			File f = new File(getServletContext().getRealPath("images/feed.png"));
			FileInputStream fis = new FileInputStream(f);
			IOUtils.copy(fis, response.getOutputStream());
			fis.close();
		}
	}
	
	

}
