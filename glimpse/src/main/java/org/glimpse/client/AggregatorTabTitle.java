package org.glimpse.client;

import org.glimpse.client.widgets.HorizontalPanelExt;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;

public class AggregatorTabTitle extends Composite implements ClickHandler {
	private HorizontalPanelExt panel;
	private Image left;
	private Label center;
	private Image options;
	private Image right;
	
	private AggregatorTabPanel tabPanel;
	
	private boolean selected = false;
	
	public AggregatorTabTitle(AggregatorTabPanel tabPanel, String text) {
		
		this.tabPanel = tabPanel;
		panel = new HorizontalPanelExt();
		panel.addClickHandler(this);
		
		left = new Image("images/p.png");
		panel.add(left);
		panel.setCellClass(left, "tabtitle-left");
		
		center = new Label(text);
		center.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				if(AggregatorTabTitle.this.isSelected()) {
					event.stopPropagation();
				}
			}
		});
		panel.add(center);
		panel.setCellClass(center, "tabtitle-center");
		
		options = new Image("images/p.png");
		options.setVisible(false);
		panel.add(options);
		panel.setCellClass(options, "tabtitle-options");
		
		right = new Image("images/p.png");
		panel.add(right);
		panel.setCellClass(right, "tabtitle-right");
		
		Image separator = new Image("images/p.png");
		panel.add(separator);
		panel.setCellClass(separator, "tabtitle-separator");
		
		initWidget(panel);
	}
	
	public void onClick(ClickEvent event) {
		if(selected) {
			tabPanel.toogleOptions();
		} else {
			int i = ((HorizontalPanel)getParent()).getWidgetIndex(AggregatorTabTitle.this);
			tabPanel.selectTab(i);
		}
	}
	
	public boolean isSelected() {
		return selected;
	}
	
	public void setSelected(boolean selected) {
		this.selected = selected;
		String classSuffix = "";
		if(selected) {
			classSuffix = "-selected";
		}
		
		panel.setCellClass(left, "tabtitle-left" + classSuffix);
		panel.setCellClass(center, "tabtitle-center" + classSuffix);
		panel.setCellClass(options, "tabtitle-options" + classSuffix);
		panel.setCellClass(right, "tabtitle-right" + classSuffix);
	}
	
	public String getText() {
		return center.getText();
	}
	
	public void setText(String text) {
		center.setText(text);
	}
	
}
