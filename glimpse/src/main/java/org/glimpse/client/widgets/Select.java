package org.glimpse.client.widgets;

import java.util.LinkedList;
import java.util.List;

import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.HasChangeHandlers;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.ListBox;

public class Select extends Composite implements HasChangeHandlers {
	public static class Option {
		private String label;
		private String value;

		public Option(String label) {
			this(label, label);
		}
		
		public Option(String label, String value) {
			this.label = label;
			this.value = value;
		}

		public String getLabel() {
			return label;
		}

		public void setLabel(String label) {
			this.label = label;
		}

		public String getValue() {
			return value;
		}

		public void setValue(String value) {
			this.value = value;
		}
	}
	
	private ListBox listBox;
	private List<Option> options;
	
	public Select() {
		options = new LinkedList<Option>();
		listBox = new ListBox();
		initWidget(listBox);
	}
	
	public void addOption(Option option) {
		options.add(option);
		listBox.addItem(option.getLabel(), option.getValue());
	}
	
	public Option getSelectedOption() {
		return options.get(listBox.getSelectedIndex());
	}
	
	public void setSelectedValue(String value) {
		for(int i = 0; i < options.size(); i++) {
			if(options.get(i).getValue().equals(value)) {
				listBox.setSelectedIndex(i);
				return;
			}
		}
	}

	public HandlerRegistration addChangeHandler(ChangeHandler handler) {
		return listBox.addChangeHandler(handler);
	}

}
