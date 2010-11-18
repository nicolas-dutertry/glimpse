package org.glimpse.client;

import com.allen_sauer.gwt.dnd.client.PickupDragController;
import com.google.gwt.user.client.ui.AbsolutePanel;

public class AggregatorDragController extends PickupDragController {

	public AggregatorDragController(AbsolutePanel boundaryPanel,
			boolean allowDroppingOnBoundaryPanel) {
		super(boundaryPanel, allowDroppingOnBoundaryPanel);
	}

	@Override
	public void dragStart() {
		super.dragStart();
		
		context.draggable.getElement().getStyle().setProperty("position", "");
	}
}
