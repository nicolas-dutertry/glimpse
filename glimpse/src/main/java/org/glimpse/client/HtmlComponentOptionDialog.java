package org.glimpse.client;

import org.glimpse.client.i18n.AggregatorConstants;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Overflow;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.TextBox;

public class HtmlComponentOptionDialog extends DialogBox {
	private AggregatorConstants constants = GWT.create(AggregatorConstants.class);
	
	public static final KeyDownHandler DEFAULT_TEXTAREA_TAB_HANDLER = new KeyDownHandler() {
	    @Override
	    public final void onKeyDown(KeyDownEvent event) {
	      if (event.getNativeKeyCode() == 9) {
	        event.preventDefault();
	        event.stopPropagation();
	        final TextArea ta = (TextArea) event.getSource();
	        final int index = ta.getCursorPos();
	        final String text = ta.getText();
	        ta.setText(text.substring(0, index) 
	                   + "\t" + text.substring(index));
	        ta.setCursorPos(index + 1);
	      }
	    }
	  };
	
	private HtmlComponent component;
	private TextBox titleInput;
	private TextArea htmlArea;
	private TextArea scriptArea;
	
	public HtmlComponentOptionDialog(HtmlComponent component) {
		this.component = component;
		
		setAnimationEnabled(true); 
		setText(constants.options());
		
		FlowPanel panel = new FlowPanel();
		
		FlexTable table = new FlexTable();
		table.setText(0, 0, constants.title());
		titleInput = new TextBox();
		table.setWidget(0, 1, titleInput);
		
		table.setText(1, 0, constants.html());
		htmlArea = new TextArea();
		htmlArea.setWidth("600px");
		htmlArea.setHeight("200px");
		htmlArea.addKeyDownHandler(DEFAULT_TEXTAREA_TAB_HANDLER);
		htmlArea.getElement().setAttribute("wrap", "off");
		htmlArea.getElement().getStyle().setOverflow(Overflow.SCROLL);
		table.setWidget(1, 1, htmlArea);
		
		table.setText(2, 0, "Script");
		scriptArea = new TextArea();
		scriptArea.setWidth("600px");
		scriptArea.setHeight("200px");
		scriptArea.addKeyDownHandler(DEFAULT_TEXTAREA_TAB_HANDLER);
		scriptArea.getElement().setAttribute("wrap", "off");
		scriptArea.getElement().getStyle().setOverflow(Overflow.SCROLL);
		table.setWidget(2, 1, scriptArea);
		
		panel.add(table);
		
		HorizontalPanel buttonsPanel = new HorizontalPanel();
		buttonsPanel.setWidth("100%");
		buttonsPanel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
		panel.add(buttonsPanel);
		
		Button ok = new Button(constants.ok());
		ok.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				HtmlComponentOptionDialog.this.component.update(
						titleInput.getText(),
						htmlArea.getText(),
						scriptArea.getText());
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
	
	@Override
	public void show() {
		synchronize();
		super.show();
	}

	@Override
	public void center() {
		synchronize();
		super.center();
	}
	
	private void synchronize() {
		titleInput.setValue(component.getTitle());
		htmlArea.setValue(component.getHtml());
		scriptArea.setValue(component.getScript());
	}
}