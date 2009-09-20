package org.glimpse.client;

import java.util.List;

import org.glimpse.client.i18n.AggregatorConstants;
import org.glimpse.client.widgets.HorizontalPanelExt;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;

public class AggregatorTabOptions extends Composite {
	private TextBox titleInput;
	private ListBox columnList;
	private AggregatorTabPanel tabPanel;
	private AggregatorConstants constants = GWT.create(AggregatorConstants.class);
	
	public AggregatorTabOptions(AggregatorTabPanel tabPanel) {
		this.tabPanel = tabPanel;
		
		FlowPanel mainPanel = new FlowPanel();
		mainPanel.setWidth("100%");
		SimplePanel closePanel = new SimplePanel();
		closePanel.setStylePrimaryName("taboptions-close");
		mainPanel.add(closePanel);
		FocusPanel closeButton = new FocusPanel();		
		closePanel.add(closeButton);
		closeButton.setWidget(new Image("images/p.png"));
		closeButton.setStylePrimaryName("taboptions-close-button");
		closeButton.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				AggregatorTabOptions.this.tabPanel.hideOptions();
			}
		});
		
		HorizontalPanelExt panel = new HorizontalPanelExt();
		mainPanel.add(panel);
		panel.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
		
		HorizontalPanel movePanel = new HorizontalPanel();
		movePanel.setSpacing(0);
		panel.add(movePanel);
		FocusPanel moveLeft = new FocusPanel();
		movePanel.add(moveLeft);
		moveLeft.setWidget(new Image("images/p.png"));
		moveLeft.setStylePrimaryName("move-tab-left");
		moveLeft.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				AggregatorTabOptions.this.tabPanel.moveVisibleLeft();
				Aggregator.getInstance().update();
			}
		});
		FocusPanel moveRight = new FocusPanel();
		movePanel.add(moveRight);
		moveRight.setWidget(new Image("images/p.png"));
		moveRight.setStylePrimaryName("move-tab-right");
		moveRight.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				AggregatorTabOptions.this.tabPanel.moveVisibleRight();
				Aggregator.getInstance().update();
			}
		});
		
		FlexTable titleTable = new FlexTable();
		titleTable.setText(0, 0, constants.title());
		titleInput = new TextBox();
		titleTable.setWidget(0, 1, titleInput);
		Button titleButton = new Button(constants.ok());
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
		columnTable.setText(0, 0, constants.numberOfColumns());
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
				if(confirm && !Window.confirm(constants.removeColumnWarning())) {
					return;
				}
				tab.setColumns(columns);
				Aggregator.getInstance().update();
			}
		});
		columnTable.setWidget(0, 1, columnList);
		panel.add(columnTable);
		panel.setCellVerticalAlignment(columnTable, VerticalPanel.ALIGN_MIDDLE);
		
		Button delButton = new Button(constants.deleteTab());
		delButton.addClickHandler(new ClickHandler() {			
			public void onClick(ClickEvent event) {
				if(Window.confirm(constants.deleteTabConfirm())) {
					AggregatorTabOptions.this.tabPanel.remove(
							AggregatorTabOptions.this.tabPanel.getVisibleTab());
					Aggregator.getInstance().update();
				}
			}
		});
		panel.add(delButton);
		panel.setCellVerticalAlignment(delButton, VerticalPanel.ALIGN_MIDDLE);
		
		initWidget(mainPanel);
	}
	
	public void reinit() {
		int index = tabPanel.getVisibleTab();
		
		String title = tabPanel.getTitle(index);
		titleInput.setValue(title);
		
		AggregatorTab tab = tabPanel.getTab(index);
		columnList.setItemSelected(tab.getColumnCount()-1, true);		
	}
}
