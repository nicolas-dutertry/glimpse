package org.glimpse.client.news;

import java.util.HashMap;
import java.util.Map;

import org.glimpse.client.Aggregator;
import org.glimpse.client.Component;
import org.glimpse.client.widgets.HorizontalPanelExt;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;

public class NewsReader extends Component {
	static NewsRetrieverServiceAsync newsRetrieverService =
		GWT.create(NewsRetrieverService.class);
	
	private Anchor title = new Anchor("RSS reader");
	private HorizontalPanel loadingPanel = new HorizontalPanel();
	private HorizontalPanel optionPanel;
	private TextBox urlField;
	// Le tableau des news
	private EntriesTable entriesTable;
	Anchor previousButton = new Anchor();
	Anchor nextButton = new Anchor();
	
	private class RefreshHandler implements ClickHandler {
		public void onClick(ClickEvent event) {
			refresh();
		}
	}
	
	private class OptionHandler implements ClickHandler {
		public void onClick(ClickEvent event) {
			if(optionPanel.isVisible()) {
				optionPanel.setVisible(false);
			} else {
				optionPanel.setVisible(true);
			}
		}
	}
	
	private class NextHandler implements ClickHandler {
		public void onClick(ClickEvent event) {
			entriesTable.nextPage();
			checkPreviousNext();
		}
	}
	
	private class PreviousHandler implements ClickHandler {
		public void onClick(ClickEvent event) {
			entriesTable.previousPage();
			checkPreviousNext();
		}
	}
	
	public NewsReader() {
		this(new HashMap<String, String>());
	}
	
	public NewsReader(Map<String, String> properties) {
		super(properties);
		entriesTable = new EntriesTable();
		
		// Les boutons de commande du titre
		HorizontalPanelExt topBar = new HorizontalPanelExt();
		topBar.setWidth("100%");
		
		title.setHref("javascript:void(0)");
		title.setTarget("_blank");
		title.setStylePrimaryName("component-title-text");
		topBar.add(title);
		topBar.setCellWidth(title, "100%");
		
		Image refreshButton = new Image("images/refresh.png");
		refreshButton.addClickHandler(new RefreshHandler());
		topBar.add(refreshButton);
		topBar.setCellClass(refreshButton, "component-action");
		
		Image optionButton = new Image("images/options.png");
		optionButton.addClickHandler(new OptionHandler());
		topBar.add(optionButton);
		topBar.setCellClass(optionButton, "component-action");
		
		setTitle(topBar);
		
		// Contenu
		VerticalPanel panel = new VerticalPanel();		
		panel.setWidth("100%");
		
		optionPanel = new HorizontalPanel();
		optionPanel.add(new Label("URL"));
		urlField = new TextBox();
		String url = getUrl();
		if(url != null && !url.trim().equals("")) {
			urlField.setValue(url);
			optionPanel.setVisible(false);
		}
		optionPanel.add(urlField);
		Button button = new Button("OK");
		button.addClickHandler(new ClickHandler() {			
			@Override
			public void onClick(ClickEvent event) {
				setUrl(urlField.getValue());
			}
		});
		optionPanel.add(button);
		panel.add(optionPanel);
		
		
		// Le tableau des news
		panel.add(entriesTable);
		
		// image de chargement
		Image image = new Image("wait.gif");
		Label label = new Label("loading...");
		loadingPanel.add(image);
		loadingPanel.add(label);
		loadingPanel.setVisible(false);
		panel.add(loadingPanel);
		
		// Les boutons de commande du bas
		HorizontalPanelExt bottomBar = new HorizontalPanelExt();
		bottomBar.setWidth("100%");
		
		previousButton.setText("previous");
		previousButton.setHref("javascript:void(0)");
		previousButton.setStylePrimaryName("news-previous");
		previousButton.addClickHandler(new PreviousHandler());
		bottomBar.add(previousButton);
		bottomBar.setCellHorizontalAlignment(previousButton,
				HorizontalPanel.ALIGN_LEFT);
		
		nextButton.setText("next");
		nextButton.setHref("javascript:void(0)");
		nextButton.setStylePrimaryName("news-next");
		nextButton.addClickHandler(new NextHandler());
		bottomBar.add(nextButton);
		bottomBar.setCellHorizontalAlignment(nextButton,
				HorizontalPanel.ALIGN_RIGHT);
		
		panel.add(bottomBar);
		
		setContent(panel);
		
		refresh();
	}
	
	private void setUrl(String url) {
		setProperty("url", url);
		Aggregator.getInstance().update();
		refresh();
	}
	
	public void refresh() {
		final String url = getUrl();
		final boolean directOpen = isDirectOpen();
		entriesTable.setEntries(null, url, directOpen);
		
		if(url == null || url.trim().equals("")) {
			optionPanel.setVisible(true);
			return;
		} else {
			optionPanel.setVisible(false);
		}
		
		loadingPanel.setVisible(true);
		checkPreviousNext();
		newsRetrieverService.getNewsChannel(
				url,
				new AsyncCallback<NewsChannel>() {
					public void onFailure(Throwable caught) {
						Window.alert("Error");
					}

					public void onSuccess(NewsChannel channel) {
						loadingPanel.setVisible(false);
						title.setText(channel.getTitle());
						title.setHref(channel.getUrl());
						entriesTable.setEntries(channel.getEntries(), url, directOpen);
						checkPreviousNext();
					}
		});
	}
	
	public void checkPreviousNext() {
		if(entriesTable.hasPreviousPage()) {
			previousButton.setVisible(true);
		} else {
			previousButton.setVisible(false);
		}
		
		if(entriesTable.hasNextPage()) {
			nextButton.setVisible(true);
		} else {
			nextButton.setVisible(false);
		}
	}
	
	public String getUrl() {
		return getProperty("url");
	}
	
	public boolean isDirectOpen() {
		return !"false".equals(getProperty("directOpen"));
	}
}
