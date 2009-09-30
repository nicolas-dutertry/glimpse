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
package org.glimpse.client;

import com.allen_sauer.gwt.dnd.client.DragContext;
import com.allen_sauer.gwt.dnd.client.drop.FlowPanelDropController;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;

public class AggregatorColumnDropController extends FlowPanelDropController {

	public AggregatorColumnDropController(FlowPanel dropTarget) {
		super(dropTarget);
	}

	@Override
	protected Widget newPositioner(DragContext context) {
		SimplePanel positioner = new SimplePanel();
		positioner.setStylePrimaryName("drop-positioner");
		SimplePanel framePositioner = new SimplePanel();
		framePositioner.setWidth("100%");
		framePositioner.setStylePrimaryName("drop-positioner-frame");
		positioner.add(framePositioner);
		framePositioner.add(new Image(Aggregator.TRANSPARENT_IMAGE));
		
		return positioner;
	}

}
