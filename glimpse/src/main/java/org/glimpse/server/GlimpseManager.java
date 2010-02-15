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

import java.io.File;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import net.sf.ehcache.CacheManager;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.log4j.LogManager;
import org.apache.log4j.PropertyConfigurator;

public class GlimpseManager implements ServletContextListener {
	private static final Log logger = LogFactory.getLog(GlimpseManager.class);

	private Configuration configuration;
	private ConnectionManager connectionManager;
	private UserManager userManager;
	private CacheManager cacheManager;

	public void contextInitialized(ServletContextEvent sce) {
		try {
			ServletContext context = sce.getServletContext();			
			context.setAttribute("org.glimpse.manager", this);
			
			String confDir = System.getProperty("org.glimpse.conf.dir");
			if(StringUtils.isEmpty(confDir)) {
				confDir = context.getRealPath("/WEB-INF/conf");
			}
			
			File logConfig = new File(confDir, "log4j.properties");
			LogManager.resetConfiguration();
			PropertyConfigurator.configure(logConfig.getAbsolutePath());
			
			configuration = new PropertiesConfiguration(new File(confDir,
					"glimpse.properties"));			
			
			File usersDirectory = new File(configuration.getString(
					"users.directory",
					context.getRealPath("/WEB-INF/users")));
			
			if(logger.isDebugEnabled()) {
				logger.debug("user directory : " +
						usersDirectory.getAbsolutePath());
			}
			
			connectionManager = new SimpleConnectionManager(usersDirectory);
			userManager = new XmlUserManager(usersDirectory);
			cacheManager = CacheManager.create();
			
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
	
	public static GlimpseManager getInstance(ServletContext context) {
		return (GlimpseManager)context.getAttribute("org.glimpse.manager");
	}
	
	public Configuration getConfiguration() {
		return configuration;		
	}
	
	public ConnectionManager getConnectionManager() {
		return connectionManager;
	}
	
	public UserManager getUserManager() {
		return userManager;
	}
	
	public CacheManager getCacheManager() {
		return cacheManager;
	}
	
	public Proxy getProxy(String url) {
		String host = configuration.getString("proxy.host");
		int port = configuration.getInt("proxy.port", 8000);
		if(StringUtils.isBlank(host)) {
			return null;
		}
		return new Proxy(host, port);
	}
	
	public void contextDestroyed(ServletContextEvent sce) {
		// Nothing to do
	}

}
