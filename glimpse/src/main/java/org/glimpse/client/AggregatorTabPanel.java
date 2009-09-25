package org.glimpse.client;

import java.util.LinkedList;

import org.glimpse.client.i18n.AggregatorConstants;
import org.glimpse.client.widgets.HorizontalPanelExt;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.SimplePanel;

public class AggregatorTabPanel extends Composite {
	private FlowPanel panel;
	private HorizontalPanelExt tabTitles;
	private AggregatorTabTitle currentTabTitle;
	private SimplePanel optionsPanel;
	
	private FlowPanel tabContentsPanel;
	private AggregatorTab visibleTabContent;
	
	private AggregatorConstants constants =
		GWT.create(AggregatorConstants.class);
	
	public AggregatorTabPanel() {
		panel = new FlowPanel();
		
		// Tab titles
		FlowPanel tabTitlesPanel = new FlowPanel();
		tabTitlesPanel.setStylePrimaryName("tabtitles");
		
		tabTitles = new HorizontalPanelExt();		
		Anchor add = new Anchor(constants.newTab(), "javascript:void(0)");
		add.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				AggregatorTab tab = new AggregatorTab(3);
				add(tab, constants.newTab());
				Aggregator.getInstance().update();
				selectTab(getTabCount()-1);
				showOptions();
			}
		});
		tabTitles.add(add);
		tabTitles.setCellClass(add, "tabtitles-add");
		
		tabTitlesPanel.add(tabTitles);
		panel.add(tabTitlesPanel);
		
		// Tab options (not added at startup)
		optionsPanel = new SimplePanel();
		optionsPanel.add(new AggregatorTabOptions(this));
		optionsPanel.setStylePrimaryName("taboptions");
		
		// Tab content
		tabContentsPanel = new FlowPanel();
		tabContentsPanel.setStylePrimaryName("tabcontent");
		panel.add(tabContentsPanel);
		
		initWidget(panel);
	}
	
	public void add(AggregatorTab tab, String title) {
		AggregatorTabTitle tabTitle = new AggregatorTabTitle(this, title);
		tabTitles.insert(tabTitle, tabTitles.getWidgetCount()-1);
		
		tab.setVisible(false);
		tabContentsPanel.add(tab);
	}
	
	public void selectTab(int index) {
		AggregatorTabTitle newCurrent = (AggregatorTabTitle)tabTitles.getWidget(index);
		if(newCurrent != currentTabTitle) {
			if(currentTabTitle != null) {
				currentTabTitle.setSelected(false);
			}
			panel.remove(optionsPanel);
			newCurrent.setSelected(true);
			currentTabTitle = newCurrent;
			
			if(visibleTabContent != null) {
				visibleTabContent.setVisible(false);
			}
			visibleTabContent = (AggregatorTab)tabContentsPanel.getWidget(index);
			visibleTabContent.setVisible(true);
		}
	}
	
	public int getVisibleTab() {
		if(visibleTabContent != null) {
			return tabContentsPanel.getWidgetIndex(visibleTabContent);
		} else {
			return -1;
		}
	}
	
	public int getTabCount() {
		return tabContentsPanel.getWidgetCount();
	}

	public AggregatorTab getTab(int index) {
		return (AggregatorTab)tabContentsPanel.getWidget(index);
	}

	public int getTabIndex(AggregatorTab tab) {
		return tabContentsPanel.getWidgetIndex(tab);
	}

	public void remove(int index) {
		if(index >= getTabCount()) {
			return;
		}
		
		AggregatorTab tab = getTab(index);
		tab.setColumns(new LinkedList<AggregatorColumn>());
		
		panel.remove(optionsPanel);
		tabTitles.remove(index);
		tabContentsPanel.remove(index);
		
		if(index - 1 > 0) {
			selectTab(index-1);
		} else if(getTabCount() > 0) {
			selectTab(0);
		}
	}
	
	public void moveVisibleLeft() {
		int index = getVisibleTab();
		if(index <= 0) {
			return;
		}		
		
		tabTitles.remove(index);
		tabTitles.insert(currentTabTitle, index-1);
		tabContentsPanel.remove(index);
		tabContentsPanel.insert(visibleTabContent, index-1);
	}
	
	public void moveVisibleRight() {
		int index = getVisibleTab();
		if(index >= getTabCount()-1) {
			return;
		}		
		
		tabTitles.remove(index);
		tabTitles.insert(currentTabTitle, index+1);
		tabContentsPanel.remove(index);
		tabContentsPanel.insert(visibleTabContent, index+1);
	}
	
	public String getTitle(int index) {
		return ((AggregatorTabTitle)tabTitles.getWidget(index)).getText();
	}
	
	public void setTitle(int index, String title) {
		((AggregatorTabTitle)tabTitles.getWidget(index)).setText(title);
	}
	
	public void showOptions() {
		if(!isOptionsVisible()) {
			AggregatorTabOptions options =
				(AggregatorTabOptions)optionsPanel.getWidget();
			options.reinit();
			panel.insert(optionsPanel, panel.getWidgetIndex(tabContentsPanel));
		}
	}
	
	public void hideOptions() {
		panel.remove(optionsPanel);
	}
	
	public void toogleOptions() {
		if(isOptionsVisible()) {
			hideOptions();
		} else {
			showOptions();
		}
	}
	
	public boolean isOptionsVisible() {
		return panel.getWidgetIndex(optionsPanel) != -1;
	}
}
