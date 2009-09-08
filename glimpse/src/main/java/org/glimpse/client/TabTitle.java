package org.glimpse.client;

import org.glimpse.client.widgets.HorizontalPanelExt;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;

public class TabTitle extends Composite implements ClickHandler {
	private Label center;
	private AggregatorTabPanel tabPanel;
	
	public TabTitle(AggregatorTabPanel tabPanel, String text) {
		this.tabPanel = tabPanel;
		HorizontalPanelExt panel = new HorizontalPanelExt();
		Label left = new Label(" ");
		panel.add(left);
		
		center = new Label(text);
		center.addClickHandler(this);
		panel.add(center);
		
		Image optionsImage = new Image("images/tab-options.png");
		optionsImage.setVisible(false);
		panel.add(optionsImage);
		
		Label right = new Label(" ");
		panel.add(right);
		
		initWidget(panel);
	}
	
	public void onClick(ClickEvent event) {
		int i = ((HorizontalPanel)getParent()).getWidgetIndex(TabTitle.this);
		tabPanel.selectTab(i);
	}
	
	public void setSelected(boolean selected) {
		
	}
	
	public String getText() {
		return center.getText();
	}
	
}
