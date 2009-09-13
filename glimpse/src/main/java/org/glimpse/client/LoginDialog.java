package org.glimpse.client;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.PasswordTextBox;
import com.google.gwt.user.client.ui.TextBox;

public class LoginDialog extends DialogBox {
	private FormPanel form;
	public LoginDialog() {
		form  = new FormPanel();
		form.setAction("servlets/login");
		form.setMethod("post");
		
		FlowPanel panel = new FlowPanel();
		form.setWidget(panel);
		
		FlexTable table = new FlexTable();
		table.setText(0, 0, "Login");
		TextBox loginInput = new TextBox();
		loginInput.setName("login");
		table.setWidget(0, 1, loginInput);
		
		table.setText(1, 0, "Password");
		PasswordTextBox passwordInput = new PasswordTextBox();
		passwordInput.setName("password");
		table.setWidget(1, 1, passwordInput);
		
		panel.add(table);
		
		Button button = new Button("OK");
		button.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				form.submit();
			}
		});
		
		panel.add(button);
		
		setWidget(panel);
		
	}
}
