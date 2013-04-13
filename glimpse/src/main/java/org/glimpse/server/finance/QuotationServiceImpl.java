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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.params.ConnRoutePNames;
import org.apache.http.impl.client.DecompressingHttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.glimpse.client.finance.Quotation;
import org.glimpse.client.finance.QuotationService;
import org.glimpse.server.Proxy;
import org.glimpse.server.ProxyProvider;
import org.springframework.beans.factory.annotation.Required;

public class QuotationServiceImpl implements QuotationService {
	private static Log logger = LogFactory.getLog(QuotationServiceImpl.class);

	private ProxyProvider proxyProvider;
	private QuotationFinder quotationFinder;
	
	public Quotation getQuotation(String code) {
		try {
			Proxy proxy = proxyProvider.getProxy("http://www.boursorama.com/");
			
			HttpClient client = new DecompressingHttpClient(new DefaultHttpClient());
			if(proxy != null) {
				client.getParams().setParameter(ConnRoutePNames.DEFAULT_PROXY,
						new HttpHost(proxy.getHost(), proxy.getPort()));
			}
			
			String url = "http://www.boursorama.com/cours.phtml?symbole=" + URLEncoder.encode(code, "UTF-8");
			HttpGet method = new HttpGet(url);
			HttpResponse httpresponse = client.execute(method);
			String response = EntityUtils.toString(httpresponse.getEntity());
			
			Quotation quotation = quotationFinder.getQuotation(response);
			if(quotation == null) {
				logger.debug("Unable to find quotation for <" + code + ">\n" + response);
				quotation = new Quotation();
			}
			
			return quotation;
			
		} catch (Exception e) {
			logger.error("Error while getting quotation for <" + code + ">", e);
			return null;
		}
	}

	@Required
	public void setProxyProvider(ProxyProvider proxyProvider) {
		this.proxyProvider = proxyProvider;
	}

	public ProxyProvider getProxyProvider() {
		return proxyProvider;
	}

	@Required
	public void setQuotationFinder(QuotationFinder quotationFinder) {
		this.quotationFinder = quotationFinder;
	}

	public QuotationFinder getQuotationFinder() {
		return quotationFinder;
	}

}
