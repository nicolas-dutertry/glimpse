package org.glimpse.client.news;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.VerticalPanel;

public class EntryContentDialog extends DialogBox {
	private ScrollPanel content = new ScrollPanel();
	
	public EntryContentDialog() {		
		content.setWidth("600px");
		content.setHeight("400px");
		
		VerticalPanel panel = new VerticalPanel();
		panel.add(content);
		
		Button button = new Button("Close");
		button.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				EntryContentDialog.this.hide();
			}
		});

		panel.add(button);
		
		setWidget(panel);
	}
	
	public void showEntry(String url, Entry entry) {
		setText(entry.getTitle());
		
		HorizontalPanel waitPanel = new HorizontalPanel();
		Image image = new Image("wait.gif");
		Label label = new Label("loading...");
		waitPanel.add(image);
		waitPanel.add(label);
		content.setWidget(waitPanel);
		
		
		NewsReader.newsRetrieverService.getEntryContent(url, entry.getId(),
				new AsyncCallback<String>() {
					@Override
					public void onSuccess(String result) {
						content.setWidget(new HTML(result));
					}
					
					@Override
					public void onFailure(Throwable caught) {
						content.setWidget(new Label("Error"));
					}
				}
		);
		
		center();
	}
}
