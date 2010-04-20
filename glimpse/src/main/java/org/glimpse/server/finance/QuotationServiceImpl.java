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
package org.glimpse.server.finance;

import java.net.URLEncoder;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.glimpse.client.finance.Quotation;
import org.glimpse.client.finance.QuotationService;
import org.glimpse.server.GlimpseManager;
import org.glimpse.server.Proxy;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

public class QuotationServiceImpl extends RemoteServiceServlet implements QuotationService {
	private static final long serialVersionUID = 1L;
	private static Log logger = LogFactory.getLog(QuotationServiceImpl.class);

	Pattern pattern =
		Pattern.compile(
				"<div class=\"InfB\"><span class=\"gras\">([0-9\\s\\.]+).*</span>(&nbsp;)*<span\\s+class=\"gras\"><span class=\"VAR[a-z]*\">([-\\+]?[0-9\\s\\.]+)%</span>");
	
	public Quotation getQuotation(String code) {
		try {
			GlimpseManager glimpseManager = GlimpseManager.getInstance(getServletContext());
			Proxy proxy = glimpseManager.getProxy("http://www.boursorama.com/");
			
			HttpClient client = new HttpClient();		
			if(proxy != null) {
				client.getHostConfiguration().setProxy(proxy.getHost(), proxy.getPort());
			}
			
			String url = "http://www.boursorama.com/cours.phtml?symbole=" + URLEncoder.encode(code, "UTF-8");
			GetMethod method = new GetMethod(url);
			client.executeMethod(method);
			String response = method.getResponseBodyAsString();
			
			double value = 0;
			double variation = 0;
			Matcher matcher = pattern.matcher(response);
			if(matcher.find()) {
				String s = matcher.group(1);
				s = StringUtils.remove(s, " ");
				value = Double.parseDouble(s);
				
				s = matcher.group(3);
				s = StringUtils.remove(s, " ");
				variation = Double.parseDouble(s);
			} else {
				logger.debug("Unable to find quotation for <" + code + ">\n" + response);
			}
			
			return new Quotation(value, variation);
			
		} catch (Exception e) {
			logger.error("Error while getting quotation for <" + code + ">", e);
			return null;
		}
	}

}
