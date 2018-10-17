/*
 * © 1996-2014 Sopra HR Software. All rights reserved
 */
package org.glimpse.server;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.lang.StringUtils;
import org.apache.http.HttpException;
import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;
import org.apache.http.impl.conn.DefaultRoutePlanner;
import org.apache.http.impl.conn.DefaultSchemePortResolver;
import org.apache.http.protocol.HttpContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 *
 * @author ndutertry
 */
@Component
public class StaticProxyRoutePlanner extends DefaultRoutePlanner {
    
    private final HttpHost proxyHost;
    
    public StaticProxyRoutePlanner(@Autowired Configuration configuration) {
        super(DefaultSchemePortResolver.INSTANCE);
        
        String host = configuration.getString("proxy.host");
		int port = configuration.getInt("proxy.port", 8000);
		if(StringUtils.isBlank(host)) {
			proxyHost = null;
		} else {
			proxyHost = new HttpHost(host, port);
		}
    }

    @Override
    protected HttpHost determineProxy(HttpHost target, HttpRequest request, HttpContext context) throws HttpException {
        return proxyHost;
    }
    
}
