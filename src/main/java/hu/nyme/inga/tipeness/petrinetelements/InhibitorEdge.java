/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hu.nyme.inga.tipeness.petrinetelements;


import hu.nyme.inga.tipeness.petrinetelements.Transition;
import java.awt.Color;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import org.piccolo2d.nodes.PPath;

/**
 *
 * @author Andrew
 */
public class InhibitorEdge extends AbstractEdge {

    int RADIUS = 10;

    public InhibitorEdge(Place start, Transition end) {
        super(start, end);
    }

    public InhibitorEdge(PetriNetNode start, PetriNetNode end, ArrayList<BreakpointXMLNode> breakpointXMLNodeList) {
        super(start, end, breakpointXMLNodeList);
    }
    @Override
    public void updateLink() {

        for (Link link : this.links) {
            link.updateLink();
        }
        endShape.reset();
        endShape = createEndShape();
        links.get(links.size() - 1).addChild(endShape);
        endShape.raiseToTop();
        //endShape.append(path, true);
        //this.reset();
        //this.append(path, true);
        repaint();
        updateWeightPosition();
    }

    @Override
    public PPath createEndShape() {
        PPath createdShape;

        //breakpoints.get(middleBreakpoint).addChild(text);
        Point2D.Double lastBreakpoint = links.get(links.size() - 1).start.getPoint();
        Point2D intersectPoint = end.getIntersectionPoint(lastBreakpoint);

        double deltaX = Math.abs(lastBreakpoint.getX() - intersectPoint.getX());
        double deltaY = Math.abs(lastBreakpoint.getY() - intersectPoint.getY());

        double circleMiddleX;
        double circleMiddleY;
        double tanPhi;

        tanPhi = deltaX / deltaY;
        double tanPhiDeg = Math.toDegrees(tanPhi);
        double d;
        if (tanPhiDeg <= 30) {
            tanPhi = deltaY / deltaX;
            if (tanPhi != 0) {
                d = RADIUS / 2 / tanPhi;
            } else {
                d = 0;
            }

            if (lastBreakpoint.x > intersectPoint.getX()) {
                circleMiddleX = intersectPoint.getX() + d - RADIUS / 2;
            } else {
                circleMiddleX = intersectPoint.getX() - d - RADIUS / 2;
            }
            if (lastBreakpoint.y > intersectPoint.getY()) {
                circleMiddleY = intersectPoint.getY();
            } else {
                circleMiddleY = intersectPoint.getY() - RADIUS;
            }

        } else {
            d = RADIUS / 2 / tanPhi;
            if (lastBreakpoint.x > intersectPoint.getX()) {
                circleMiddleX = intersectPoint.getX();
            } else {
                circleMiddleX = intersectPoint.getX() - RADIUS;
            }

            if (lastBreakpoint.y > intersectPoint.getY()) {
                circleMiddleY = intersectPoint.getY() + d - RADIUS / 2;
            } else {
                circleMiddleY = intersectPoint.getY() - d - RADIUS / 2;
            }
        }
        // Arrow Path. 
        createdShape = PPath.createEllipse(circleMiddleX, circleMiddleY, RADIUS, RADIUS);        
        //createdShape.getBoundsReference().getCenter2D().setLocation(intersectPoint.getX()+RADIUS, intersectPoint.getY()+RADIUS);
        return createdShape;
    }

    @Override
    public String getConnectedPlaceName() {
        return ((Place)start).getName();
    }

    @Override
    public String getEdgeTypeTag() {
        return inhibitorEdgeTag;
    }

    @Override
    public Transition getConnectedTransition() {
        return (Transition) end;
    }

}
