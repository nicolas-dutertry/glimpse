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

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.io.Serializable;
import java.util.Collections;
import java.util.List;
import javax.persistence.Basic;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Table;
import org.apache.commons.lang.StringUtils;


@Entity
@Table(name = "glimpseuser")
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
	
	@Lob
    @Basic(fetch = FetchType.LAZY)
    private String jsonTabs;
	
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

    public String getJsonTabs() {
        return jsonTabs;
    }

    public void setJsonTabs(String jsonTabs) {
        this.jsonTabs = jsonTabs;
    }
    
    public List<ServerTabDescription> getTabDescriptions() {
        String json = getJsonTabs();
        if(StringUtils.isBlank(json)) {
            return Collections.emptyList();
        }
        
        ObjectMapper mapper = new ObjectMapper();
        try {            
            return mapper.readValue(json, new TypeReference<List<ServerTabDescription>>(){});
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }
     public void setTabDescriptions(List<ServerTabDescription> serverTabDescriptions) {         
        ObjectMapper mapper = new ObjectMapper();
        try {
            String json = mapper.writeValueAsString(serverTabDescriptions);
            setJsonTabs(json);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
     }
    
	
}
