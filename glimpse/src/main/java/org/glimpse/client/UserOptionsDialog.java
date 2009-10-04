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
		
		int themeIndex = 0;
		while(true) {
			String theme = Aggregator.getHiddenValue("theme_" + themeIndex);
			if(theme != null) {
				themesList.addOption(new Select.Option(theme));
			} else {
				break;
			}
			themeIndex++;
		}
		
		
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
