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

import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.springframework.web.context.support.WebApplicationContextUtils;

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
		
		CloseableHttpClient client =  WebApplicationContextUtils.getWebApplicationContext(getServletContext())
            .getBean(CloseableHttpClient.class);
		
		HttpGet method = new HttpGet(url);
		CloseableHttpResponse httpresponse = null;
		try {			
			httpresponse = client.execute(method);
		} catch (Exception e) {
			logger.error("Unable to get new icon for <" + url + ">", e);
		}
		
        try {
            if(httpresponse != null && httpresponse.getStatusLine().getStatusCode() == 200) {
                IOUtils.copy(httpresponse.getEntity().getContent(), response.getOutputStream());
            } else {
                File f = new File(getServletContext().getRealPath("images/feed.png"));
                FileInputStream fis = new FileInputStream(f);
                IOUtils.copy(fis, response.getOutputStream());
                fis.close();
            }
        } finally {
            if(httpresponse != null ) {
                httpresponse.close();
            }
        }
	}
	
	

}
