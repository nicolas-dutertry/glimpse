package org.glimpse.client;

import java.util.LinkedList;
import java.util.List;

import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Image;

public class AggregatorColumn extends Composite {
	private FlowPanel panel;
	public AggregatorColumn() {
		panel = new FlowPanel();
		panel.setStylePrimaryName("column");
		panel.add(new Image("images/p.png"));
		initWidget(panel);
	}
	
	public void add(Component component) {
		panel.add(component);
	}
	
	public boolean remove(Component component) {
		return panel.remove(component);
	}
	
	public List<Component> getComponents() {
		List<Component> components = new LinkedList<Component>();
		for(int i = 1; i < panel.getWidgetCount(); i++) {
			components.add((Component)panel.getWidget(i));
		}
		return components;
	}
	
	public int getComponentCount() {
		return panel.getWidgetCount()-1;
	}
	
	public Component getComponent(int index) {
		return (Component)panel.getWidget(index+1);
	}
	
	public int getComponentIndex(Component component) {
		int i = panel.getWidgetIndex(component);
		if(i < 0) {
			return i;
		} else {
			return i-1;
		}
	}
	
	public void insert(Component component, int beforeIndex) {
		panel.insert(component, beforeIndex+1);
	}
}
