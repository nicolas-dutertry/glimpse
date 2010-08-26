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

import org.glimpse.server.dao.ConnectionDao;
import org.glimpse.server.model.Connection;
import org.glimpse.server.model.User;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Expression;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

public class ConnectionDaoImpl extends HibernateDaoSupport implements
		ConnectionDao {

	public void createConnection(String id, User user) {
		Session session = getHibernateTemplate().getSessionFactory().getCurrentSession();
		Connection connection = new Connection(id, user);
		session.save(connection);
	}

	public void deleteConnection(String id) {
		Connection connection = getConnection(id);
		Session session = getHibernateTemplate().getSessionFactory().getCurrentSession();
		session.delete(connection);
	}

	public Connection getConnection(String id) {
		Session session = getHibernateTemplate().getSessionFactory().getCurrentSession();
		// create a new criteria
		Criteria crit = session.createCriteria(Connection.class);
		crit.add(Expression.eq("id", id));
		
		Connection connection = (Connection)crit.uniqueResult();
		return connection;
	}

}
