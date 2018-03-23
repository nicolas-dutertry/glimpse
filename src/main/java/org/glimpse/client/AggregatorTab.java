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

public class AggregatorTab extends Composite {
	private FlowPanel panel;
	
	public AggregatorTab() {
		panel = new FlowPanel();
		panel.setWidth("100%");
		panel.setStylePrimaryName("autoclear");
		
		initWidget(panel);
	}
	
	public AggregatorTab(int columnCount) {
		this();
		
		List<AggregatorColumn> columns = new LinkedList<AggregatorColumn>();
		for (int i = 0; i < columnCount; i++) {
			AggregatorColumn column = new AggregatorColumn();			
			columns.add(column);
		}
		setColumns(columns);
	}
	
	public void setColumns(List<AggregatorColumn> columns) {
		List<AggregatorColumn> oldColumns = getColumns();
		for (AggregatorColumn oldColumn : oldColumns) {
			oldColumn.makeNotDroppable();
		}
		
		panel.clear();
		for (AggregatorColumn column : columns) {
			panel.add(column);
			column.setWidth((100 / columns.size()) + "%");
			column.makeDroppable();
		}		
	}
	
	public List<AggregatorColumn> getColumns() {
		List<AggregatorColumn> columns = new LinkedList<AggregatorColumn>();
		for(int i = 0; i < panel.getWidgetCount(); i++) {
			columns.add((AggregatorColumn)panel.getWidget(i));
		}
		return columns;
	}
	
	public int getColumnCount() {
		return panel.getWidgetCount();
	}
	
	public int getColumnIndex(AggregatorColumn column) {
		return panel.getWidgetIndex(column);
	}
	
	public AggregatorColumn getColumn(int index) {
		return (AggregatorColumn)panel.getWidget(index);
	}
	
	public int getColumnIndex(Component component) {
		for(int i = 0; i < getColumnCount(); i++) {
			AggregatorColumn column = getColumn(i);
			if(column.getComponentIndex(component) != -1) {
				return i;
			}
		}
		return -1;
	}
	
	public void setVisible(boolean visible) {
		super.setVisible(visible);
		
		if(visible) {
			for(int i = 0; i < getColumnCount(); i++) {
				AggregatorColumn column = getColumn(i);
				column.onTabActivated();
			}
		}
	}
}
