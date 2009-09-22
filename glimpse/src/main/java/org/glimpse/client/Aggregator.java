package org.glimpse.client;

import java.util.LinkedList;
import java.util.List;

import org.glimpse.client.i18n.AggregatorConstants;
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
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.FlowPanel;
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
	
	private UserDescription userDescription;
	private AggregatorTabPanel tabPanel;
	private PopupPanel loadPopup;
	private DialogBox addDialog;
	private DialogBox loginDialog;
	private DialogBox optionsDialog;
	private PickupDragController dragController;
	
	public UserDescription getUserDescription() {
		return userDescription;
	}

	/**
	 * This is the entry point method.
	 */
	public void onModuleLoad() {
		instance = this;
		
		RootPanel.get("main").getElement().getStyle().setProperty("position" , "relative");
		dragController = new PickupDragController(RootPanel.get("main"), false);
		dragController.addDragHandler(this);
		
		loadPopup = new PopupPanel();
		HorizontalPanel popupContent = new HorizontalPanel();
		loadPopup.add(popupContent);		
		popupContent.add(new Image("wait.gif"));
		popupContent.add(new Label(constants.loading()));		
		loadPopup.center();
		
		userDescriptionService.getUserDescription(
				new AsyncCallback<UserDescription>() {
					public void onFailure(Throwable caught) {
						Window.alert(SERVER_ERROR);
					}

					public void onSuccess(UserDescription userDescription) {
						Aggregator.this.userDescription = userDescription;
						pageDescriptionService.getPageDescription(
								new AsyncCallback<PageDescription>() {
									public void onFailure(Throwable caught) {
										Window.alert(SERVER_ERROR);
									}

									public void onSuccess(PageDescription pageDescription) {
										load(pageDescription);
									}					
						});
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
		
		// Top bar
		HorizontalPanel topBar = new HorizontalPanel();
		topBar.setWidth("100%");
		topBar.setStylePrimaryName("topbar");
		
		Anchor addButton = new Anchor(constants.addContent(),
				"javascript:void(0)");
		addButton.setStylePrimaryName("add-content-button");
		addButton.addClickHandler(new ClickHandler() {			
			public void onClick(ClickEvent event) {
				addDialog.center();
			}
		});
		topBar.add(addButton);
		topBar.setCellWidth(addButton, "100%");		
		
		if(UserDescription.GUEST_ID.equals(userDescription.getId())) {
			// Guest user
			Anchor loginButton = new Anchor(constants.login(),
					"javascript:void(0)");
			loginButton.setStylePrimaryName("login-button");
			loginButton.addClickHandler(new ClickHandler() {			
				public void onClick(ClickEvent event) {
					loginDialog.center();
				}
			});
			topBar.add(loginButton);
		} else {
			// Connected user
			Anchor optionsButton = new Anchor(constants.userOptions(),
					"javascript:void(0)");
			optionsButton.setStylePrimaryName("user-options-button");
			optionsButton.addClickHandler(new ClickHandler() {			
				public void onClick(ClickEvent event) {
					optionsDialog.center();
				}
			});
			topBar.add(optionsButton);
			
			Anchor logoutButton = new Anchor(constants.logout(),
				"javascript:void(0)");
			logoutButton.setStylePrimaryName("logout-button");
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
		RootPanel.get("main").add(mainPanel);
		
		loadPopup.hide();
	}
	
	public void update() {
		PageDescription pageDescription = generatePageDescription();
		pageDescriptionService.setPageDescription(pageDescription, new AsyncCallback<Void>() {
			public void onFailure(Throwable caught) {
				Window.alert(SERVER_ERROR);
			}

			public void onSuccess(Void result) {
			}			
		});
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
	}

	public void onPreviewDragEnd(DragEndEvent event) throws VetoDragException {
	}

	public void onPreviewDragStart(DragStartEvent event)
			throws VetoDragException {
	}
	
	public native void reloadPage() /*-{
    	$wnd.location.reload();
	}-*/;
}
