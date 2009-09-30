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

import org.glimpse.client.widgets.HorizontalPanelExt;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;

public class AggregatorTabTitle extends Composite implements ClickHandler {
	private HorizontalPanelExt panel;
	private Image left;
	private Label center;
	private Image options;
	private Image right;
	
	private AggregatorTabPanel tabPanel;
	
	private boolean selected = false;
	
	public AggregatorTabTitle(AggregatorTabPanel tabPanel, String text) {
		
		this.tabPanel = tabPanel;
		panel = new HorizontalPanelExt();
		panel.addClickHandler(this);
		
		left = new Image(Aggregator.TRANSPARENT_IMAGE);
		panel.add(left);
		panel.setCellClass(left, "tabtitle-left");
		
		center = new Label(text);
		center.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				if(AggregatorTabTitle.this.isSelected()) {
					event.stopPropagation();
				}
			}
		});
		panel.add(center);
		panel.setCellClass(center, "tabtitle-center");
		
		if(Aggregator.getInstance().isModifiable()) {
			options = new Image(Aggregator.TRANSPARENT_IMAGE);
			options.setVisible(false);
			panel.add(options);
			panel.setCellClass(options, "tabtitle-options");
		}
		
		right = new Image(Aggregator.TRANSPARENT_IMAGE);
		panel.add(right);
		panel.setCellClass(right, "tabtitle-right");
		
		initWidget(panel);
	}
	
	public void onClick(ClickEvent event) {
		if(selected) {
			tabPanel.toogleOptions();
		} else {
			int i = ((HorizontalPanel)getParent()).getWidgetIndex(AggregatorTabTitle.this);
			tabPanel.selectTab(i);
		}
	}
	
	public boolean isSelected() {
		return selected;
	}
	
	public void setSelected(boolean selected) {
		this.selected = selected;
		String classSuffix = "";
		if(selected) {
			classSuffix = "-selected";
		}
		
		panel.setCellClass(left, "tabtitle-left" + classSuffix);
		panel.setCellClass(center, "tabtitle-center" + classSuffix);
		if(Aggregator.getInstance().isModifiable()) {
			panel.setCellClass(options, "tabtitle-options" + classSuffix);
		}
		panel.setCellClass(right, "tabtitle-right" + classSuffix);
	}
	
	public String getText() {
		return center.getText();
	}
	
	public void setText(String text) {
		center.setText(text);
	}
	
}
