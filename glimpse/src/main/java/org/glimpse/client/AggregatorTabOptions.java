package org.glimpse.client;

import java.util.List;

import org.glimpse.client.widgets.HorizontalPanelExt;
import org.glimpse.client.widgets.VerticalPanelExt;

import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DeckPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.TextBox;

public class AggregatorTabOptions extends Composite {
	private VerticalPanelExt panel;
	private TextBox titleInput;
	private ListBox columnList;
	private AggregatorTabPanel tabPanel;
	public AggregatorTabOptions(AggregatorTabPanel tabPanel) {
		this.tabPanel = tabPanel;
		
		panel = new VerticalPanelExt();
		
		HorizontalPanelExt titlePanel = new HorizontalPanelExt();
		titlePanel.add(new Label("Title"));
		titleInput = new TextBox();
		titlePanel.add(titleInput);
		Button titleButton = new Button("OK");
		titleButton.addClickHandler(new ClickHandler() {			
			public void onClick(ClickEvent event) {
				AggregatorTabOptions.this.tabPanel.setTitle(
						getIndex(), titleInput.getValue());
				Aggregator.getInstance().update();
			}
		});
		titlePanel.add(titleButton);
		panel.add(titlePanel);
		
		HorizontalPanelExt columnPanel = new HorizontalPanelExt();
		columnPanel.add(new Label("Columns' number"));
		columnList = new ListBox();
		columnList.addItem("1");
		columnList.addItem("2");
		columnList.addItem("3");
		columnList.addItem("4");
		columnList.addChangeHandler(new ChangeHandler() {
			public void onChange(ChangeEvent event) {
				AggregatorTab tab = AggregatorTabOptions.this.tabPanel.getTab(getIndex());
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
		columnPanel.add(columnList);
		panel.add(columnPanel);
		
		Button delButton = new Button("Delete this tab");
		delButton.addClickHandler(new ClickHandler() {			
			public void onClick(ClickEvent event) {
				if(Window.confirm("Are you sure you want to delete this tab ?")) {
					AggregatorTabOptions.this.tabPanel.remove(getIndex());
					Aggregator.getInstance().update();
				}
			}
		});
		panel.add(delButton);
		
		initWidget(panel);
	}
	
	public int getIndex() {
		return ((DeckPanel)getParent()).getWidgetIndex(this);
	}
	
	public void reinit() {
		int index = getIndex();
		
		String title = tabPanel.getTitle(index);
		titleInput.setValue(title);
		
		AggregatorTab tab = tabPanel.getTab(index);
		columnList.setItemSelected(tab.getColumnCount()-1, true);		
	}
}
