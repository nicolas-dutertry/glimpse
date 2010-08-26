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

import org.glimpse.server.dao.UserDao;
import org.glimpse.server.model.User;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Expression;
import org.jasypt.util.password.PasswordEncryptor;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

public class UserDaoImpl extends HibernateDaoSupport implements UserDao {
	private PasswordEncryptor passwordEncryptor;

	public PasswordEncryptor getPasswordEncryptor() {
		return passwordEncryptor;
	}

	@Required	
	public void setPasswordEncryptor(PasswordEncryptor passwordEncryptor) {
		this.passwordEncryptor = passwordEncryptor;
	}

	public boolean checkPassword(String login, String password) {
		User user = getUser(login);
		if(user != null) {
			return passwordEncryptor.checkPassword(password, user.getEncryptedPassword());
		}
		return false;
	}
	
	public void setPassword(String userId, String password) {
		User user = getUser(userId);
		if(user != null) {
			user.setEncryptedPassword(passwordEncryptor.encryptPassword(password));
		}
	}

	public User getUser(String userId) {
		Session session = getHibernateTemplate().getSessionFactory().getCurrentSession();
		// create a new criteria
		Criteria crit = session.createCriteria(User.class);
		crit.add(Expression.eq("id", userId));
		
		User user = (User)crit.uniqueResult();
		return user;
	}
	
	@SuppressWarnings("unchecked")
	public Collection<User> getUsers() {
		Session session = getHibernateTemplate().getSessionFactory().getCurrentSession();
		Criteria crit = session.createCriteria(User.class);		
		return crit.list();
	}

	public void createUser(String userId, String password) {
		Session session = getHibernateTemplate().getSessionFactory().getCurrentSession();
		User user = new User(userId, passwordEncryptor.encryptPassword(password));
		user.setLabel(userId);
		user.setLocale("en");
		user.setTheme("default");
		session.save(user);
	}

}
