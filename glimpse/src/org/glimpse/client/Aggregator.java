package org.glimpse.client;

import java.util.LinkedList;
import java.util.List;

import org.glimpse.client.layout.ColumnDescription;
import org.glimpse.client.layout.ComponentDescription;
import org.glimpse.client.layout.PageDescription;
import org.glimpse.client.layout.TabDescription;
import org.glimpse.client.layout.ComponentDescription.Type;
import org.glimpse.client.news.NewsReader;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.TabPanel;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class Aggregator implements EntryPoint {
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
	
	private TabPanel tabPanel;
	private List<TabTitle> tabTitles;


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
				int i = tabPanel.getTabBar().getSelectedTab();
				HorizontalPanel tab = (HorizontalPanel)tabPanel.getWidget(i);
				VerticalPanelExt column = (VerticalPanelExt)tab.getWidget(0);
				NewsReader rssReader = new NewsReader(); 
				column.add(rssReader);
				column.setCellClass(rssReader, "component");
			}
		});
		mainPanel.add(addButton);
		
		tabPanel = new TabPanel();
		tabPanel.setWidth("100%");
		tabTitles = new LinkedList<TabTitle>();
		
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
			TabTitle title = new TabTitle(tabDescription.getTitle());
			tabPanel.add(panel, title);
			tabTitles.add(title);
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
			TabTitle tabTitle = tabTitles.get(i);
			HorizontalPanel panel = (HorizontalPanel)tabPanel.getWidget(i);
			TabDescription tabDescription = new TabDescription();
			tabDescription.setTitle(tabTitle.getText());
			
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
}
