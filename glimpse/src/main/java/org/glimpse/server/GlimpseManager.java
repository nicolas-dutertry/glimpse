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

import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.log4j.LogManager;
import org.apache.log4j.PropertyConfigurator;
import org.springframework.web.context.ServletContextAware;

public class GlimpseManager implements ServletContextAware {
	private static final Log logger = LogFactory.getLog(GlimpseManager.class);
	
	private File configurationDirectory;
	private Configuration configuration;
	
	public void setServletContext(ServletContext servletContext) {
		String confDirPath = System.getProperty("org.glimpse.conf.dir");
		if(StringUtils.isEmpty(confDirPath)) {
			confDirPath = servletContext.getRealPath("/WEB-INF/conf");
		}
		configurationDirectory = new File(confDirPath);
		
		File logConfig = new File(configurationDirectory, "log4j.properties");
		LogManager.resetConfiguration();
		PropertyConfigurator.configure(logConfig.getAbsolutePath());
		
		try {
			configuration = new PropertiesConfiguration(new File(configurationDirectory,
				"glimpse.properties"));
		} catch (ConfigurationException e) {
			logger.fatal("Unable to Load configuration", e);
			throw new RuntimeException(e);
		}
	}
	
	public Configuration getConfiguration() {
		return configuration;
	}
	
	public File getConfigurationDirectory() {
		return configurationDirectory;
	}

}
