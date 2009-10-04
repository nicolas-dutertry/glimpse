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
			if(file.isDirectory()) {
				themes.add(file.getName());
			}
		}
		pageContext.setAttribute(var, themes);
        return Tag.SKIP_BODY;
    }

}
