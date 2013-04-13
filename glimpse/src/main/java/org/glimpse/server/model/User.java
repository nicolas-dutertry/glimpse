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
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.IndexColumn;

@Entity
@Table(name = "user")
public class User implements Serializable {
	private static final long serialVersionUID = 1L;
	
	@Id
	@Column(nullable = false)
	private String id;
	
	@Column(name="password", nullable = false)
	private String encryptedPassword;
	
	@Column(nullable = false)
	private boolean administrator;
	
	@Column(nullable = false)
	private String label;
	
	@Column(nullable = false)
	private String locale;
	
	@Column(nullable = false)
	private String theme;
	
	@OneToMany(cascade=CascadeType.ALL)
	@Cascade(org.hibernate.annotations.CascadeType.DELETE_ORPHAN)
	@IndexColumn(name="tab_index")
	@JoinColumn(name="user_id", nullable=false)
	private List<ServerTabDescription> tabDescriptions;
	
	public User() {
	}
	
	public User(String id, String encryptedPassword) {
		this.id = id;
		this.encryptedPassword = encryptedPassword;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getEncryptedPassword() {
		return encryptedPassword;
	}

	public void setEncryptedPassword(String encryptedPassword) {
		this.encryptedPassword = encryptedPassword;
	}

	public boolean isAdministrator() {
		return administrator;
	}

	public void setAdministrator(boolean administrator) {
		this.administrator = administrator;
	}
	
	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public String getLocale() {
		return locale;
	}

	public void setLocale(String locale) {
		this.locale = locale;
	}

	public String getTheme() {
		return theme;
	}

	public void setTheme(String theme) {
		this.theme = theme;
	}

	public List<ServerTabDescription> getTabDescriptions() {
		return tabDescriptions;
	}
	
}
