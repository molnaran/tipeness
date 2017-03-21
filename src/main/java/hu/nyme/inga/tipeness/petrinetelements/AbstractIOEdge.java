/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hu.nyme.inga.tipeness.petrinetelements;

import java.awt.BasicStroke;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import org.piccolo2d.nodes.PPath;

/**
 *
 * @author Andrew
 */
public abstract class AbstractIOEdge extends AbstractEdge{

    public AbstractIOEdge(PetriNetNode start, PetriNetNode end) {
        super(start, end);
    }
    public AbstractIOEdge(PetriNetNode start, PetriNetNode end, ArrayList<BreakpointXMLNode> breakpointXMLNodeList) {
        super(start, end, breakpointXMLNodeList);
    }
    
    @Override
    public PPath createEndShape(){        
        PPath.Double createdShape= new PPath.Double();
        
        int b = 10;
        double theta = Math.toRadians(30);   
        
        //breakpoints.get(middleBreakpoint).addChild(text);
        Point2D.Double lastBreakpoint= links.get(links.size()-1).start.getPoint();
        Point2D intersectPoint=end.getIntersectionPoint(lastBreakpoint);
        
        double xe = intersectPoint.getX();
        double ye = intersectPoint.getY();        
        double alpha = Math.atan2(ye - lastBreakpoint.y, xe - lastBreakpoint.x);
        double dx1 = b * Math.cos(alpha + theta);
        double dy1 = b * Math.sin(alpha + theta);
        double dx2 = b * Math.cos(alpha - theta);
        double dy2 = b * Math.sin(alpha - theta);
        
        // Arrow Path. 
        
        createdShape.setStroke(new BasicStroke(2));
                
        createdShape.moveTo(xe, ye);
        //path.lineTo(xe, ye);
        createdShape.lineTo(xe - dx1, ye - dy1);
        createdShape.moveTo(xe, ye);
        createdShape.lineTo(xe - dx2, ye- dy2);
        createdShape.moveTo(xe, ye);
        createdShape.lineTo(xe, ye);
        
        return createdShape;
    }    
    
}
