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

import org.glimpse.client.finance.QuotationComponent;
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
		typeList.addItem(constants.stockExchange());
		
		HorizontalPanel buttonPanel = new HorizontalPanel();
		dialogContent.add(buttonPanel);
		Button ok = new Button(constants.ok());
		buttonPanel.add(ok);
		ok.addClickHandler(new ClickHandler() {			
			public void onClick(ClickEvent event) {
				Component component = null;
				if(typeList.getSelectedIndex() == 1) {
					component = new HtmlComponent();
				} else if(typeList.getSelectedIndex() == 2) {
					component = new QuotationComponent();
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
