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
package org.glimpse.server.dao.hibernate;

import java.util.Collection;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.Validate;
import org.glimpse.server.dao.UserDao;
import org.glimpse.server.model.User;
import org.jasypt.util.password.PasswordEncryptor;
import org.springframework.beans.factory.annotation.Autowired;

public class UserDaoImpl implements UserDao {
    
    @PersistenceContext(unitName = "glimpse")
    private EntityManager entityManager;
    
    @Autowired
	private PasswordEncryptor passwordEncryptor;

	public boolean checkPassword(String login, String password) {
		User user = getUser(login);
		if(user != null) {
			return passwordEncryptor.checkPassword(password, user.getEncryptedPassword());
		}
		return false;
	}
	
	public void setPassword(String userId, String password) {
		Validate.isTrue(StringUtils.isNotBlank(password), "password must not be empty");
		
		User user = getUser(userId);
		if(user != null) {
			user.setEncryptedPassword(passwordEncryptor.encryptPassword(password));
		}
	}

	public User getUser(String userId) {
        return entityManager.find(User.class, userId);
	}
	
	@SuppressWarnings("unchecked")
	public Collection<User> getUsers() {
        TypedQuery<User> query = entityManager.createQuery("SELECT u FROM User u", User.class);
		return query.getResultList();
	}

	public void createUser(String userId, String password) {
		Validate.isTrue(StringUtils.isNotBlank(userId), "userId must not be empty");
		Validate.isTrue(StringUtils.isNotBlank(password), "password must not be empty");
		
		User user = new User(userId, passwordEncryptor.encryptPassword(password));
		user.setLabel(userId);
		user.setLocale("en");
		user.setTheme("default");
		entityManager.persist(user);
	}
	
	public void deleteUser(String userId) {
		User user = entityManager.find(User.class, userId);
		if(user != null) {
			entityManager.remove(user);
		}
	}
}
