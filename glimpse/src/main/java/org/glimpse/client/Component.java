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
		
		Image titleLeft = new Image("images/p.png");
		topPanel.add(titleLeft);
		topPanel.setCellClass(titleLeft, "component-title-left");
		
		titlePanel = new SimplePanel();
		topPanel.add(titlePanel);
		topPanel.setCellClass(titlePanel, "component-title-content");
				
		actionsPanel = new HorizontalPanelExt();
		actionsPanel.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
		dragHandle = new Image("images/move.png");
		actionsPanel.add(dragHandle);
		actionsPanel.setCellClass(dragHandle, "component-action");
		
		Image deleteButton = new Image();
		deleteButton.setUrl("images/close.png");
		deleteButton.setTitle(constants.delete());
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
	
	public abstract Type getType();
}
