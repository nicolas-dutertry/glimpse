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
package org.glimpse.server.model;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "glimpsecomponent_property")
public class ServerComponentProperty implements Serializable {
	@Id
	@GeneratedValue
	private int id;
	
	@ManyToOne(optional=false)
	@JoinColumn(name="component_id")
	private ServerComponentDescription componentDescription;
	
	@Column(nullable=false)
	private String propertyName;
	
	@Column
	private String propertyValue;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public ServerComponentDescription getComponentDescription() {
		return componentDescription;
	}

	public void setComponentDescription(ServerComponentDescription componentDescription) {
		this.componentDescription = componentDescription;
	}

	public String getPropertyName() {
		return propertyName;
	}

	public void setPropertyName(String propertyName) {
		this.propertyName = propertyName;
	}

	public String getPropertyValue() {
		return propertyValue;
	}

	public void setPropertyValue(String propertyValue) {
		this.propertyValue = propertyValue;
	}
	
}
