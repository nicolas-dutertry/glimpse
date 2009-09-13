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
	
	public static void setConnectionId(HttpServletResponse response, String connectionId) {
		Cookie cookie = new Cookie(COOKIE_CONNECTION, connectionId);
		cookie.setMaxAge(-1);
		response.addCookie(cookie);
	}
}
