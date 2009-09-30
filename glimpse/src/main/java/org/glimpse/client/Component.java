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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.glimpse.client.i18n.AggregatorConstants;
import org.glimpse.client.layout.ComponentDescription.Type;
import org.glimpse.client.widgets.HorizontalPanelExt;
import org.glimpse.client.widgets.VerticalPanelExt;

import com.allen_sauer.gwt.dnd.client.HasDragHandle;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;

public abstract class Component extends Composite implements HasDragHandle {
	private SimplePanel titlePanel;
	private Widget dragHandle;
	private HorizontalPanelExt actionsPanel;
	private SimplePanel contentPanel;
	private Map<String, String> properties;
	private AggregatorConstants constants = GWT.create(AggregatorConstants.class);
	
	public Component(Map<String, String> properties) {
		this.properties = new HashMap<String, String>(properties);
		
		SimplePanel mainPanel = new SimplePanel();
		mainPanel.setStylePrimaryName("component");
		
		SimplePanel frame = new SimplePanel();
		frame.setWidth("100%");
		mainPanel.add(frame);
		
		VerticalPanelExt panel = new VerticalPanelExt();
		panel.setWidth("100%");
		frame.add(panel);
		
		HorizontalPanelExt topPanel = new HorizontalPanelExt();
		topPanel.setWidth("100%");
		topPanel.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
		
		Image titleLeft = new Image(Aggregator.TRANSPARENT_IMAGE);
		topPanel.add(titleLeft);
		topPanel.setCellClass(titleLeft, "component-title-left");
		
		titlePanel = new SimplePanel();
		topPanel.add(titlePanel);
		topPanel.setCellClass(titlePanel, "component-title-content");
				
		actionsPanel = new HorizontalPanelExt();
		actionsPanel.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
		dragHandle = new FocusPanel(new Image(Aggregator.TRANSPARENT_IMAGE));
		dragHandle.setStylePrimaryName("component-action-move");
		actionsPanel.add(dragHandle);
		actionsPanel.setCellClass(dragHandle, "component-action");
		
		FocusPanel deleteButton = new FocusPanel(new Image(Aggregator.TRANSPARENT_IMAGE));
		deleteButton.setTitle(constants.delete());
		deleteButton.setStylePrimaryName("component-action-delete");
		deleteButton.addClickHandler(new ClickHandler() {			
			public void onClick(ClickEvent event) {
				if(Window.confirm(constants.deleteComponentConfirm())) {
					removeFromParent();
					Aggregator.getInstance().update();
				}
			}
		});
		actionsPanel.add(deleteButton);
		actionsPanel.setCellClass(deleteButton, "component-action");
		
		topPanel.add(actionsPanel);
		topPanel.setCellHorizontalAlignment(actionsPanel, HorizontalPanel.ALIGN_RIGHT);
		topPanel.setCellClass(actionsPanel, "component-title-actions");
		
		Image titleRight = new Image(Aggregator.TRANSPARENT_IMAGE);
		topPanel.add(titleRight);
		topPanel.setCellClass(titleRight, "component-title-right");
		
		HorizontalPanelExt center = new HorizontalPanelExt();
		center.setWidth("100%");
		
		Image left = new Image(Aggregator.TRANSPARENT_IMAGE);
		center.add(left);
		center.setCellClass(left, "component-left");
		
		contentPanel = new SimplePanel();
		contentPanel.setWidth("100%");
		center.add(contentPanel);
		center.setCellClass(contentPanel, "component-content");
		
		Image right = new Image(Aggregator.TRANSPARENT_IMAGE);
		center.add(right);
		center.setCellClass(right, "component-right");
		
		HorizontalPanelExt bottom = new HorizontalPanelExt();
		bottom.setWidth("100%");
		
		Image bottomLeft = new Image(Aggregator.TRANSPARENT_IMAGE);
		bottom.add(bottomLeft);
		bottom.setCellClass(bottomLeft, "component-bottom-left");
		
		
		Image bottomCenter = new Image(Aggregator.TRANSPARENT_IMAGE);		
		bottom.add(bottomCenter);
		bottom.setCellClass(bottomCenter, "component-bottom-center");
		
		Image bottomRight = new Image(Aggregator.TRANSPARENT_IMAGE);
		bottom.add(bottomRight);
		bottom.setCellClass(bottomRight, "component-bottom-right");
		
		panel.add(topPanel);
		panel.add(center);
		panel.add(bottom);
		
		initWidget(mainPanel);

		Aggregator.getInstance().getDragController().makeDraggable(this);
	}
	
	public void setTitleWidget(Widget widget) {
		titlePanel.setWidget(widget);
	}
	
	public void setActions(List<Widget> widgets) {
		for (Widget widget : widgets) {
			actionsPanel.insert(widget, actionsPanel.getWidgetCount()-1);
			actionsPanel.setCellClass(widget, "component-action");
		}
	}
	
	public void setContent(Widget widget) {
		contentPanel.setWidget(widget);
	}
	
	public Map<String, String> getProperties() {
		return new HashMap<String, String>(properties);
	}
	
	public String getProperty(String name) {
		return properties.get(name);
	}
	
	public void setProperty(String name, String value) {
		properties.put(name, value);
	}	

	public Widget getDragHandle() {
		return dragHandle;
	}
	
	protected void onTabActivated() {
		// Nothing by default
	}
	
	public abstract Type getType();
}
