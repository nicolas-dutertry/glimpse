package org.glimpse.client;

import org.glimpse.client.i18n.AggregatorConstants;
import org.glimpse.client.widgets.Select;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;

public class UserOptionsDialog extends DialogBox {
	private AggregatorConstants constants = GWT.create(AggregatorConstants.class);
	private UserDescriptionServiceAsync userDescriptionService =
		GWT.create(UserDescriptionService.class);
	
	private Select localesList;
	private Select themesList;
	
	public UserOptionsDialog() {
		setText(constants.userOptions());
		
		FlowPanel panel = new FlowPanel();
		
		FlexTable table = new FlexTable();
		table.setText(0, 0, constants.language());
		localesList = new Select();
		localesList.addOption(new Select.Option(constants.francais(), "fr"));
		localesList.addOption(new Select.Option(constants.english(), "en"));		
		table.setWidget(0, 1, localesList);
		
		table.setText(1, 0, constants.theme());
		themesList = new Select();
		themesList.addOption(new Select.Option("Default", "default"));
		table.setWidget(1, 1, themesList);
		
		panel.add(table);
		
		HorizontalPanel buttonsPanel = new HorizontalPanel();
		buttonsPanel.setWidth("100%");
		buttonsPanel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
		panel.add(buttonsPanel);
		
		Button ok = new Button(constants.ok());
		ok.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				UserDescription userDescription =
					Aggregator.getInstance().getUserDescription();
				userDescription.setLocale(localesList.getSelectedOption().getValue());
				userDescription.setTheme(themesList.getSelectedOption().getValue());
				
				userDescriptionService.setUserDescription(
						userDescription, 
						new AsyncCallback<Void>() {					
							public void onSuccess(Void result) {
								Aggregator.getInstance().reloadPage();
							}
							
							public void onFailure(Throwable caught) {
								hide();
							}
						});
			}
		});		
		buttonsPanel.add(ok);
		
		Button cancel = new Button(constants.cancel());
		cancel.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				hide();
			}
		});		
		buttonsPanel.add(cancel);
		
		setWidget(panel);
	}

	@Override
	public void show() {
		synchronize();
		super.show();
	}

	@Override
	public void center() {
		synchronize();
		super.center();
	}
	
	private void synchronize() {
		UserDescription userDescription =
			Aggregator.getInstance().getUserDescription();
		localesList.setSelectedValue(userDescription.getLocale());
		themesList.setSelectedValue(userDescription.getTheme());
	}
}
