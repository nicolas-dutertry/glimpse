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
package org.glimpse.server.manager.orm;

import java.io.InputStream;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.Validate;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.glimpse.client.UserAttributes;
import org.glimpse.client.UserDescription;
import org.glimpse.client.UserPreferences;
import org.glimpse.client.layout.PageDescription;
import org.glimpse.server.dao.ConnectionDao;
import org.glimpse.server.dao.UserDao;
import org.glimpse.server.manager.AuthenticationException;
import org.glimpse.server.manager.DuplicateUserIdException;
import org.glimpse.server.manager.InvalidPasswordException;
import org.glimpse.server.manager.InvalidUserIdException;
import org.glimpse.server.manager.UserManager;
import org.glimpse.server.manager.xml.XmlUserManagerUtils;
import org.glimpse.server.model.Connection;
import org.glimpse.server.model.ServerTabDescription;
import org.glimpse.server.model.User;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.w3c.dom.Document;

@Transactional(readOnly=true, propagation=Propagation.REQUIRED)
public class OrmUserManager implements UserManager {
	private static final Log logger = LogFactory.getLog(OrmUserManager.class);
	private static final int CONNECTION_ID_LENGTH = 64;
	private static final int USER_ID_MIN_LENGTH = 2;
	private static final int USER_ID_MAX_LENGTH = 64;
	private static final int PASSWORD_MIN_LENGTH = 5;
	private static final int PASSWORD_MAX_LENGTH = 64;
	
	private final UserDao userDao;
	private final ConnectionDao connectionDao;
	
	public OrmUserManager(UserDao userDao, ConnectionDao connectionDao) {
		this.userDao = userDao;
		this.connectionDao = connectionDao;
	}
	
	private String generateConnectionId() {
		return RandomStringUtils.randomAlphanumeric(CONNECTION_ID_LENGTH);
	}
	
	private boolean isValidConnectionId(String connectionId) {
		if(connectionId == null) {
			return false;
		}
		
		if(connectionId.length() != CONNECTION_ID_LENGTH) {
			return false;
		}
		
		for(int i = 0; i < connectionId.length(); i++) {
			if(!Character.isLetterOrDigit(connectionId.charAt(i))) {
				return false;
			}
		}
		
		return true;
	}
	
	private boolean isValidUserId(String userId) {
		if(userId == null) {
			return false;
		}
		
		if(userId.length() < USER_ID_MIN_LENGTH || userId.length() > USER_ID_MAX_LENGTH) {
			return false;
		}
		
		return StringUtils.containsNone(userId, "'<>");
	}
	
	private boolean isValidPassword(String password) {
		if(password == null) {
			return false;
		}
		
		if(password.length() < PASSWORD_MIN_LENGTH || password.length() > PASSWORD_MAX_LENGTH) {
			return false;
		}
		
		return StringUtils.containsNone(password, "'");
	}
	
	private boolean isValidLogin(String login) {
		return isValidUserId(login);
	}
	
	@Transactional (readOnly=false)
	public String connect(String login, String password)
			throws AuthenticationException {
		checkPassword(login, password);
		User user = userDao.getUser(login);
		String connectionId = generateConnectionId();
		connectionDao.createConnection(connectionId, user);
		
		return connectionId;
	}
	
	public void checkPassword(String login, String password)
			throws AuthenticationException {
		if(!isValidLogin(login) || !isValidPassword(password) ||
				!userDao.checkPassword(login, password)) {
			throw new AuthenticationException("Unable to authenticate " + login);
		}
	}

	@Transactional (readOnly=false)
	public void disconnect(String connectionId) {
		Validate.isTrue(isValidConnectionId(connectionId));
		
		connectionDao.deleteConnection(connectionId);
	}

	public String getUserId(String connectionId) {
		Validate.isTrue(isValidConnectionId(connectionId));
		
		Connection connection = connectionDao.getConnection(connectionId);
		if(connection != null) {
			return connection.getUser().getId();
		} else {
			return null;
		}
	}
	

	@Transactional (readOnly=false)
	public void createUser(String userId, String password) throws InvalidUserIdException,
			InvalidPasswordException, DuplicateUserIdException {
		if(!isValidUserId(userId)) {
			throw new InvalidUserIdException();
		}
		
		User user = userDao.getUser(userId);
		if(user != null) {
			throw new DuplicateUserIdException();
		}
		
		if(!isValidPassword(password)) {
			throw new InvalidPasswordException();
		}
		
		userDao.createUser(userId, password);
	}

