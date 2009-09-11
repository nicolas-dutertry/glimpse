package org.glimpse.client;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.glimpse.client.widgets.HorizontalPanelExt;
import org.glimpse.client.widgets.VerticalPanelExt;

import com.allen_sauer.gwt.dnd.client.HasDragHandle;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;

public class Component extends Composite implements HasDragHandle {
	private SimplePanel titlePanel;
	private Widget dragHandle;
	private HorizontalPanelExt actionsPanel;
	private SimplePanel contentPanel;
	private Map<String, String> properties;
	
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
		
		Image titleLeft = new Image("images/p.png");
		topPanel.add(titleLeft);
		topPanel.setCellClass(titleLeft, "component-title-left");
		
		titlePanel = new SimplePanel();
		topPanel.add(titlePanel);
		topPanel.setCellClass(titlePanel, "component-title-content");
				
		actionsPanel = new HorizontalPanelExt();
		dragHandle = new Image("images/move.png");
		actionsPanel.add(dragHandle);
		actionsPanel.setCellClass(dragHandle, "component-action");
		
		Image closeButton = new Image();
		closeButton.setUrl("images/close.png");
		closeButton.addClickHandler(new ClickHandler() {			
			public void onClick(ClickEvent event) {
				if(Window.confirm("Are you sure you want to remove this component ?")) {
					removeFromParent();
					Aggregator.getInstance().update();
				}
			}
		});
		actionsPanel.add(closeButton);
		actionsPanel.setCellClass(closeButton, "component-action");
		
		topPanel.add(actionsPanel);
		topPanel.setCellHorizontalAlignment(actionsPanel, HorizontalPanel.ALIGN_RIGHT);
		topPanel.setCellClass(actionsPanel, "component-title-actions");
		
		Image titleRight = new Image("images/p.png");
		topPanel.add(titleRight);
		topPanel.setCellClass(titleRight, "component-title-right");
		
		HorizontalPanelExt center = new HorizontalPanelExt();
		center.setWidth("100%");
		
		Image left = new Image("images/p.png");
		center.add(left);
		center.setCellClass(left, "component-left");
		
		contentPanel = new SimplePanel();
		contentPanel.setWidth("100%");
		center.add(contentPanel);
		center.setCellClass(contentPanel, "component-content");
		
		Image right = new Image("images/p.png");
		center.add(right);
		center.setCellClass(right, "component-right");
		
		HorizontalPanelExt bottom = new HorizontalPanelExt();
		bottom.setWidth("100%");
		
		Image bottomLeft = new Image("images/p.png");
		bottom.add(bottomLeft);
		bottom.setCellClass(bottomLeft, "component-bottom-left");
		
		
		Image bottomCenter = new Image("images/p.png");		
		bottom.add(bottomCenter);
		bottom.setCellClass(bottomCenter, "component-bottom-center");
		
		Image bottomRight = new Image("images/p.png");
		bottom.add(bottomRight);
		bottom.setCellClass(bottomRight, "component-bottom-right");
		
		panel.add(topPanel);
		panel.add(center);
		panel.add(bottom);
		
		initWidget(mainPanel);

		Aggregator.getInstance().getDragController().makeDraggable(this);
	}
	
	public void setTitle(Widget widget) {
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
}
