package org.glimpse.server.tag;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.Tag;
import javax.servlet.jsp.tagext.TagSupport;

import org.glimpse.client.UserDescription;
import org.glimpse.server.GlimpseManager;
import org.glimpse.server.GlimpseUtils;
import org.glimpse.server.UserManager;

public class LocaleTag extends TagSupport {
	private static final long serialVersionUID = 1L;

	public int doStartTag() throws JspException {
		HttpServletRequest request = (HttpServletRequest)pageContext.getRequest();
		String locale = request.getLocale().toString();
    	if(GlimpseUtils.isConnected(request)) {
    		GlimpseManager glimpseManager = GlimpseManager.getInstance(pageContext.getServletContext());
			UserManager userManager = glimpseManager.getUserManager();
			UserDescription userDescription = userManager.getUserDescription(
					GlimpseUtils.getUserId(request));
			locale = userDescription.getLocale();
    	}
    	try {
			pageContext.getOut().print(locale);
		} catch (IOException e) {
			throw new JspException(e);
		}
        return Tag.SKIP_BODY;
    }
}
