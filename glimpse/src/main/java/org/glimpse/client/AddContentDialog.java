package org.glimpse.client;

import org.glimpse.client.i18n.AggregatorConstants;
import org.glimpse.client.news.NewsReader;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;

public class AddContentDialog extends DialogBox {
	private ListBox typeList;
	private AggregatorConstants constants = GWT.create(AggregatorConstants.class);
	
	public AddContentDialog() {
		setText(constants.addContent());
		
		FlowPanel dialogContent = new FlowPanel();
		add(dialogContent);
		
		HorizontalPanel typePanel = new HorizontalPanel();
		dialogContent.add(typePanel);
		typePanel.add(new Label(constants.type()));
		typeList = new ListBox();
		typePanel.add(typeList);
		typeList.addItem(constants.newsReader());
		typeList.addItem(constants.html());
		
		HorizontalPanel buttonPanel = new HorizontalPanel();
		dialogContent.add(buttonPanel);
		Button ok = new Button(constants.ok());
		buttonPanel.add(ok);
		ok.addClickHandler(new ClickHandler() {			
			public void onClick(ClickEvent event) {
				Component component = null;
				if(typeList.getSelectedIndex() == 1) {
					component = new HtmlComponent();
				} else {
					component = new NewsReader();
				}
				hide();
				Aggregator.getInstance().addComponent(component);
				Aggregator.getInstance().update();
			}
		});
		
		Button cancel = new Button(constants.cancel());
		buttonPanel.add(cancel);
		cancel.addClickHandler(new ClickHandler() {			
			public void onClick(ClickEvent event) {
				hide();
			}
		});
	}
}
