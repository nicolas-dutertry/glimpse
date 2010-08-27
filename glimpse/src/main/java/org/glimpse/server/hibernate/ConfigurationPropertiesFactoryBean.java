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
package org.glimpse.server.hibernate;

import java.io.File;
import java.io.FileReader;
import java.util.Properties;

import org.apache.commons.io.IOUtils;
import org.glimpse.server.GlimpseManager;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Required;

public class ConfigurationPropertiesFactoryBean implements FactoryBean<Properties>, InitializingBean {

	private GlimpseManager glimpseManager;
	private String fileName;
	
	private Properties properties;
	
	public GlimpseManager getGlimpseManager() {
		return glimpseManager;
	}

	@Required
	public void setGlimpseManager(GlimpseManager glimpseManager) {
		this.glimpseManager = glimpseManager;
	}

	public String getFileName() {
		return fileName;
	}

	@Required
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	
	@Override
	public Properties getObject() throws Exception {
		return properties;
	}

	@Override
	public Class<?> getObjectType() {
		return Properties.class;
	}

	@Override
	public boolean isSingleton() {
		return true;
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		FileReader reader = null;
		try {
			properties = new Properties();
			File file = new File(glimpseManager.getConfigurationDirectory(), fileName);
			reader = new FileReader(file);
			properties.load(reader);
		} finally {
			IOUtils.closeQuietly(reader);
		}
	}

}
