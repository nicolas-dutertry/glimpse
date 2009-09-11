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
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public class HtmlComponent extends Component {
	
	private static final String PROPERTY_TITLE = "title";
	private static final String PROPERTY_HTML = "html";
	
	private AggregatorConstants constants = GWT.create(AggregatorConstants.class);
	
	private Label titleWidget;
	private SimplePanel optionPanel;
	private TextBox titleInput;
	private TextArea htmlArea;
	private HTML htmlWidget;
	
	private class OptionHandler implements ClickHandler {
		public void onClick(ClickEvent event) {
			if(optionPanel.isVisible()) {
				optionPanel.setVisible(false);
			} else {
				synchronizeOptions();
				optionPanel.setVisible(true);
			}
		}
	}

	public HtmlComponent() {
		this(new HashMap<String, String>());
	}
	
	public HtmlComponent(Map<String, String> properties) {
		super(properties);
		
		titleWidget = new Label(getTitle());
		setTitleWidget(titleWidget);
		
		List<Widget> actions = new LinkedList<Widget>();
		
		Image optionButton = new Image("images/options.png");
		optionButton.addClickHandler(new OptionHandler());
		optionButton.setTitle(constants.options());
		actions.add(optionButton);
		
		setActions(actions);
		
		// Contenu
		VerticalPanel panel = new VerticalPanel();		
		panel.setWidth("100%");
		
		optionPanel = new SimplePanel();
		optionPanel.setStylePrimaryName("component-options");
		VerticalPanel vp = new VerticalPanel();
		FlexTable table = new FlexTable();		
		
		table.setText(0, 0, constants.title());
		titleInput = new TextBox();
		table.setWidget(0, 1, titleInput);
		
		table.setText(1, 0, constants.html());
		htmlArea = new TextArea();
		table.setWidget(1, 1, htmlArea);
		
		vp.add(table);
		
		Button button = new Button(constants.ok());
		button.addClickHandler(new ClickHandler() {			
			public void onClick(ClickEvent event) {
				setProperty(PROPERTY_TITLE, titleInput.getValue());
				setProperty(PROPERTY_HTML, htmlArea.getValue());
				Aggregator.getInstance().update();
				refresh();
			}
		});
		vp.add(button);
		
		optionPanel.add(vp);
		panel.add(optionPanel);
		
		synchronizeOptions();
		
		String html = getHtml();		
		if(html != null && !html.trim().equals("")) {
			optionPanel.setVisible(false);
		}
		
		htmlWidget = new HTML(getHtml());
		panel.add(htmlWidget);
		
		setContent(panel);
	}
	
	public String getTitle() {
		return getProperty(PROPERTY_TITLE);
	}
	
	public String getHtml() {
		return getProperty(PROPERTY_HTML);
	}
	
	private void synchronizeOptions() {
		titleInput.setValue(getTitle());
		htmlArea.setValue(getHtml());
	}

	public void refresh() {
		String html = getHtml();
		if(html == null || html.trim().equals("")) {
			optionPanel.setVisible(true);
		} else {
			optionPanel.setVisible(false);
		}
		
		titleWidget.setText(getTitle());
		htmlWidget.setHTML(html);
	}
	
	@Override
	public Type getType() {
		return Type.HTML;
	}
}
