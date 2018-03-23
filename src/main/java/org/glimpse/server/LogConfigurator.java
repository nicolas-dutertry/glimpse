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
