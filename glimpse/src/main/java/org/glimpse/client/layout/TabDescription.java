package org.glimpse.client.layout;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class TabDescription implements Serializable {
	private static final long serialVersionUID = 1L;
	
	private String title;
	private List<ColumnDescription> columnDescriptions = new ArrayList<ColumnDescription>();
	
	public String getTitle() {
		return title;
	}
	
	public void setTitle(String title) {
		this.title = title;
	}
	
	public List<ColumnDescription> getColumnDescriptions() {
		return new ArrayList<ColumnDescription>(columnDescriptions);
	}
	
	public void addColumnDescription(ColumnDescription column) {
		columnDescriptions.add(column);
	}
	
	public void removeColumnDescription(ColumnDescription column) {
		columnDescriptions.remove(column);
	}

}
