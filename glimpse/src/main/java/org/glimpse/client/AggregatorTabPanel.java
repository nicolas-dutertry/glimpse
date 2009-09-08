package org.glimpse.client;

import org.glimpse.client.widgets.HorizontalPanelExt;
import org.glimpse.client.widgets.VerticalPanelExt;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DeckPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.IndexedPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;

public class AggregatorTabPanel extends Composite implements IndexedPanel {
	private HorizontalPanelExt tabTitles;
	private TabTitle currentTabTitle;
	private DeckPanel optionsPanel;
	private DeckPanel deck;
	public AggregatorTabPanel() {
		VerticalPanelExt panel = new VerticalPanelExt();
		tabTitles = new HorizontalPanelExt();
		Anchor add = new Anchor("add new tab", "javascript:void(0)");
		add.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				HorizontalPanel panel = new HorizontalPanel();
				panel.setWidth("100%");
				
				for (int i = 0; i < 3; i++) {
					VerticalPanelExt column = new VerticalPanelExt();
					column.setWidth("100%");
					
					panel.add(column);
					panel.setCellWidth(column, (100 / 3) + "%");
				}
				add(panel, "new tab");
				Aggregator.getInstance().update();
			}
		});
		tabTitles.add(add);
		
		optionsPanel = new DeckPanel();
		optionsPanel.setVisible(false);
		deck = new DeckPanel();
		
		panel.add(tabTitles);
		panel.add(optionsPanel);
		panel.add(deck);
		
		initWidget(panel);
	}
	
	public void add(Widget w, String title) {
		TabTitle tabTitle = new TabTitle(this, title);
		tabTitles.insert(tabTitle, tabTitles.getWidgetCount()-1);
		optionsPanel.add(new SimplePanel());
		deck.add(w);
	}
	
	public void selectTab(int index) {
		TabTitle newCurrent = (TabTitle)tabTitles.getWidget(index);
		if(newCurrent != currentTabTitle) {
			if(currentTabTitle != null) {
				currentTabTitle.setSelected(false);
			}
			optionsPanel.setVisible(false);
			newCurrent.setSelected(true);
			optionsPanel.showWidget(index);
			deck.showWidget(index);
		}
	}
	
	public int getVisibleWidget() {
		return deck.getVisibleWidget();
	}
	
	public int getWidgetCount() {
		return deck.getWidgetCount();
	}

	public Widget getWidget(int index) {
		return deck.getWidget(index);
	}

	public int getWidgetIndex(Widget child) {
		return deck.getWidgetIndex(child);
	}

	public boolean remove(int index) {
		if(index >= getWidgetCount()) {
			return false;
		}
		tabTitles.remove(index);
		optionsPanel.remove(index);
		return deck.remove(index);
	}
	
	public String getTitle(int index) {
		return ((TabTitle)tabTitles.getWidget(index)).getText();
	}
}
