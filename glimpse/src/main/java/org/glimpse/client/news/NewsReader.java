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
package org.glimpse.client.news;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.glimpse.client.Aggregator;
import org.glimpse.client.Component;
import org.glimpse.client.i18n.AggregatorConstants;
import org.glimpse.client.layout.ComponentDescription.Type;
import org.glimpse.client.widgets.HorizontalPanelExt;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.http.client.URL;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public class NewsReader extends Component {
	private static final String PROPERTY_URL = "url";
	private static final String PROPERTY_DIRECT_OPEN = "directOpen";
	private static final String PROPERTY_MAX_PER_PAGE = "maxPerPage";
	private static final int MAX_VISITED_ENTRIES = 30;
	
	static NewsRetrieverServiceAsync newsRetrieverService =
		GWT.create(NewsRetrieverService.class);
	private AggregatorConstants constants = GWT.create(AggregatorConstants.class);
	
	private Anchor title = new Anchor(constants.newsReader());
	private Image titleImage;
	private HorizontalPanel loadingPanel = new HorizontalPanel();
	private SimplePanel optionPanel;
	private TextBox urlField;
	private CheckBox directOpenBox;
	private ListBox maxBox;
	
	// Le tableau des news
	private EntriesTable entriesTable;
	Anchor previousButton = new Anchor();
	Anchor nextButton = new Anchor();
	
	private Label error;
	
	private boolean initialized;
	private List<String> visitedEntries;
	
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
				synchronizeOptions();
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
		visitedEntries = stringToList(getProperty("visitedEntries"));
		
		entriesTable = new EntriesTable(this);
		
		// Les boutons de commande du titre
		
		HorizontalPanel titlePanel = new HorizontalPanel();
		titlePanel.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
		titleImage = new Image("images/feed.png");
		titleImage.setStylePrimaryName("component-title-image");
		titlePanel.add(titleImage);
		title.setHref("javascript:void(0)");
		title.setTarget("_blank");
		titlePanel.add(title);
		setTitleWidget(titlePanel);
		
		List<Widget> actions = new LinkedList<Widget>();
		FocusPanel refreshButton = new FocusPanel(new Image(Aggregator.TRANSPARENT_IMAGE));
		refreshButton.setTitle(constants.refresh());
		refreshButton.setStylePrimaryName("component-action-refresh");
		refreshButton.addClickHandler(new RefreshHandler());
		actions.add(refreshButton);
		
		FocusPanel optionButton = new FocusPanel(new Image(Aggregator.TRANSPARENT_IMAGE));
		optionButton.addClickHandler(new OptionHandler());
		optionButton.setTitle(constants.options());
		optionButton.setStylePrimaryName("component-action-options");
		actions.add(optionButton);
		
		setActions(actions);
		
		// Contenu
		VerticalPanel panel = new VerticalPanel();		
		panel.setWidth("100%");
		
		optionPanel = new SimplePanel();
		optionPanel.setStylePrimaryName("component-options");
		VerticalPanel vp = new VerticalPanel();
		FlexTable table = new FlexTable();		
		
		table.setText(0, 0, constants.url());
		urlField = new TextBox();
		table.setWidget(0, 1, urlField);
		
		table.setText(1, 0, constants.directOpen());
		directOpenBox = new CheckBox();
		table.setWidget(1, 1, directOpenBox);
		
		table.setText(2, 0, constants.maxEntries());
		maxBox = new ListBox();
		for(int i = 1; i <= 20; i++) {
			maxBox.addItem(String.valueOf(i));
		}
		table.setWidget(2, 1, maxBox);
		
		vp.add(table);
		
		synchronizeOptions();
		
		Button button = new Button(constants.ok());
		button.addClickHandler(new ClickHandler() {			
			public void onClick(ClickEvent event) {
				setProperty(PROPERTY_URL, urlField.getValue());
				setProperty(PROPERTY_DIRECT_OPEN,
						Boolean.toString(directOpenBox.getValue()));
				setProperty(PROPERTY_MAX_PER_PAGE,
						Integer.toString(maxBox.getSelectedIndex()+1));
				Aggregator.getInstance().update();
				refresh();
			}
		});
		vp.add(button);
		optionPanel.add(vp);
		panel.add(optionPanel);
		
		
		// Le tableau des news
		panel.add(entriesTable);
		
		// image de chargement
		Image wait = new Image("wait.gif");
		loadingPanel.add(wait);
		loadingPanel.setWidth("100%");
		loadingPanel.setVisible(false);
		loadingPanel.setCellHorizontalAlignment(wait, HorizontalPanel.ALIGN_CENTER);
		panel.add(loadingPanel);
		
		// En cas d'erreur
		error = new Label(constants.error());
		error.setVisible(false);
		panel.add(error);
		
		// Les boutons de commande du bas
		HorizontalPanelExt bottomBar = new HorizontalPanelExt();
		bottomBar.setWidth("100%");
		
		previousButton.setText(constants.previous());
		previousButton.setHref("javascript:void(0)");
		previousButton.setStylePrimaryName("news-previous");
		previousButton.addClickHandler(new PreviousHandler());
		bottomBar.add(previousButton);
		bottomBar.setCellHorizontalAlignment(previousButton,
				HorizontalPanel.ALIGN_LEFT);
		
		nextButton.setText(constants.next());
		nextButton.setHref("javascript:void(0)");
		nextButton.setStylePrimaryName("news-next");
		nextButton.addClickHandler(new NextHandler());
		bottomBar.add(nextButton);
		bottomBar.setCellHorizontalAlignment(nextButton,
				HorizontalPanel.ALIGN_RIGHT);
		
		panel.add(bottomBar);
		
		setContent(panel);
		
		checkPreviousNext();
	}
	
	public void refresh() {
		initialized = true;
		final String url = getUrl();
		entriesTable.clear();
		
		if(url == null || url.trim().equals("")) {
			optionPanel.setVisible(true);
			checkPreviousNext();
			return;
		} else {
			optionPanel.setVisible(false);
		}
		
		error.setVisible(false);
		loadingPanel.setVisible(true);
		checkPreviousNext();
		newsRetrieverService.getNewsChannel(
				url,
				new AsyncCallback<NewsChannel>() {
					public void onFailure(Throwable caught) {
						loadingPanel.setVisible(false);
						error.setVisible(true);
					}

					public void onSuccess(NewsChannel channel) {
						loadingPanel.setVisible(false);
						if(channel != null) {
							title.setText(channel.getTitle());
							title.setHref(channel.getUrl());
							String encodedUrl = URL.encodeComponent(channel.getUrl());
							titleImage.setUrl("servlets/news-icon?url=" + encodedUrl);
							entriesTable.setProperties(channel.getEntries(),
									getUrl(),
									getMaxPerPage(),
									isDirectOpen());
							checkPreviousNext();
						} else {
							error.setVisible(true);
						}
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
		return getProperty(PROPERTY_URL);
	}
	
	public boolean isDirectOpen() {
		return !"false".equals(getProperty(PROPERTY_DIRECT_OPEN));
	}
	
	public int getMaxPerPage() {
		String s = getProperty(PROPERTY_MAX_PER_PAGE);
		if(s == null || s.equals("")) {
			return 10;
		} else {
			return Integer.valueOf(s);
		}
	}
	
	private void synchronizeOptions() {
		urlField.setValue(getUrl());
		directOpenBox.setValue(isDirectOpen());
		maxBox.setSelectedIndex(getMaxPerPage()-1);
	}

	@Override
	public Type getType() {
		return Type.NEWS;
	}

	@Override
	protected void onTabActivated() {
		super.onTabActivated();
		if(!initialized) {
			refresh();
		}
	}
	
	public boolean isVisited(String entryId) {
		return visitedEntries.contains(entryId);
	}
	
	public void addVisitedEntry(String entryId) {
		visitedEntries.add(entryId);
		while(visitedEntries.size() > MAX_VISITED_ENTRIES) {
			visitedEntries.remove(0);
		}
		
		setProperty("visitedEntries", listToString(visitedEntries));
		Aggregator.getInstance().update();
	}
	
	private static String listToString(List<?> list) {
		if(list == null) {
			return "";
		}
		StringBuilder s = new StringBuilder();
		for(Object o : list) {
			if(s.length() > 0) {
				s.append(",");
			}
			s.append(o.toString());
		}
		return s.toString();
	}	
	
	private static List<String> stringToList(String s) {
		List<String> list = new LinkedList<String>();
		if(s != null && !s.equals("")) {
			int begin = 0;
			int end = s.indexOf(',');
			if(end == -1) {
				end = s.length();
			}
			while(begin < s.length()-1) {
				list.add(s.substring(begin, end));
				begin = end + 1;
				end = s.indexOf(',', begin);
				if(end == -1) {
					end = s.length();
				}
			}
		}
		return list;
	}
}
