package org.glimpse.server;

import java.io.File;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.PropertyConfigurator;

public class LogConfigurator implements ServletContextListener {

	@Override
	public void contextInitialized(ServletContextEvent sce) {
		ServletContext servletContext = sce.getServletContext();
		
		String confDirPath = System.getProperty("org.glimpse.conf.dir");
		if(StringUtils.isEmpty(confDirPath)) {
			confDirPath = servletContext.getRealPath("/WEB-INF/conf");
		}
		File configurationDirectory = new File(confDirPath);
		
		File logConfig = new File(configurationDirectory, "log4j.properties");
		
		if(logConfig.exists()) {
			LogManager.resetConfiguration();
			PropertyConfigurator.configure(logConfig.getAbsolutePath());
		}
	}

	@Override
	public void contextDestroyed(ServletContextEvent sce) {
		// Nothing to do
	}

}
