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
package org.glimpse.server.manager;

import java.util.Set;

import org.glimpse.client.UserAttributes;
import org.glimpse.client.layout.PageDescription;

public interface UserManager {
	/**
	 * Connect a user with a given login id and a given password.
	 * Return a connection id as a string available for future interactions with the server. 
	 * @param login the user's login id
	 * @param password the user's password
	 * @return Connection id
	 * @throws AuthenticationException
	 */
	String connect(String login, String password) throws AuthenticationException;
	
	/**
	 * Check if a given password is valid for a given login id. 
	 * @param login the user's login id
	 * @param password the user's password
	 * @throws AuthenticationException
	 */
	void checkPassword(String login, String password) throws AuthenticationException;	
	
	/**
	 * Get the identifier of a connected user.  
	 * @param connectionId the connection id
	 * @return the user's identifier
	 */
	String getUserId(String connectionId);
	
	/**
	 * Close a user's connection.
	 * @param connectionId the connection id
	 */
	void disconnect(String connectionId);
	
	void createUser(String userId, String password) throws InvalidUserIdException,
			InvalidPasswordException, DuplicateUserIdException;
	
	void setUserPassword(String userId, String password) throws InvalidPasswordException;
	
	void deleteUser(String userId);
	
	Set<String> getUsers();
	
	UserAttributes getUserAttributes(String userId);
	UserAttributes getDefaultUserAttributes();	
	void setUserAttributes(String userId, UserAttributes userAttributes);	
	
	PageDescription getUserPageDescription(String localeName, String userId);
	void setUserPageDescription(String userId, PageDescription pageDescription);
	PageDescription getDefaultPageDescription(String localeName);
	void setDefaultPageDescription(String localeName, PageDescription pageDescription);
}
