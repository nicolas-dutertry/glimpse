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
package org.glimpse.client.finance;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.glimpse.client.Aggregator;
import org.glimpse.client.ClientUtils;
import org.glimpse.client.Component;
import org.glimpse.client.i18n.AggregatorConstants;
import org.glimpse.client.layout.ComponentDescription.Type;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.HTMLTable.CellFormatter;
import com.google.gwt.user.client.ui.HTMLTable.RowFormatter;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public class QuotationComponent extends Component {
	private static final String PROPERTY_QUOTED_ELEMENTS = "quotedElements";
	
	static QuotationServiceAsync quotationService =
		GWT.create(QuotationService.class);
	private AggregatorConstants constants = GWT.create(AggregatorConstants.class);

	private FlexTable valueTable;
	private List<QuotedElement> quotedElements;
	private AddQuotedDialog addQuotedDialog;
	
	private class RefreshHandler implements ClickHandler {
		public void onClick(ClickEvent event) {
			refresh();
		}
	}
	
	private class OptionHandler implements ClickHandler {
		public void onClick(ClickEvent event) {
			addQuotedDialog.center();
		}
	}
	
	private class DeleteQuotedHandler implements ClickHandler {
		private QuotedElement quotedElement;
		
		private DeleteQuotedHandler(QuotedElement quotedElement) {
			this.quotedElement = quotedElement;
		}
		
		public void onClick(ClickEvent event) {
			if(Window.confirm(constants.deleteQuotedConfirm())) {
				deleteQuotedElement(quotedElement);
			}
		}
	}
	
	private class QuotationCallback implements AsyncCallback<Quotation> {
		private final QuotedElement quotedElement;
		
		public QuotationCallback(QuotedElement quotedElement) {
			this.quotedElement = quotedElement;
		}
		
		@Override
		public void onFailure(Throwable caught) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onSuccess(Quotation quotation) {
			int index = quotedElements.indexOf(quotedElement);
			if(index >= 0) {
				int row = index + 1;
				
				RowFormatter rowFormatter = valueTable.getRowFormatter();
				CellFormatter cellFormatter = valueTable.getCellFormatter();
				
				valueTable.setText(row, 1, String.valueOf(quotation.getValue()) +
						" " + quotation.getUnit());
				cellFormatter.setStyleName(row, 1, "quotation-value");
				
				double variation = quotation.getVariation();
				String cellContent = variation + "%";
				if(variation > 0) {
					cellContent = "+" + cellContent;
				}
				valueTable.setText(row, 2, cellContent);
				cellFormatter.setStyleName(row, 2, "quotation-variation");
				
				if(variation >= 0) {
					rowFormatter.addStyleName(row, "quotation-line-positive");
				} else {
					rowFormatter.addStyleName(row, "quotation-line-negative");
				}
			}
		}
		
	}
	
	public QuotationComponent() {
		this(new HashMap<String, String>());
	}
	
	public QuotationComponent(Map<String, String> properties) {
		super(properties);
		
		addQuotedDialog = new AddQuotedDialog(this);
		
		List<String> list = ClientUtils.stringToList(getProperty(PROPERTY_QUOTED_ELEMENTS));
		quotedElements = new LinkedList<QuotedElement>();
		QuotedElement quotedElement = null;
		for (int i = 0; i < list.size(); i++) {
			if(i % 2 == 0) {
				quotedElement = new QuotedElement(list.get(i));
				quotedElements.add(quotedElement);
			} else {
				quotedElement.setLabel(list.get(i));
			}
		}
		
		HorizontalPanel titlePanel = new HorizontalPanel();
		titlePanel.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
		Image titleImage = new Image("images/boursorama.png");
		titleImage.setStylePrimaryName("component-title-image");
		titlePanel.add(titleImage);
		Anchor title = new Anchor("Boursorama");
		title.setHref("http://www.boursorama.com");
		title.setTarget("_blank");
		titlePanel.add(title);
		setTitleWidget(titlePanel);
		
		List<Widget> actions = new LinkedList<Widget>();
		FocusPanel refreshButton = new FocusPanel(new Image(Aggregator.TRANSPARENT_IMAGE));
		refreshButton.setTitle(constants.refresh());
		refreshButton.setStylePrimaryName("component-action-refresh");
		refreshButton.addClickHandler(new RefreshHandler());
		actions.add(refreshButton);
		
		if(Aggregator.getInstance().isModifiable()) {
			FocusPanel optionButton = new FocusPanel(new Image(Aggregator.TRANSPARENT_IMAGE));
			optionButton.addClickHandler(new OptionHandler());
			optionButton.setTitle(constants.add());
			optionButton.setStylePrimaryName("component-action-add");
			actions.add(optionButton);
		}
		
		setActions(actions);
				
		// Contenu
		VerticalPanel panel = new VerticalPanel();		
		panel.setWidth("100%");
		
		// Le tableau des valeurs
		valueTable = new FlexTable();
		valueTable.setStylePrimaryName("quotations-table");
		valueTable.setCellPadding(0);
		valueTable.setCellSpacing(0);
		
		panel.add(valueTable);
		
		setContent(panel);
		
		refresh();		
	}
	
	void addQuotedElement(QuotedElement quotedElement) {
		quotedElements.add(quotedElement);
		saveQuotedElements();
		refresh();
	}
	
	void deleteQuotedElement(QuotedElement quotedElement) {
		quotedElements.remove(quotedElement);
		saveQuotedElements();
		refresh();
	}
	
	private void saveQuotedElements() {
		String s = "";
		for (QuotedElement qe : quotedElements) {
			if(!s.equals("")) {
				s += ",";
			}
			s += qe.getCode();
			s += ",";
			s += qe.getLabel();
		}
		setProperty(PROPERTY_QUOTED_ELEMENTS, s);
		Aggregator.getInstance().update();
	}
	
	public void refresh() {
		valueTable.removeAllRows();
		
		valueTable.getRowFormatter().setStylePrimaryName(0, "quotation-title");
		
		valueTable.setText(0, 0, constants.label());
		valueTable.setText(0, 1, constants.value());
		valueTable.setText(0, 2, constants.variation());
		valueTable.setText(0, 3, "");
		
		final RowFormatter rowFormatter = valueTable.getRowFormatter();
		final CellFormatter cellFormatter = valueTable.getCellFormatter();
		for (int i = 0; i < quotedElements.size(); i++) {
			QuotedElement quotedElement = quotedElements.get(i);
			int row = i+1;
			
			Anchor anchor = new Anchor(quotedElement.getLabel(),
					"http://www.boursorama.com/cours.phtml?symbole=" + quotedElement.getCode(),
					"_blank");
			
			valueTable.setWidget(row, 0, anchor);
			cellFormatter.setStyleName(row, 0, "quotation-label");
			valueTable.setText(row, 1, "loading...");
			cellFormatter.setStyleName(row, 1, "quotation-label");
			valueTable.setText(row, 2, "");
			cellFormatter.setStyleName(row, 2, "quotation-label");
			
			FocusPanel deleteButton = new FocusPanel(new Image(Aggregator.TRANSPARENT_IMAGE));
			deleteButton.setTitle(constants.delete());
			deleteButton.setStylePrimaryName("quotation-delete-button");
			deleteButton.addClickHandler(new DeleteQuotedHandler(quotedElement));
			valueTable.setWidget(row, 3, deleteButton);
			cellFormatter.setStyleName(row, 3, "quotation-delete");
			
			rowFormatter.setStyleName(row, "quotation-line");
			if(row % 2 == 0) {
				rowFormatter.addStyleName(row, "quotation-line-even");
			} else {
				rowFormatter.addStyleName(row, "quotation-line-odd");
			}			
			
			quotationService.getQuotation(quotedElement.getCode(),
					new QuotationCallback(quotedElement));
		}
	}

	@Override
	public Type getType() {
		return Type.QUOTE;
	}

}
