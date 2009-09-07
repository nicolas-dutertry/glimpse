package org.glimpse.client.widgets;

import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Widget;

public class HorizontalPanelExt extends HorizontalPanel {
	public void setCellClass(Widget w, String className) {
		Element td = DOM.getParent(w.getElement());
		td.setClassName(className);
	}
}
