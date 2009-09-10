package org.glimpse.client;

import java.util.LinkedList;
import java.util.List;

import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.SimplePanel;

public class AggregatorTab extends Composite {
	private FlowPanel panel;
	
	public AggregatorTab() {
		panel = new FlowPanel();
		panel.setWidth("100%");
		
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
		panel.clear();
		for (AggregatorColumn column : columns) {
			panel.add(column);
			column.setWidth((100 / columns.size()) + "%");
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
}