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
package org.glimpse.service;

import java.util.Collection;

import org.glimpse.client.UserDescription;

public interface UserService {
	Collection<UserDescription> getUsers();
	UserDescription getUser(String userId);
	void createUser(String userId, String password);
	void setUserPassword(String userId, String password);
	void updateUser(String userId,
			boolean administrator, String label,
			String locale, String theme);
	void deleteUser(String userId);
}
