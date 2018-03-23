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
package org.glimpse.server.tag;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.Tag;
import javax.servlet.jsp.tagext.TagSupport;

public class ThemesListTag extends TagSupport {

	private static final long serialVersionUID = 1L;
	
	private String var;

	public String getVar() {
		return var;
	}

	public void setVar(String var) {
		this.var = var;
	}
	
	public int doStartTag() throws JspException {
		List<String> themes = new LinkedList<String>();
		File themesDir = new File(pageContext.getServletContext().getRealPath("themes"));
		File[] files = themesDir.listFiles();
		for (File file : files) {
			if(file.isDirectory() && !file.getName().startsWith(".")) {
				File style = new File(file, "style.css");
				if(style.exists()) {
					themes.add(file.getName());
				}
			}
		}
		pageContext.setAttribute(var, themes);
        return Tag.SKIP_BODY;
    }

}
