/*
 * Copyright (C) 2009 Nicolas Dutertry
 * 
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
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
