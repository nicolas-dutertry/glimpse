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
import org.apache.log4j.LogManager;
import org.apache.log4j.PropertyConfigurator;

public class GlimpseManager implements ServletContextListener {
	private static final Log logger = LogFactory.getLog(GlimpseManager.class);

	private Configuration configuration;
	private ConnectionManager connectionManager;
	private UserManager userManager;

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
