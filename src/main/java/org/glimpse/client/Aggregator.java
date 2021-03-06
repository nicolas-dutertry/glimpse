/*
 * Copyright (C) 2009 Nicolas Dutertry
 * 
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
package org.glimpse.client;

import java.util.LinkedList;
import java.util.List;

import org.glimpse.client.finance.QuotationComponent;
import org.glimpse.client.i18n.AggregatorConstants;
import org.glimpse.client.i18n.AggregatorMessages;
import org.glimpse.client.layout.ColumnDescription;
import org.glimpse.client.layout.ComponentDescription;
import org.glimpse.client.layout.PageDescription;
import org.glimpse.client.layout.TabDescription;
import org.glimpse.client.news.NewsReader;

import com.allen_sauer.gwt.dnd.client.DragEndEvent;
import com.allen_sauer.gwt.dnd.client.DragHandler;
import com.allen_sauer.gwt.dnd.client.DragStartEvent;
import com.allen_sauer.gwt.dnd.client.PickupDragController;
import com.allen_sauer.gwt.dnd.client.VetoDragException;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.Hidden;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.RootPanel;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class Aggregator implements EntryPoint, DragHandler {
	/**
	 * The message displayed to the user when the server cannot be reached or
	 * returns an error.
	 */
	public static final String SERVER_ERROR = "An error occurred while "
			+ "attempting to contact the server. Please check your network "
			+ "connection and try again.";
	
	public static final String TRANSPARENT_IMAGE = "images/p.gif";
	
	private static Aggregator instance;

	/**
	 * Create a remote service proxy to talk to the server-side service.
	 */
	private LoginServiceAsync loginService = GWT.create(LoginService.class);
	private UserDescriptionServiceAsync userDescriptionService =
		GWT.create(UserDescriptionService.class);
	private PageDescriptionServiceAsync pageDescriptionService =
		GWT.create(PageDescriptionService.class);
	
	
	private AggregatorConstants constants = GWT.create(AggregatorConstants.class);
	private AggregatorMessages messages = GWT.create(AggregatorMessages.class);
	
	private UserDescription userDescription;
	private String locale;
	private AggregatorTabPanel tabPanel;
	private PopupPanel loadPopup;
	private DialogBox addDialog;
	private DialogBox loginDialog;
	private DialogBox optionsDialog;
	private PickupDragController dragController;
	private boolean defaultPage = false;
	
	public UserDescription getUserDescription() {
		return userDescription;
	}

	/**
	 * This is the entry point method.
	 */
	public void onModuleLoad() {
		instance = this;
		
		String sDefaultPage = getHiddenValue("default-page");
		if("true".equals(sDefaultPage)) {
			defaultPage = true;
		} else {
			defaultPage = false;
		}
		
		RootPanel.get("main").getElement().getStyle().setProperty("position" , "relative");
		dragController = new AggregatorDragController(RootPanel.get("main"), false);
		dragController.addDragHandler(this);
		
		loadPopup = new PopupPanel();
		HorizontalPanel popupContent = new HorizontalPanel();
		loadPopup.add(popupContent);		
		popupContent.add(new Image("wait.gif"));
		popupContent.add(new Label(constants.loading()));		
		loadPopup.center();
		
		locale = getHiddenValue("locale");
		
		userDescriptionService.getUserDescription(
				new AsyncCallback<UserDescription>() {
					public void onFailure(Throwable caught) {
						Window.alert(SERVER_ERROR);
					}

					public void onSuccess(UserDescription userDescription) {
						Aggregator.this.userDescription = userDescription;						
						
						if(!isDefaultPage()) {
							pageDescriptionService.getPageDescription(
									locale,
									new AsyncCallback<PageDescription>() {
										public void onFailure(Throwable caught) {
											Window.alert(SERVER_ERROR);
										}
	
										public void onSuccess(PageDescription pageDescription) {
											load(pageDescription);
										}					
							});
						} else {
							pageDescriptionService.getDefaultPageDescription(
									locale,
									new AsyncCallback<PageDescription>() {
										public void onFailure(Throwable caught) {
											Window.alert(SERVER_ERROR);
										}

										public void onSuccess(PageDescription pageDescription) {
											load(pageDescription);
										}					
							});
						} 
					}				
		});
	}
	
	private void load(PageDescription pageDescription) {
		RootPanel.get("main").clear();
		
		addDialog = new AddContentDialog();
		loginDialog = new LoginDialog();
		optionsDialog = new UserOptionsDialog();
		
		FlowPanel mainPanel = new FlowPanel();
		mainPanel.setWidth("100%");
		mainPanel.setVisible(false);
		RootPanel.get("main").add(mainPanel);
		
		// Top bar
		HorizontalPanel topBar = new HorizontalPanel();
		topBar.setWidth("100%");
		topBar.setStylePrimaryName("topbar");
		topBar.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
		
		Anchor addButton = new Anchor(constants.addContent(),
				"javascript:void(0)");
		addButton.setStylePrimaryName("topbar-button");
		addButton.addStyleName("add-content-button");
		addButton.addClickHandler(new ClickHandler() {			
			public void onClick(ClickEvent event) {
				addDialog.center();
			}
		});
		if(!isModifiable()) {
			addButton.setVisible(false);
		}
		topBar.add(addButton);
		topBar.setCellWidth(addButton, "100%");		
		
		 if(UserDescription.GUEST_ID.equals(userDescription.getId())) {
			// Guest user
			Anchor loginButton = new Anchor(constants.login(),
					"javascript:void(0)");
			loginButton.setStylePrimaryName("topbar-button");
			loginButton.addStyleName("login-button");
			loginButton.addClickHandler(new ClickHandler() {			
				public void onClick(ClickEvent event) {
					loginDialog.center();
				}
			});
			topBar.add(loginButton);
		} else {
			// Connected user
			/*
			MenuBar menu = new MenuBar();
			
			MenuBar pagesMenu = new MenuBar(true);
			pagesMenu.setStylePrimaryName("topbar-submenu");
			
			MenuItem myPageItem = new MenuItem(constants.myPage(), new Command() {				
				public void execute() {
					if(defaultPage) {
						Window.Location.replace("index.jsp");
					}
				}
			});			
			pagesMenu.addItem(myPageItem);
			
			
			
			if(userDescription.isAdministrator()) {
				MenuItem defaultPageLocItem = new MenuItem(constants.defaultPage(), new Command() {				
					public void execute() {
						defaultPageDialog.center();
					}
				});
				pagesMenu.addItem(defaultPageLocItem);
				
				defaultPageLocItem.addStyleName("topbar-submenu-item");
			} else {
				MenuItem defaultPageItem = new MenuItem(constants.defaultPage(), new Command() {				
					public void execute() {
						if(!defaultPage) {
							Window.Location.replace("default-page.jsp");
						}
					}
				});
				pagesMenu.addItem(defaultPageItem);
				
				if(defaultPage) {
					defaultPageItem.addStyleName("topbar-submenu-item-current");				
				} else {
					defaultPageItem.addStyleName("topbar-submenu-item");
				}
			}
			
			if(defaultPage) {
				myPageItem.addStyleName("topbar-submenu-item");				
			} else {
				myPageItem.addStyleName("topbar-submenu-item-current");
			}
			
			MenuItem pages = new MenuItem(constants.pages(), pagesMenu);			
			pages.setStylePrimaryName("topbar-button");
			pages.addStyleName("topbar-menu");
			menu.addItem(pages);
			topBar.add(menu);
			*/
	
			Anchor optionsButton = new Anchor(constants.userOptions(),
					"javascript:void(0)");
			optionsButton.setStylePrimaryName("topbar-button");
			optionsButton.addStyleName("user-options-button");
			optionsButton.addClickHandler(new ClickHandler() {			
				public void onClick(ClickEvent event) {
					optionsDialog.center();
				}
			});
			topBar.add(optionsButton);
			
			Anchor logoutButton = new Anchor(constants.logout(),
				"javascript:void(0)");
			logoutButton.setStylePrimaryName("topbar-button");
			logoutButton.addStyleName("logout-button");
			logoutButton.addClickHandler(new ClickHandler() {			
				public void onClick(ClickEvent event) {
					loadPopup.center();
					loginService.disconnnect(new AsyncCallback<Void>() {
						public void onFailure(Throwable caught) {
							reloadPage();
						}
		
						public void onSuccess(Void result) {
							reloadPage();
						}
					});
				}
			});
			topBar.add(logoutButton);
		}
		
		mainPanel.add(topBar);
		
		// Header
		FlowPanel header = new FlowPanel();
		header.setStylePrimaryName("header");
		mainPanel.add(header);
		
		tabPanel = new AggregatorTabPanel();
		tabPanel.setWidth("100%");		
		
		List<TabDescription> tabDescriptions =
			pageDescription.getTabDescriptions();
		for (TabDescription tabDescription : tabDescriptions) {
			AggregatorTab tab = new AggregatorTab();
			
			List<ColumnDescription> columnDescriptions =
				tabDescription.getColumnDescriptions();
			List<AggregatorColumn> columns = new LinkedList<AggregatorColumn>();
			for (ColumnDescription columnDescription : columnDescriptions) {
				AggregatorColumn column = new AggregatorColumn();
				
				List<ComponentDescription> componentDescriptions =
					columnDescription.getComponentDescriptions();
				for (ComponentDescription componentDescription : componentDescriptions) {
					Component component = null;
					switch(componentDescription.getType())  {
						case NEWS :
							component = new NewsReader(componentDescription.getProperties()); 
							break;
						case HTML :
							component = new HtmlComponent(componentDescription.getProperties());
							break;
						case QUOTE :
							component = new QuotationComponent(componentDescription.getProperties());
							break;
					}
					if(component != null) {
						column.add(component);
					}
				}
				columns.add(column);
			}
			tab.setColumns(columns);
			tabPanel.add(tab, tabDescription.getTitle());
		}
		tabPanel.selectTab(0);
		
		mainPanel.add(tabPanel);
		
		// Footer
		FlowPanel footer = new FlowPanel();
		footer.setStylePrimaryName("footer");
		HTML poweredBy = new HTML(
				messages.poweredBy(
						"<a href=\"http://nicolas.dutertry.com/glimpse-project\">Glimpse</a>"));
		footer.add(poweredBy);		
		mainPanel.add(footer);
		
		loadPopup.hide();
		mainPanel.setVisible(true);
	}
	
	public void update() {
		if(!isModifiable()) {
			return;
		}
		PageDescription pageDescription = generatePageDescription();
		if(isDefaultPage()) {
			pageDescriptionService.setDefaultPageDescription(
					locale, pageDescription, new AsyncCallback<Void>() {
				public void onFailure(Throwable caught) {
					Window.alert(SERVER_ERROR);
				}
	
				public void onSuccess(Void result) {
				}			
			});
		} else {
			pageDescriptionService.setPageDescription(pageDescription, new AsyncCallback<Void>() {
				public void onFailure(Throwable caught) {
					Window.alert(SERVER_ERROR);
				}
	
				public void onSuccess(Void result) {
				}			
			});
		}
	}
	
	private PageDescription generatePageDescription() {
		PageDescription pageDescription = new PageDescription();
		
		for(int i = 0; i < tabPanel.getTabCount(); i++) {
			AggregatorTab tab = tabPanel.getTab(i);
			TabDescription tabDescription = new TabDescription();
			tabDescription.setTitle(tabPanel.getTitle(i));
			
			List<AggregatorColumn> columns = tab.getColumns();
			for (AggregatorColumn column : columns) {
				ColumnDescription columnDescription = new ColumnDescription();
				List<Component> components = column.getComponents();
				for (Component component : components) {
					ComponentDescription componentDescription =
						new ComponentDescription(component.getType());
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
		AggregatorTab tab = tabPanel.getTab(tabPanel.getVisibleTab());
		AggregatorColumn column = tab.getColumns().get(0);
		column.add(component);
	}
	
	public AggregatorColumn getColumn(AggregatorTab tab, Component component) {
		List<AggregatorColumn> columns = tab.getColumns();
		for (AggregatorColumn column : columns) {
			if(column.getComponentIndex(component) != -1) {
				return column;
			}
		}
		return null;
	}
	
	public PickupDragController getDragController() {
		return dragController;
	}

	public void onDragEnd(DragEndEvent event) {
		update();
	}

	public void onDragStart(DragStartEvent event) {
		event.getContext().draggable.getElement().getStyle().setProperty("position", "");
	}

	public void onPreviewDragEnd(DragEndEvent event) throws VetoDragException {
	}

	public void onPreviewDragStart(DragStartEvent event)
			throws VetoDragException {		
	}
	
	public void reloadPage() {
    	Window.Location.reload();
	}
	
	public static String getHiddenValue(String id) {
		Element elm = DOM.getElementById(id);
		if(elm == null) {
			return null;
		}
		final Hidden hidden = Hidden.wrap(elm);

		return (hidden != null) ? hidden.getValue() : null;
	}
	
	public boolean isModifiable() {
		return userDescription.getAttributes().isAdministrator() ||
			(!defaultPage && !userDescription.getId().equals(UserDescription.GUEST_ID));
	}
	
	public boolean isDefaultPage() {
		return defaultPage;
	}
}
