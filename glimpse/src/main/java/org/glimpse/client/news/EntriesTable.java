package org.glimpse.client.news;

import java.util.LinkedList;
import java.util.List;

import org.glimpse.client.i18n.AggregatorConstants;
import org.glimpse.client.i18n.AggregatorMessages;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.FlexTable;

public class EntriesTable extends FlexTable {
	private AggregatorConstants constants = GWT.create(AggregatorConstants.class);
	private AggregatorMessages messages = GWT.create(AggregatorMessages.class);
	
	private List<Entry> entries = new LinkedList<Entry>();
	private int maxPerPage = 10;
	private int currentPage = 0;
	private String url;
	private boolean directOpen = true;
	private EntryContentDialog dialog;
	
	private class EntryTitle extends Anchor {
		private int entryIndex = 0;
		
		public EntryTitle(String label, String pubTime, int entryIndex) {
			super(createEntryTitleHtml(label, pubTime), true);
			this.entryIndex = entryIndex;
			setStylePrimaryName("entry-title");
			setWidth("100%");
			
			if(directOpen) {
				Entry entry = entries.get(this.entryIndex);
				setHref(entry.getUrl());
				setTarget("_blank");
			} else {
				setHref("javascript:void(0)");
				
				addClickHandler(new ClickHandler() {
					public void onClick(ClickEvent event) {
						setStylePrimaryName("entry-title");
						Entry entry = entries.get(EntryTitle.this.entryIndex);
						dialog.showEntry(url, entry);
					}
				});
			}
		}
	}
	
	private static String createEntryTitleHtml(String label, String pubTime) {
		StringBuilder html = new StringBuilder(label);
		if(pubTime != null) {
			html.append("<span class=\"entry-pubtime\"> - ");
			html.append(pubTime);
			html.append("<span>");
		}
		return html.toString();
	}

	public EntriesTable() {
		dialog = new EntryContentDialog();		
		setStylePrimaryName("entries-table");
	}
	
	public void clear() {
		setProperties(null, null, 10, true);
	}
	
	public void setProperties(List<Entry> entries,
			String url,
			int maxPerPage,
			boolean directOpen) {
		if(entries == null) {
			entries = new LinkedList<Entry>();
		}
		this.entries = entries;
		this.url = url;
		this.maxPerPage = maxPerPage;
		this.directOpen = directOpen;
		setPage(0);
	}
	
	public void setPage(int page) {
		if(page < 0 || page > ((entries.size()-1) / maxPerPage)) {
			return;
		}
		currentPage = page;
		while(getRowCount() > 0) {
			removeRow(0);
		}
		for(int i = page*maxPerPage; i < (page+1)*maxPerPage && i < entries.size(); i++) {
			Entry entry = entries.get(i);
			String pubTime = null;
			if(entry.getDate() != null) {
				long millis = System.currentTimeMillis() - entry.getDate().getTime();
				long minutes = millis / (60*1000L);
				long hours = minutes / 60L;
				long days = hours / 24L;
				long months = days / 30L;
				long years = days / 365L;
				if(minutes <= 1) {
					pubTime = constants.oneMinuteAgo();
				} else if(hours < 1) {
					pubTime = messages.someMinutesAgo(minutes);
				} else if(hours == 1) {
					pubTime = constants.oneHourAgo();
				} else if(days < 1) {
					pubTime = messages.someHoursAgo(hours);
				} else if(days == 1) {
					pubTime = constants.yesterday();
				}  else if(months < 1) {
					pubTime = messages.someDaysAgo(days);
				} else if(months == 1) {
					pubTime = constants.oneMonthAgo();
				} else if(years < 1) {
					pubTime = messages.someMonthsAgo(months);
				} else if(years == 1) {
					pubTime = constants.oneYearAgo();
				} else {
					pubTime = messages.someYearsAgo(years);
				}
			}
			
			setWidget(getRowCount(), 0, new EntryTitle(entry.getTitle(), pubTime, i));
		}
	}
	
	public void nextPage() {
		setPage(currentPage+1);
	}
	
	public void previousPage() {
		setPage(currentPage-1);
	}
	
	public boolean hasPreviousPage() {
		return currentPage > 0;
	}
	
	public boolean hasNextPage() {
		return currentPage < ((entries.size()-1) / maxPerPage);
	}
	
}
