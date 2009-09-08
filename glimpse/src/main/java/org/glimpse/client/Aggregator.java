package org.glimpse.client;

import java.util.List;

import org.glimpse.client.layout.ColumnDescription;
import org.glimpse.client.layout.ComponentDescription;
import org.glimpse.client.layout.PageDescription;
import org.glimpse.client.layout.TabDescription;
import org.glimpse.client.layout.ComponentDescription.Type;
import org.glimpse.client.news.NewsReader;
import org.glimpse.client.widgets.VerticalPanelExt;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class Aggregator implements EntryPoint {
	public enum Direction {
		LEFT,
		RIGHT,
		UP,
		DOWN
	}
	
	/**
	 * The message displayed to the user when the server cannot be reached or
	 * returns an error.
	 */
	private static final String SERVER_ERROR = "An error occurred while "
			+ "attempting to contact the server. Please check your network "
			+ "connection and try again.";
	
	private static Aggregator instance;

	/**
	 * Create a remote service proxy to talk to the server-side service.
	 */	
	private PageDescriptionServiceAsync pageDescriptionService = GWT
		.create(PageDescriptionService.class);
	
	private AggregatorTabPanel tabPanel;


	/**
	 * This is the entry point method.
	 */
	public void onModuleLoad() {
		instance = this;
		
		pageDescriptionService.getPageDescription(
				new AsyncCallback<PageDescription>() {
					@Override
					public void onFailure(Throwable caught) {
						Window.alert(SERVER_ERROR);
					}

					@Override
					public void onSuccess(PageDescription pageDescription) {
						load(pageDescription);
					}					
		});
	}
	
	private void load(PageDescription pageDescription) {
		RootPanel.get("main").clear();
		
		VerticalPanel mainPanel = new VerticalPanel();
		mainPanel.setWidth("100%");
		Button addButton = new Button("Add");
		addButton.addClickHandler(new ClickHandler() {			
			@Override
			public void onClick(ClickEvent event) {
				NewsReader rssReader = new NewsReader();
				addComponent(rssReader);
			}
		});
		mainPanel.add(addButton);
		
		tabPanel = new AggregatorTabPanel();
		tabPanel.setWidth("100%");		
		
		List<TabDescription> tabDescriptions =
			pageDescription.getTabDescriptions();
		for (TabDescription tabDescription : tabDescriptions) {
			HorizontalPanel panel = new HorizontalPanel();
			panel.setWidth("100%");
			
			List<ColumnDescription> columnDescriptions =
				tabDescription.getColumnDescriptions();
			for (ColumnDescription columnDescription : columnDescriptions) {
				VerticalPanelExt column = new VerticalPanelExt();
				column.setWidth("100%");
				
				List<ComponentDescription> componentDescriptions =
					columnDescription.getComponentDescriptions();
				for (ComponentDescription componentDescription : componentDescriptions) {
					if(componentDescription.getType().equals(Type.NEWS)) {
						NewsReader rssReader = new NewsReader(componentDescription.getProperties()); 
						column.add(rssReader);
						column.setCellClass(rssReader, "component");
					}
				}
				
				panel.add(column);
				panel.setCellWidth(column, (100 / columnDescriptions.size()) + "%");
			}
			tabPanel.add(panel, tabDescription.getTitle());
		}
		tabPanel.selectTab(0);
		
		mainPanel.add(tabPanel);
		RootPanel.get("main").add(mainPanel);
	}
	
	public void update() {
		PageDescription pageDescription = generatePageDescription();
		pageDescriptionService.setPageDescription(pageDescription, new AsyncCallback<Void>() {
			@Override
			public void onFailure(Throwable caught) {
				Window.alert(SERVER_ERROR);
			}

			@Override
			public void onSuccess(Void result) {
			}			
		});
	}
	
	private PageDescription generatePageDescription() {
		PageDescription pageDescription = new PageDescription();
		
		for(int i = 0; i < tabPanel.getWidgetCount(); i++) {
			HorizontalPanel panel = (HorizontalPanel)tabPanel.getWidget(i);
			TabDescription tabDescription = new TabDescription();
			tabDescription.setTitle(tabPanel.getTitle(i));
			
			for(int j = 0; j < panel.getWidgetCount(); j++) {
				VerticalPanel column = (VerticalPanel)panel.getWidget(j);
				ColumnDescription columnDescription = new ColumnDescription();
				
				for(int k = 0; k < column.getWidgetCount(); k++) {
					Component component = (Component)column.getWidget(k);
					ComponentDescription componentDescription = new ComponentDescription();
					// TODO component type
					componentDescription.setProperties(component.getProperties());
					columnDescription.addComponentDescription(componentDescription);
				}				
				tabDescription.addColumnDescription(columnDescription);
			}			
			pageDescription.addTabDescription(tabDescription);
		}
		
		return pageDescription;
	}
	
	public static Aggregator getInstance() {
		return instance;
	}
	
	public void addComponent(Component component) {
		HorizontalPanel tab = (HorizontalPanel)tabPanel.getWidget(
				tabPanel.getVisibleWidget());
		VerticalPanelExt column = (VerticalPanelExt)tab.getWidget(0);
		column.add(component);
		column.setCellClass(component, "component");
	}
	
	public void moveComponent(Component component, Direction direction) {
		HorizontalPanel tab = (HorizontalPanel)tabPanel.getWidget(
				tabPanel.getVisibleWidget());
		VerticalPanelExt column = getColumn(tab, component);
		int col = tab.getWidgetIndex(column);
		int row = column.getWidgetIndex(component);
		boolean moved = false;
		if(direction == Direction.RIGHT) {
			if(col+1 < tab.getWidgetCount()) {
				column.remove(component);
				VerticalPanelExt rightColumn = (VerticalPanelExt)tab.getWidget(col+1);
				rightColumn.add(component);
				moved = true;
			}
		} else if(direction == Direction.LEFT) {
			if(col-1 >= 0) {
				column.remove(component);
				VerticalPanelExt leftColumn = (VerticalPanelExt)tab.getWidget(col-1);
				leftColumn.add(component);
				moved = true;
			}
		} else if(direction == Direction.UP) {
			if(row-1 >= 0) {
				column.remove(component);
				column.insert(component, row-1);
				moved = true;
			}
		}  else if(direction == Direction.DOWN) {
			if(row+1 < column.getWidgetCount()) {
				column.remove(component);
				column.insert(component, row+1);
				moved = true;
			}
		}
		if(moved) {
			column = getColumn(tab, component);
			column.setCellClass(component, "component");
			update();
		}
	}
	
	public VerticalPanelExt getColumn(HorizontalPanel tab,
			Component component) {
		for(int i = 0; i < tab.getWidgetCount(); i++) {
			VerticalPanelExt col = (VerticalPanelExt)tab.getWidget(i);
			if(col.getWidgetIndex(component) != -1) {
				return col;
			}
		}
		return null;
	}
}
