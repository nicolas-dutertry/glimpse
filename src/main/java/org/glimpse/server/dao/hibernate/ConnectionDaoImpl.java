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

import org.glimpse.server.dao.ConnectionDao;
import org.glimpse.server.model.Connection;
import org.glimpse.server.model.User;
import org.springframework.stereotype.Component;

@Component
public class ConnectionDaoImpl implements ConnectionDao {
    
    @PersistenceContext(unitName = "glimpse")
    private EntityManager entityManager;
	
	@SuppressWarnings("unchecked")
	public Collection<Connection> getConnections() {
        TypedQuery<Connection> query = entityManager.createQuery("SELECT c FROM Connection c", Connection.class);
		return query.getResultList();
	}

	public void createConnection(String id, User user) {
		Connection connection = new Connection(id, user);
		entityManager.persist(connection);
	}

	public void deleteConnection(String id) {
		Connection connection = entityManager.find(Connection.class, id);
		entityManager.remove(connection);
	}

	public Connection getConnection(String id) {
		return entityManager.find(Connection.class, id);
	}

}
