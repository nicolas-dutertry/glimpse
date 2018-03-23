/*
 * Copyright 2009 Gordon Pike.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.glimpse.spring.web;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * RemoteServiceUtil - gives access to variables from the Http request.
 * @author Gordon Pike.
 *
 */
public class RemoteServiceUtil {
	private static final Log log = LogFactory.getLog(RemoteServiceUtil.class);

	private static final ThreadLocal<HttpServletRequest> perThreadRequest = new ThreadLocal<HttpServletRequest>();

	private static final ThreadLocal<HttpServletResponse> perThreadResponse = new ThreadLocal<HttpServletResponse>();

	private static final ThreadLocal<ServletContext> perThreadContext = new ThreadLocal<ServletContext>();

	/**
	 * Sets the
	 * <code>HttpServletRequest,HttpServletResponse,ServletContext</code>
	 * objects for the current call. It is stored thread-locally so that
	 * simultaneous invocations can have different request objects.
	 */
	protected static void setThreadLocals(HttpServletRequest request,
			HttpServletResponse response, ServletContext context) {
		setThreadLocalRequest(request);
		setThreadLocalResponse(response);
		setThreadLocalContext(context);
	}

	/**
	 * Sets the <code>HttpServletRequest</code> object for the current call. It
	 * is stored thread-locally so that simultaneous invocations can have
	 * different request objects.
	 */
	protected static void setThreadLocalRequest(HttpServletRequest request) {
		perThreadRequest.set(request);
		log.debug("Set request to: " + request + "for thread: "
				+ Thread.currentThread().getId());
	}

	/**
	 * Sets the <code>HttpServletResponse</code> object for the current call. It
	 * is stored thread-locally so that simultaneous invocations can have
	 * different response objects.
	 */
	protected static void setThreadLocalResponse(HttpServletResponse response) {
		perThreadResponse.set(response);
		log.debug("Set response to: " + response + "for thread: "
				+ Thread.currentThread().getId());
	}

	/**
	 * Sets the <code>ServletContext</code> object for the current call. It is
	 * stored thread-locally so that simultaneous invocations can have different
	 * request objects.
	 */
	protected static void setThreadLocalContext(ServletContext context) {
		perThreadContext.set(context);
		log.debug("Set context to: " + context + "for thread: "
				+ Thread.currentThread().getId());
	}

	/**
	 * Gets the <code>HttpServletRequest</code> object for the current call. It
	 * is stored thread-locally so that simultaneous invocations can have
	 * different request objects.
	 */
	public static final HttpServletRequest getThreadLocalRequest() {
		return perThreadRequest.get();
	}

	/**
	 * Gets the <code>HttpServletResponse</code> object for the current call. It
	 * is stored thread-locally so that simultaneous invocations can have
	 * different response objects.
	 */
	public static final HttpServletResponse getThreadLocalResponse() {
		return perThreadResponse.get();
	}

	/**
	 * Gets the <code>ServletContext</code> object for the current call. It is
	 * stored thread-locally so that simultaneous invocations can have different
	 * context objects.
	 */
	public static final ServletContext getThreadLocalContext() {
		return perThreadContext.get();
	}

	/**
	 * Clears out the ThreadLocals. This is a convienence method.
	 */
	public static final void clearThreadLocals() {
		perThreadRequest.set(null);
		perThreadResponse.set(null);
		perThreadContext.set(null);
	}

	/**
	 * Gets the <code>HttpSession</code> object for the current call. It is
	 * stored thread-locally so that simultaneous invocations can have different
	 * context objects. This is a convenience method, it's equivalent to
	 * getThreadLocalRequest().getSession() with null checking.
	 * @return HttpSession
	 */
	public static final HttpSession getThreadLocalSession() {
		HttpSession session = null;
		HttpServletRequest request = perThreadRequest.get();
		if (request != null) {
			session = request.getSession();
			if (session != null)
				log.debug("Session found: " + session.getId());
		}
		return session;
	}

}
