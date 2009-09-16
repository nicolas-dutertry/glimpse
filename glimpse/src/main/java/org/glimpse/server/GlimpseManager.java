package org.glimpse.server;

import java.io.File;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.PropertyConfigurator;

public class GlimpseManager implements ServletContextListener {
	private Configuration configuration;
	private ConnectionManager connectionManager;

	public void contextInitialized(ServletContextEvent sce) {
		try {
			ServletContext context = sce.getServletContext();			
			context.setAttribute("org.glimpse.manager", this);
			
			String confDir = System.getProperty("org.glimpse.conf.dir");
			if(StringUtils.isEmpty(confDir)) {
				confDir = context.getRealPath("/WEB-INF/conf");
			}
			
			File logConfig = new File(confDir, "log4j.properties");
			PropertyConfigurator.configure(logConfig.getAbsolutePath());
			
			configuration = new PropertiesConfiguration(new File(confDir,
					"glimpse.properties"));
			connectionManager = new SimpleConnectionManager();
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
