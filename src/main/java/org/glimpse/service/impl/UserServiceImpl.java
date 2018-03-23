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
package org.glimpse.service.impl;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.glimpse.client.UserAttributes;
import org.glimpse.client.UserDescription;
import org.glimpse.client.UserPreferences;
import org.glimpse.server.manager.DuplicateUserIdException;
import org.glimpse.server.manager.InvalidPasswordException;
import org.glimpse.server.manager.InvalidUserIdException;
import org.glimpse.server.manager.UserManager;
import org.glimpse.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;

@Path("/users/")
@Produces({MediaType.APPLICATION_JSON})
public class UserServiceImpl implements UserService {
    @Autowired
	private UserManager userManager;

	@GET
	@Path("/")
	@Override
	public Collection<UserDescription> getUsers() {
		List<UserDescription> userDescriptions = new LinkedList<UserDescription>();
		
		Set<String> userIds = userManager.getUsers();
		for (String userId : userIds) {
			UserAttributes attributes = userManager.getUserAttributes(userId);
			UserDescription userDescription = new UserDescription(userId);
			userDescription.setAttributes(attributes);
			userDescriptions.add(userDescription);
		}
		
		return userDescriptions;
	}

	@GET
	@Path("/{userId}")
	@Override
	public UserDescription getUser(@PathParam("userId") String userId) {
		UserAttributes attributes = userManager.getUserAttributes(userId);
		if(attributes == null) {
			return null;
		}
		
		UserDescription userDescription = new UserDescription(userId);
		userDescription.setAttributes(attributes);
		return userDescription;
	}

	@POST
	@Path("/")
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	@Override
	public void createUser(@FormParam("userId") String userId,
			@FormParam("password") String password) {
		try {
			userManager.createUser(userId, password);
		} catch (InvalidUserIdException e) {
			throw new WebApplicationException(Response.status(Status.INTERNAL_SERVER_ERROR).entity("Invalid user ID").build());
		} catch (InvalidPasswordException e) {
			throw new WebApplicationException(Response.status(Status.INTERNAL_SERVER_ERROR).entity("Invalid password").build());
		} catch (DuplicateUserIdException e) {
			throw new WebApplicationException(Response.status(Status.INTERNAL_SERVER_ERROR).entity("Duplicate user ID").build());
		}
	}

	@POST
	@Path("/{userId}/password")
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	@Override
	public void setUserPassword(@PathParam("userId") String userId,
			@FormParam("password") String password) {
		try {
			userManager.setUserPassword(userId, password);
		} catch (InvalidPasswordException e) {
			throw new WebApplicationException(Response.status(Status.INTERNAL_SERVER_ERROR).entity("Invalid password").build());
		}
	}

	@POST
	@Path("/{userId}/attributes")
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED) 
	@Override
	public void updateUser(@PathParam("userId") String userId,
			@FormParam("administrator") boolean administrator,
			@FormParam("label") String label,
			@FormParam("locale") String locale,
			@FormParam("theme") String theme) {		
		UserAttributes userAttributes = new UserAttributes();
		userAttributes.setAdministrator(administrator);
		UserPreferences userPreferences = new UserPreferences();
		userPreferences.setLabel(label);
		userPreferences.setLocale(locale);
		userPreferences.setTheme(theme);
		userAttributes.setPreferences(userPreferences);
		
		userManager.setUserAttributes(userId, userAttributes);
	}

	@DELETE
	@Path("/{userId}")
	@Override
	public void deleteUser(@PathParam("userId") String userId) {
		userManager.deleteUser(userId);
	}
}
