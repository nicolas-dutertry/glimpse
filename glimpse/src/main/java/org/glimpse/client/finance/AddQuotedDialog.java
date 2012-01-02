package org.glimpse.client.finance;

import org.glimpse.client.i18n.AggregatorConstants;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.TextBox;

public class AddQuotedDialog extends DialogBox {
	private AggregatorConstants constants = GWT.create(AggregatorConstants.class);
	
	private QuotationComponent quotationComponent;
	
	private TextBox codeBox;
	private TextBox labelBox;	

	public AddQuotedDialog(QuotationComponent quotationComponent) {
		this.quotationComponent = quotationComponent;
		
		setText(constants.addQuoted());
		
		FlowPanel panel = new FlowPanel();
		
		FlexTable table = new FlexTable();
		table.setText(0, 0, constants.code());
		codeBox = new TextBox();
		table.setWidget(0, 1, codeBox);
		
		table.setText(1, 0, constants.label());
		labelBox = new TextBox();
		table.setWidget(1, 1, labelBox);
		
		panel.add(table);
		
		// Boutons
		HorizontalPanel buttonsPanel = new HorizontalPanel();
		buttonsPanel.setWidth("100%");
		buttonsPanel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
		panel.add(buttonsPanel);
		
		Button ok = new Button(constants.ok());
		ok.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				String code = codeBox.getValue();
				String label = labelBox.getValue();
				QuotedElement quotedElement = new QuotedElement(code, label);
				AddQuotedDialog.this.quotationComponent.addQuotedElement(quotedElement);
				hide();
			}
		});		
		buttonsPanel.add(ok);
		
		Button cancel = new Button(constants.cancel());
		cancel.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				hide();
			}
		});		
		buttonsPanel.add(cancel);
		
		setWidget(panel);
	}
}
