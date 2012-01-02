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
package org.glimpse.server.servlet;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.glimpse.client.UserAttributes;
import org.glimpse.client.layout.PageDescription;
import org.glimpse.server.GlimpseUtils;
import org.glimpse.server.manager.UserManager;
import org.glimpse.server.manager.xml.XmlUserManagerUtils;
import org.springframework.web.context.support.WebApplicationContextUtils;
import org.w3c.dom.Document;

public class ModifyUserServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static final Log logger = LogFactory.getLog(ModifyUserServlet.class);
	
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		doPost(request, response);
	}
	
	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		String connectedUserId = GlimpseUtils.getUserId(request);
		if(StringUtils.isBlank(connectedUserId)) {
			throw new ServletException("Login before using this page");
		}
		
		UserManager userManager = WebApplicationContextUtils.getWebApplicationContext(
				getServletContext()).getBean(UserManager.class);
		UserAttributes connectedUserAttributes = userManager.getUserAttributes(connectedUserId);
		if(connectedUserAttributes == null) {
			throw new ServletException("Login before using this page");
		}
		
		if(!connectedUserAttributes.isAdministrator()) {
			throw new ServletException("You must be administrator to use this page");
		}
		
		try {
			String userId = null;
			
			if(ServletFileUpload.isMultipartContent(request)) {
				// Create a factory for disk-based file items
				FileItemFactory factory = new DiskFileItemFactory();
		
				// Create a new file upload handler
				ServletFileUpload upload = new ServletFileUpload(factory);
				
				// Parse the request				
				String password1 = null;
				String password2 = null;
				String administrator = null;
				String label = null;
				String locale = null;
				String theme = null;
				Document pageDocument = null;
				DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
				@SuppressWarnings("unchecked")
				List<FileItem> items = upload.parseRequest(request);
				for (FileItem item : items) {
					if(item.getFieldName().equals("userId")) {
						userId = item.getString();
					} else if(item.getFieldName().equals("password1")) {
						password1 = item.getString();
					} else if(item.getFieldName().equals("password2")) {
						password2 = item.getString();
					} else if(item.getFieldName().equals("administrator")) {
						administrator = item.getString();
					} else if(item.getFieldName().equals("label")) {
						label = item.getString();
					} else if(item.getFieldName().equals("locale")) {
						locale = item.getString();
					} else if(item.getFieldName().equals("theme")) {
						theme = item.getString();
					} else if(item.getFieldName().equals("page")) {
						if(item.getSize() > 0) {
							pageDocument = builder.parse(item.getInputStream());
						}
					}
				}
				
				String errorMessage = null;
				
				if(StringUtils.isBlank(userId)) {
					errorMessage = "User ID is empty";
				}
				
				if(StringUtils.isEmpty(errorMessage) && StringUtils.isNotBlank(password1) &&
						!StringUtils.equals(password1, password2)) {
					errorMessage = "Passwords do not match";
				}
				
				if(connectedUserId.equals(userId) && !"true".equals(administrator)) {
					errorMessage = "Cannot remove administrator rights to the current user";
				}
				
				if(StringUtils.isEmpty(errorMessage)) {
					if(StringUtils.isNotBlank(password1)) {
						userManager.setUserPassword(userId, password1);
					}
					
					UserAttributes userAttributes = userManager.getUserAttributes(userId);
					if(StringUtils.isNotBlank(administrator)) {
						userAttributes.setAdministrator("true".equals(administrator));
					}
					if(StringUtils.isNotBlank(label)) {
						userAttributes.getPreferences().setLabel(label);
					}
					if(StringUtils.isNotBlank(locale)) {
						userAttributes.getPreferences().setLocale(locale);
					}
					if(StringUtils.isNotBlank(theme)) {
						userAttributes.getPreferences().setTheme(theme);
					}
					
					userManager.setUserAttributes(userId, userAttributes);
					
					if(pageDocument != null) {
						PageDescription pageDescription = XmlUserManagerUtils.buildPage(pageDocument);
						userManager.setUserPageDescription(userId, pageDescription);
					}
					
					request.setAttribute("message", "User " + userId + " successfully updated");
				} else {
					request.setAttribute("errorMessage", errorMessage);
				}
			} else {
				userId = request.getParameter("userId");
			}
			
			request.setAttribute("userId", userId);
			UserAttributes userAttributes = userManager.getUserAttributes(userId);
			request.setAttribute("userAttributes", userAttributes);
			
			getServletContext().getRequestDispatcher("/WEB-INF/views/modify-user.jsp").forward(
					request, response);
		} catch(Exception e) {
			logger.error("Exception", e);
			throw new ServletException(e);
		}
		
		
	}
	
	

}
