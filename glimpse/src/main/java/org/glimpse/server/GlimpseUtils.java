package org.glimpse.server;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;

public class GlimpseUtils {
	public static final String COOKIE_CONNECTION = "org.glimpse.connectionId";
	public static final String REQUEST_ATTRIBUTE_USER_ID = "org.glimpse.userId";
	
	public static String getConnectionId(HttpServletRequest request) {
		Cookie[] cookies = request.getCookies();
		if(cookies == null) {
			return null;
		}
		for (Cookie cookie : cookies) {
			if(cookie.getName().equals(COOKIE_CONNECTION)) {
				return cookie.getValue();
			}
		}
		return null;
	}
	
	public static void setConnectionId(HttpServletRequest request,
			HttpServletResponse response,
			String connectionId,
			boolean persistent) {
		Cookie cookie = new Cookie(COOKIE_CONNECTION, connectionId);
		cookie.setPath(request.getContextPath() + "/");
		if(!persistent) {
			cookie.setMaxAge(-1);
		} else {
			cookie.setMaxAge(365*24*60*60);
		}
		response.addCookie(cookie);
	}
	
	public static String getUserId(HttpServletRequest request) {
		return (String)request.getAttribute(REQUEST_ATTRIBUTE_USER_ID);
	}
	
	public static boolean isConnected(HttpServletRequest request) {
		return StringUtils.isNotEmpty(getUserId(request));
	}
}
