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
package org.glimpse.server;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.lang.StringUtils;

public class ConfiguredProxyProvider implements ProxyProvider {
	private Proxy proxy;
	
	public ConfiguredProxyProvider(Configuration configuration) {
		String host = configuration.getString("proxy.host");
		int port = configuration.getInt("proxy.port", 8000);
		if(StringUtils.isBlank(host)) {
			proxy = null;
		} else {
			proxy = new Proxy(host, port);
		}
	}

	public Proxy getProxy(String url) {
		return proxy;
	}

}
