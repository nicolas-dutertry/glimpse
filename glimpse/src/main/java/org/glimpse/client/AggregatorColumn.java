package org.glimpse.client;

import java.util.LinkedList;
import java.util.List;

import org.glimpse.client.widgets.VerticalPanelExt;

import com.google.gwt.user.client.ui.Composite;

public class AggregatorColumn extends Composite {
	private VerticalPanelExt panel;
	public AggregatorColumn() {
		panel = new VerticalPanelExt();
		panel.setWidth("100%");
		
		initWidget(panel);
	}
	
	public void add(Component component) {
		panel.add(component);
		panel.setCellClass(component, "component");
	}
	
	public boolean remove(Component component) {
		return panel.remove(component);
	}
	
	public List<Component> getComponents() {
		List<Component> components = new LinkedList<Component>();
		for(int i = 0; i < panel.getWidgetCount(); i++) {
			components.add((Component)panel.getWidget(i));
		}
		return components;
	}
	
	public int getComponentCount() {
		return panel.getWidgetCount();
	}
	
	public Component getComponent(int index) {
		return (Component)panel.getWidget(index);
	}
	
	public int getComponentIndex(Component component) {
		return panel.getWidgetIndex(component);
	}
	
	public void insert(Component component, int beforeIndex) {
		panel.insert(component, beforeIndex);
	}
}
