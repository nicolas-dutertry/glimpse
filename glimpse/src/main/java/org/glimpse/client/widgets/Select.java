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
