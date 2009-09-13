package org.glimpse.server;

import java.io.File;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class GlimpseManager implements ServletContextListener {
	private static final Log logger = LogFactory.getLog(GlimpseManager.class);
	
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
			configuration = new PropertiesConfiguration(new File(confDir,
					"glimpse.properties"));
			connectionManager = new SimpleConnectionManager();
		} catch (Exception e) {
			logger.fatal("Error", e);
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
	
	public void contextDestroyed(ServletContextEvent sce) {
		// Nothing to do
	}

}
