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

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.PasswordTextBox;
import com.google.gwt.user.client.ui.TextBox;

public class LoginDialog extends DialogBox {
	private AggregatorConstants constants = GWT.create(AggregatorConstants.class);
	private LoginServiceAsync loginService = GWT.create(LoginService.class);
	
	private TextBox loginInput;
	private PasswordTextBox passwordInput;
	private CheckBox rememberMe;
	
	public LoginDialog() {
		setText(constants.login());
		
		FlowPanel panel = new FlowPanel();
		
		FlexTable table = new FlexTable();
		table.setText(0, 0, constants.loginId());
		loginInput = new TextBox();
		loginInput.setName("login");
		table.setWidget(0, 1, loginInput);
		
		table.setText(1, 0, constants.password());
		passwordInput = new PasswordTextBox();
		passwordInput.setName("password");
		table.setWidget(1, 1, passwordInput);
		
		panel.add(table);
		
		rememberMe = new CheckBox(constants.rememberMe());
		panel.add(rememberMe);
		
		HorizontalPanel buttonsPanel = new HorizontalPanel();
		buttonsPanel.setWidth("100%");
		buttonsPanel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
		panel.add(buttonsPanel);
		
		Button ok = new Button(constants.ok());
		ok.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				loginService.connect(loginInput.getValue(),
						passwordInput.getValue(),
						rememberMe.getValue(),
						new AsyncCallback<Boolean>() {					
							public void onSuccess(Boolean result) {
								if(result) {
									Aggregator.getInstance().reloadPage();
								} else {
									Window.alert(constants.incorrectLoginPassword());
								}
							}
							
							public void onFailure(Throwable caught) {
								Window.alert(Aggregator.SERVER_ERROR);
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
		loginInput.setValue("");
		passwordInput.setValue("");
		rememberMe.setValue(false);
	}
}
