package org.glimpse.client;

import org.glimpse.client.widgets.HorizontalPanelExt;
import org.glimpse.client.widgets.VerticalPanelExt;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DeckPanel;

public class AggregatorTabPanel extends Composite {
	private VerticalPanelExt panel;
	private HorizontalPanelExt tabTitles;
	private AggregatorTabTitle currentTabTitle;
	private DeckPanel optionsPanel;
	private DeckPanel deck;
	public AggregatorTabPanel() {
		panel = new VerticalPanelExt();
		tabTitles = new HorizontalPanelExt();
		Anchor add = new Anchor("New tab", "javascript:void(0)");
		add.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				AggregatorTab tab = new AggregatorTab(3);
				add(tab, "new tab");
				Aggregator.getInstance().update();
				selectTab(getTabCount()-1);
			}
		});
		tabTitles.add(add);
		tabTitles.setCellClass(add, "tabtitles-add");
		
		optionsPanel = new DeckPanel();
		deck = new DeckPanel();
		
		panel.add(tabTitles);
		panel.setCellClass(tabTitles, "tabtitles");
		panel.add(deck);
		panel.setCellClass(deck, "tabcontent");
		
		initWidget(panel);
	}
	
	public void add(AggregatorTab tab, String title) {
		AggregatorTabTitle tabTitle = new AggregatorTabTitle(this, title);
		tabTitles.insert(tabTitle, tabTitles.getWidgetCount()-1);
		optionsPanel.add(new AggregatorTabOptions(this));
		deck.add(tab);
	}
	
	public void selectTab(int index) {
		AggregatorTabTitle newCurrent = (AggregatorTabTitle)tabTitles.getWidget(index);
		if(newCurrent != currentTabTitle) {
			if(currentTabTitle != null) {
				currentTabTitle.setSelected(false);
			}
			panel.remove(optionsPanel);
			newCurrent.setSelected(true);
			optionsPanel.showWidget(index);
			deck.showWidget(index);
			currentTabTitle = newCurrent;
		}
	}
	
	public int getVisibleTab() {
		return deck.getVisibleWidget();
	}
	
	public int getTabCount() {
		return deck.getWidgetCount();
	}

	public AggregatorTab getTab(int index) {
		return (AggregatorTab)deck.getWidget(index);
	}

	public int getTabIndex(AggregatorTab tab) {
		return deck.getWidgetIndex(tab);
	}

	public void remove(int index) {
		if(index >= getTabCount()) {
			return;
		}
		panel.remove(optionsPanel);
		tabTitles.remove(index);
		optionsPanel.remove(index);
		deck.remove(index);
		
		if(index - 1 > 0) {
			selectTab(index-1);
		} else if(getTabCount() > 0) {
			selectTab(0);
		}
	}
	
	public String getTitle(int index) {
		return ((AggregatorTabTitle)tabTitles.getWidget(index)).getText();
	}
	
	public void setTitle(int index, String title) {
		((AggregatorTabTitle)tabTitles.getWidget(index)).setText(title);
	}
	
	public void toogleOptions() {
		if(!panel.remove(optionsPanel)) {
			AggregatorTabOptions options =
				(AggregatorTabOptions)optionsPanel.getWidget(optionsPanel.getVisibleWidget());
			options.reinit();
			panel.insert(optionsPanel, panel.getWidgetIndex(deck));
			panel.setCellClass(optionsPanel, "taboptions");
		}
	}
}
