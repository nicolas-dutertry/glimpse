package org.glimpse.client;

import java.util.HashMap;
import java.util.Map;

import org.glimpse.client.Aggregator.Direction;
import org.glimpse.client.widgets.HorizontalPanelExt;
import org.glimpse.client.widgets.VerticalPanelExt;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;

public class Component extends Composite {
	private SimplePanel titlePanel;
	private SimplePanel contentPanel;
	private Map<String, String> properties;
	
	public Component(Map<String, String> properties) {
		this.properties = new HashMap<String, String>(properties);
		
		VerticalPanelExt panel = new VerticalPanelExt();
		panel.setWidth("100%");
		
		HorizontalPanelExt top = new HorizontalPanelExt();
		top.setWidth("100%");
		
		Label titleLeft = new Label(" ");
		top.add(titleLeft);
		top.setCellClass(titleLeft, "component-title-left");
		
		titlePanel = new SimplePanel();
		titlePanel.setWidth("100%");
		top.add(titlePanel);
		top.setCellClass(titlePanel, "component-title-content");
		
		HorizontalPanelExt actionsPanel = new HorizontalPanelExt();		
		Image closeButton = new Image();
		closeButton.setUrl("images/close.png");
		closeButton.addClickHandler(new ClickHandler() {			
			@Override
			public void onClick(ClickEvent event) {
				removeFromParent();
				Aggregator.getInstance().update();
			}
		});
		actionsPanel.add(closeButton);
		actionsPanel.setCellClass(closeButton, "component-action");
		
		top.add(actionsPanel);
		top.setCellClass(actionsPanel, "component-title-actions");
		
		Label titleRight = new Label(" ");
		top.add(titleRight);
		top.setCellClass(titleRight, "component-title-right");
		
		HorizontalPanelExt center = new HorizontalPanelExt();
		center.setWidth("100%");
		
		Label left = new Label(" ");
		center.add(left);
		center.setCellClass(left, "component-left");
		
		contentPanel = new SimplePanel();
		contentPanel.setWidth("100%");
		center.add(contentPanel);
		center.setCellClass(contentPanel, "component-content");
		
		Label right = new Label(" ");
		center.add(right);
		center.setCellClass(right, "component-right");
		
		HorizontalPanelExt bottom = new HorizontalPanelExt();
		bottom.setWidth("100%");
		
		Label bottomLeft = new Label(" ");
		bottom.add(bottomLeft);
		bottom.setCellClass(bottomLeft, "component-bottom-left");
		
		
		HorizontalPanel movePanel = new HorizontalPanel();
		Image moveLeft = new Image("images/left.png");
		moveLeft.addClickHandler(new ClickHandler() {			
			@Override
			public void onClick(ClickEvent event) {
				Aggregator.getInstance().moveComponent(Component.this,
						Direction.LEFT);				
			}
		});
		movePanel.add(moveLeft);
		Image moveRight = new Image("images/right.png");
		moveRight.addClickHandler(new ClickHandler() {			
			@Override
			public void onClick(ClickEvent event) {
				Aggregator.getInstance().moveComponent(Component.this,
						Direction.RIGHT);				
			}
		});
		movePanel.add(moveRight);
		Image moveUp = new Image("images/up.png");
		moveUp.addClickHandler(new ClickHandler() {			
			@Override
			public void onClick(ClickEvent event) {
				Aggregator.getInstance().moveComponent(Component.this,
						Direction.UP);				
			}
		});
		movePanel.add(moveUp);
		Image moveDown = new Image("images/down.png");
		moveDown.addClickHandler(new ClickHandler() {			
			@Override
			public void onClick(ClickEvent event) {
				Aggregator.getInstance().moveComponent(Component.this,
						Direction.DOWN);				
			}
		});
		movePanel.add(moveDown);
		
		bottom.add(movePanel);
		bottom.setCellClass(movePanel, "component-bottom-center");
		
		Label bottomRight = new Label(" ");
		bottom.add(bottomRight);
		bottom.setCellClass(bottomRight, "component-bottom-right");
		
		panel.add(top);
		panel.add(center);
		panel.add(bottom);
		
		initWidget(panel);
	}
	
	public void setTitle(Widget widget) {
		titlePanel.setWidget(widget);
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
}
