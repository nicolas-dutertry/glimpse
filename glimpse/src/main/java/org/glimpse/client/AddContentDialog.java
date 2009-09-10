package org.glimpse.client;

import org.glimpse.client.news.NewsReader;

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
	public AddContentDialog() {
		FlowPanel dialogContent = new FlowPanel();
		add(dialogContent);
		
		HorizontalPanel typePanel = new HorizontalPanel();
		dialogContent.add(typePanel);
		typePanel.add(new Label("Type"));
		typeList = new ListBox();
		typePanel.add(typeList);
		typeList.addItem("News reader");
		typeList.addItem("Link");
		typeList.addItem("HTML");
		
		HorizontalPanel buttonPanel = new HorizontalPanel();
		dialogContent.add(buttonPanel);
		Button ok = new Button("OK");
		buttonPanel.add(ok);
		ok.addClickHandler(new ClickHandler() {			
			public void onClick(ClickEvent event) {
				Component component = null;
				if(typeList.getSelectedIndex() == 1) {
					// TODO
				} else if(typeList.getSelectedIndex() == 2) {
					// TODO
				} else {
					component = new NewsReader();
				}
				hide();
				Aggregator.getInstance().addComponent(component);
				Aggregator.getInstance().update();
			}
		});
		
		Button cancel = new Button("Cancel");
		buttonPanel.add(cancel);
		cancel.addClickHandler(new ClickHandler() {			
			public void onClick(ClickEvent event) {
				hide();
			}
		});
	}
}
