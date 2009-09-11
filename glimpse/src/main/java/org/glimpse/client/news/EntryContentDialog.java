package org.glimpse.client.news;

import org.glimpse.client.i18n.AggregatorConstants;

import com.google.gwt.core.client.GWT;
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
	private AggregatorConstants constants = GWT.create(AggregatorConstants.class);
	
	public EntryContentDialog() {		
		content.setWidth("600px");
		content.setHeight("400px");
		
		VerticalPanel panel = new VerticalPanel();
		panel.add(content);
		
		Button button = new Button(constants.close());
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
		Label label = new Label(constants.loading());
		waitPanel.add(image);
		waitPanel.add(label);
		content.setWidget(waitPanel);
		
		
		NewsReader.newsRetrieverService.getEntryContent(url, entry.getId(),
				new AsyncCallback<String>() {
					public void onSuccess(String result) {
						content.setWidget(new HTML(result));
					}
					
					public void onFailure(Throwable caught) {
						content.setWidget(new Label(constants.error()));
					}
				}
		);
		
		center();
	}
}
