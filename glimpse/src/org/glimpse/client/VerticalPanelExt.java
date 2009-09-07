package org.glimpse.client;

import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public class VerticalPanelExt extends VerticalPanel {
	public void setCellClass(Widget w, String className) {
		Element td = DOM.getParent(w.getElement());
		td.setClassName(className);
	}
}