	@Transactional (readOnly=false)
	public void setUserPassword(String userId, String password) throws InvalidPasswordException {
		Validate.isTrue(isValidUserId(userId));
		
		if(!isValidPassword(password)) {
			throw new InvalidPasswordException();
		}
		
		userDao.setPassword(userId, password);
	}
	
	@Transactional (readOnly=false)
	public void deleteUser(String userId) {
		Validate.isTrue(isValidUserId(userId));
		
		userDao.deleteUser(userId);
	}

	public Set<String> getUsers() {
		Set<String> userIds = new HashSet<String>();
		Collection<User> users = userDao.getUsers();
		for (User user : users) {
			userIds.add(user.getId());
		}
		return userIds;
	}


	public UserAttributes getDefaultUserAttributes() {
		UserAttributes userAttributes = getUserAttributes(UserDescription.GUEST_ID);
		return userAttributes;
	}
	
	public UserAttributes getUserAttributes(String userId) {
		Validate.isTrue(isValidUserId(userId));
		
		UserAttributes userAttributes = new UserAttributes();
		User user = userDao.getUser(userId);		
		if(user != null) {
			userAttributes.setAdministrator(user.isAdministrator());
			UserPreferences userPreferences = new UserPreferences();
			userPreferences.setLabel(user.getLabel());
			userPreferences.setLocale(user.getLocale());
			userPreferences.setTheme(user.getTheme());
			userAttributes.setPreferences(userPreferences);
		}
		return userAttributes;
	}
	
	@Transactional (readOnly=false)
	public void setUserAttributes(String userId,
			UserAttributes userAttributes) {
		Validate.isTrue(isValidUserId(userId));
		
		User user = userDao.getUser(userId);
		user.setAdministrator(userAttributes.isAdministrator());
		UserPreferences userPreferences = userAttributes.getPreferences();
		user.setLabel(userPreferences.getLabel());
		user.setLocale(userPreferences.getLocale());
		user.setTheme(userPreferences.getTheme());
	}
	
	public PageDescription getUserPageDescription(String localeName, String userId) {
		Validate.isTrue(isValidUserId(userId));
		
		PageDescription pageDescription = getExistingUserPageDescription(userId);
		if(pageDescription == null) {
			pageDescription = getExistingUserPageDescription(
					UserDescription.GUEST_ID + "_" + localeName);
		}
		if(pageDescription == null) {
			try {
				DocumentBuilder builder =
					DocumentBuilderFactory.newInstance().newDocumentBuilder();
				InputStream is = getClass().getResourceAsStream(
					"/org/glimpse/server/default-page-" + localeName + ".xml");
				if(is == null) {
					is = getClass().getResourceAsStream(
						"/org/glimpse/server/default-page.xml");
				}
				Document doc = builder.parse(is);
				
				return XmlUserManagerUtils.buildPage(doc);
				
			} catch(Exception e) {
				logger.error("Error while reading page description xml", e);
				return null;
			}
		}
		return pageDescription;
	}

	private PageDescription getExistingUserPageDescription(String userId) {
		Validate.isTrue(isValidUserId(userId));
		
		User user = userDao.getUser(userId);		
		if(user != null) {
			List<ServerTabDescription> serverTabDescriptions = user.getTabDescriptions();
			if(serverTabDescriptions == null || serverTabDescriptions.isEmpty()) {
				return null;
			}
			return OrmUserManagerUtils.buildPage(serverTabDescriptions);
		} else {
			return null;
		}
	}

	@Transactional (readOnly=false)
	public void setUserPageDescription(String userId,
			PageDescription pageDescription) {
		Validate.isTrue(isValidUserId(userId));
		
		User user = userDao.getUser(userId);		
		if(user != null) {
			user.getTabDescriptions().clear();
			user.getTabDescriptions().addAll(OrmUserManagerUtils.buildServerTabDescriptions(
					user, pageDescription));
		}
	}
	
	public PageDescription getDefaultPageDescription(String localeName) {
		return getUserPageDescription(localeName, UserDescription.GUEST_ID + "_" + localeName);
	}

	public void setDefaultPageDescription(String localeName, PageDescription pageDescription) {
		setUserPageDescription(UserDescription.GUEST_ID + "_" + localeName, pageDescription);
		
	}

	public boolean isAdministrator(String userId) {
		Validate.isTrue(isValidUserId(userId));
		
		User user = userDao.getUser(userId);		
		if(user != null) {
			return user.isAdministrator();
		}
		
		return false;
	}

}
