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
