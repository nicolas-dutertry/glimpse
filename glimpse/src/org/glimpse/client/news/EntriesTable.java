package org.glimpse.client.news;

import java.util.LinkedList;
import java.util.List;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.FlexTable;

public class EntriesTable extends FlexTable {
	private List<Entry> entries = new LinkedList<Entry>();
	private int maxPerPage = 10;
	private int currentPage = 0;
	private String url;
	private boolean directOpen = true;
	private EntryContentDialog dialog;
	
	private class EntryTitle extends Anchor {
		private int entryIndex = 0;
		
		public EntryTitle(String label, int entryIndex) {
			super(label);
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
					@Override
					public void onClick(ClickEvent event) {
						setStylePrimaryName("entry-title");
						Entry entry = entries.get(EntryTitle.this.entryIndex);
						dialog.showEntry(url, entry);
					}
				});
			}
		}
	}

	public EntriesTable() {
		dialog = new EntryContentDialog();		
		setStylePrimaryName("entries-table");
	}
	
	public void setEntries(List<Entry> entries, String url, boolean directOpen) {
		if(entries == null) {
			entries = new LinkedList<Entry>();
		}
		this.entries = entries;
		this.url = url;
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
			setWidget(getRowCount(), 0, new EntryTitle(entry.getTitle(), i));
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
