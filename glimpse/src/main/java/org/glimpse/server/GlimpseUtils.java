package org.glimpse.server;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class GlimpseUtils {
	public static final String COOKIE_CONNECTION = "org.glimpse.connectionId";
	
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
	
	public static void setConnectionId(HttpServletResponse response,
			String connectionId,
			boolean persistent) {
		Cookie cookie = new Cookie(COOKIE_CONNECTION, connectionId);
		if(!persistent) {
			cookie.setMaxAge(-1);
		} else {
			cookie.setMaxAge(365*24*60*60);
		}
		response.addCookie(cookie);
	}
}
