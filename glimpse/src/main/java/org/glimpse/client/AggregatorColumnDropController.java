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
