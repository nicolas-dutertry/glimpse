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
package org.glimpse.client;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.glimpse.client.i18n.AggregatorConstants;
import org.glimpse.client.layout.ComponentDescription.Type;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public class HtmlComponent extends Component {
	
	private static final String PROPERTY_TITLE = "title";
	private static final String PROPERTY_HTML = "html";
	private static final String PROPERTY_SCRIPT = "script";
	
	private AggregatorConstants constants = GWT.create(AggregatorConstants.class);
	
	private Label titleWidget;
	private HTML htmlWidget;
	private HtmlComponentOptionDialog optionDialog;
	
	private class OptionHandler implements ClickHandler {
		public void onClick(ClickEvent event) {
			optionDialog.center();
		}
	}

	public HtmlComponent() {
		this(new HashMap<String, String>());
	}
	
	public HtmlComponent(Map<String, String> properties) {
		super(properties);
		
		titleWidget = new Label(getTitle());
		setTitleWidget(titleWidget);
		
		if(Aggregator.getInstance().isModifiable()) {
			List<Widget> actions = new LinkedList<Widget>();
			
			FocusPanel optionButton = new FocusPanel(new Image(Aggregator.TRANSPARENT_IMAGE));
			optionButton.addClickHandler(new OptionHandler());
			optionButton.setTitle(constants.options());
			optionButton.setStylePrimaryName("component-action-options");
			actions.add(optionButton);
			
			setActions(actions);
		}
		
		optionDialog = new HtmlComponentOptionDialog(this);
		
		// Contenu
		VerticalPanel panel = new VerticalPanel();		
		panel.setWidth("100%");
		
		htmlWidget = new HTML(getHtml());
		panel.add(htmlWidget);
		
		setContent(panel);
	}
	
	public void update(String title, String html, String script) {
		setProperty(PROPERTY_TITLE, title);
		setProperty(PROPERTY_HTML, html);
		setProperty(PROPERTY_SCRIPT, script);
		Aggregator.getInstance().update();
		refresh();
	}
	
	public String getTitle() {
		return getProperty(PROPERTY_TITLE);
	}
	
	public String getHtml() {
		return getProperty(PROPERTY_HTML);
	}
	
	public String getScript() {
		return getProperty(PROPERTY_SCRIPT);
	}

	public void refresh() {
		String html = getHtml();
		if(html == null || html.trim().equals("")) {
			html = "Personalize content";
		}
		
		titleWidget.setText(getTitle());
		htmlWidget.setHTML(html);
		
		String script = getScript();
		
		if(script != null && !script.trim().equals("")) {
			evalScript(script);
		}
	}
	
	@Override
	public Type getType() {
		return Type.HTML;
	}
	
	/**
     * Evaluate scripts in an HTML string. Will eval both <script src=""></script>
     * and <script>javascript here</scripts>.
     *
     * @param element a new HTML(text).getElement()
     */
    public static native void evalScript(String scriptText) /*-{
    	$wnd.eval(scriptText);
    }-*/;

	@Override
	protected void onLoad() {
		super.onLoad();
		refresh();
	}
}
