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

import java.util.LinkedList;
import java.util.List;

import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Widget;

public class AggregatorColumn extends Composite {
	public class Panel extends FlowPanel {

		@Override
		public void add(Widget w) {
			super.add(w);
			checkHeight();
		}

		@Override
		public void insert(Widget w, int beforeIndex) {
			super.insert(w, beforeIndex);
			checkHeight();
		}

		@Override
		public boolean remove(Widget w) {
			boolean b =  super.remove(w);
			checkHeight();
			return b;
		}
		
		private void checkHeight() {
			if(getWidgetCount() < 2) {
				setHeight("200px");
			} else {
				setHeight("");
			}
		}
		
	}
	
	private Panel panel;
	private AggregatorColumnDropController dropController;
	
	public AggregatorColumn() {
		panel = new Panel();
		panel.setHeight("200px");
		panel.setStylePrimaryName("column");
		panel.add(new Image(Aggregator.TRANSPARENT_IMAGE));
		
		dropController = new AggregatorColumnDropController(panel);

		initWidget(panel);		
		
	}
	
	public void makeDroppable() {
		Aggregator.getInstance().getDragController().registerDropController(dropController);
	}
	
	public void makeNotDroppable() {
		Aggregator.getInstance().getDragController().unregisterDropController(dropController);
	}
	
	public void add(Component component) {
		panel.add(component);
		if(Aggregator.getInstance().isModifiable()) {
			Aggregator.getInstance().getDragController().makeDraggable(component);
		}
	}
	
	public boolean remove(Component component) {
		boolean b = panel.remove(component);
		if(b) {
			Aggregator.getInstance().getDragController().makeNotDraggable(component);
		}
		return b;
	}
	
	public void insert(Component component, int beforeIndex) {
		panel.insert(component, beforeIndex+1);
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
	
	void onTabActivated() {
		for(int i = 0; i < getComponentCount(); i++) {
			Component component = getComponent(i);
			component.onTabActivated();
		}
	}
}
