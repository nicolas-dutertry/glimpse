package org.glimpse.client;

import java.util.List;

import org.glimpse.client.widgets.HorizontalPanelExt;

import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;

public class AggregatorTabOptions extends Composite {
	private TextBox titleInput;
	private ListBox columnList;
	private AggregatorTabPanel tabPanel;
	public AggregatorTabOptions(AggregatorTabPanel tabPanel) {
		this.tabPanel = tabPanel;
		
		HorizontalPanelExt panel = new HorizontalPanelExt();
		
		FlexTable titleTable = new FlexTable();
		titleTable.setText(0, 0, "Title");
		titleInput = new TextBox();
		titleTable.setWidget(0, 1, titleInput);
		Button titleButton = new Button("OK");
		titleButton.addClickHandler(new ClickHandler() {			
			public void onClick(ClickEvent event) {
				AggregatorTabOptions.this.tabPanel.setTitle(
						AggregatorTabOptions.this.tabPanel.getVisibleTab(),
						titleInput.getValue());
				Aggregator.getInstance().update();
			}
		});
		titleTable.setWidget(0, 2, titleButton);
		panel.add(titleTable);
		panel.setCellVerticalAlignment(titleTable, VerticalPanel.ALIGN_MIDDLE);
		
		FlexTable columnTable = new FlexTable();
		columnTable.setText(0, 0, "Columns' number");
		columnList = new ListBox();
		columnList.addItem("1");
		columnList.addItem("2");
		columnList.addItem("3");
		columnList.addItem("4");
		columnList.addChangeHandler(new ChangeHandler() {
			public void onChange(ChangeEvent event) {
				AggregatorTab tab = AggregatorTabOptions.this.tabPanel.getTab(
						AggregatorTabOptions.this.tabPanel.getVisibleTab());
				int newNumber = Integer.valueOf(columnList.getValue(columnList.getSelectedIndex()));
				List<AggregatorColumn> columns = tab.getColumns();
				boolean confirm = false;
				while (columns.size() < newNumber) {
					columns.add(new AggregatorColumn());
				}
				while (columns.size() > newNumber) {
					AggregatorColumn removed = columns.remove(columns.size() -1);
					if(removed.getComponentCount() > 0) {
						confirm = true;
					}
				}
				if(confirm && !Window.confirm("Some component will be lost. Do you want to proceed anyway ?")) {
					return;
				}
				tab.setColumns(columns);
				Aggregator.getInstance().update();
			}
		});
		columnTable.setWidget(0, 1, columnList);
		panel.add(columnTable);
		panel.setCellVerticalAlignment(columnTable, VerticalPanel.ALIGN_MIDDLE);
		
		Button delButton = new Button("Delete this tab");
		delButton.addClickHandler(new ClickHandler() {			
			public void onClick(ClickEvent event) {
				if(Window.confirm("Are you sure you want to delete this tab ?")) {
					AggregatorTabOptions.this.tabPanel.remove(
							AggregatorTabOptions.this.tabPanel.getVisibleTab());
					Aggregator.getInstance().update();
				}
			}
		});
		panel.add(delButton);
		panel.setCellVerticalAlignment(delButton, VerticalPanel.ALIGN_MIDDLE);
		
		initWidget(panel);
	}
	
	public void reinit() {
		int index = tabPanel.getVisibleTab();
		
		String title = tabPanel.getTitle(index);
		titleInput.setValue(title);
		
		AggregatorTab tab = tabPanel.getTab(index);
		columnList.setItemSelected(tab.getColumnCount()-1, true);		
	}
}
