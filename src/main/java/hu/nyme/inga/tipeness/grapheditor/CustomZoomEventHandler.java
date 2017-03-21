/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hu.nyme.inga.tipeness.grapheditor;

import java.awt.geom.Point2D;
import org.piccolo2d.PCamera;
import org.piccolo2d.event.PInputEvent;
import org.piccolo2d.event.PZoomEventHandler;

/**
 *
 * @author Alkohol
 */
public class CustomZoomEventHandler extends PZoomEventHandler {


    public CustomZoomEventHandler() {
        super();
    }

    @Override
    public void processEvent(final PInputEvent event, final int i) {
        double scale;
        if (event.isMouseWheelEvent()) {
            scale = 1D - 0.1 * event.getWheelRotation();
            final Point2D point = event.getPosition();
            event.getCamera().scaleViewAboutPoint(scale, point.getX(), point.getY());
        }
    }

}
