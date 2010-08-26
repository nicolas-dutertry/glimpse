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

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.Tag;
import javax.servlet.jsp.tagext.TagSupport;

import org.glimpse.client.UserAttributes;
import org.glimpse.server.GlimpseUtils;
import org.glimpse.server.UserManager;
import org.springframework.web.context.support.WebApplicationContextUtils;

public class LocaleTag extends TagSupport {
	private static final long serialVersionUID = 1L;

	public int doStartTag() throws JspException {
		HttpServletRequest request = (HttpServletRequest)pageContext.getRequest();
		String locale = request.getParameter("locale");
		if(locale == null || locale.equals("")) {
			locale = request.getLocale().toString();
	    	if(GlimpseUtils.isConnected(request)) {
	    		UserManager userManager = 
					WebApplicationContextUtils.getWebApplicationContext(pageContext.getServletContext()).getBean(
							UserManager.class);
				UserAttributes userAttributes = userManager.getUserAttributes(
						GlimpseUtils.getUserId(request));
				locale = userAttributes.getPreferences().getLocale();
	    	}
		}    	
    	try {
			pageContext.getOut().print(locale);
		} catch (IOException e) {
			throw new JspException(e);
		}
        return Tag.SKIP_BODY;
    }
}
