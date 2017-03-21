/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hu.nyme.inga.tipeness.petrinetelements;

import hu.nyme.inga.tipeness.simulation.XMLConstants;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.geom.Point2D;
import org.piccolo2d.PNode;
import org.piccolo2d.nodes.PPath;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 *
 * @author Andrew
 */
public class Breakpoint extends PNode implements EndpointInterface, XMLConstants{    
    public static int DIAMETER=5;
    PPath circle;
    AbstractEdge parentEdge;
    
    public Breakpoint(Point2D insertLocation, AbstractEdge parentEdge){        
        circle= PPath.createEllipse(insertLocation.getX(), insertLocation.getY(), DIAMETER, DIAMETER);
        circle.setPaint(Color.black);
        circle.setStroke(new BasicStroke(2));        
        this.addChild(circle);
        this.parentEdge=parentEdge;
    }    
    
    public double getDistanceFromBreakpoint(Breakpoint breakpoint){
        return Math.sqrt(Math.pow(this.circle.getFullBounds().getCenterX()-breakpoint.circle.getFullBounds().getCenterX(), 2)
                +Math.pow(this.circle.getFullBounds().getCenterY()-breakpoint.circle.getFullBounds().getCenterY(), 2));
    }
    
    public void highlight(){
        circle.setPaint(Color.BLUE);
    }

    public PPath getCircle() {
        return circle;
    }
    public Point2D.Double getCircleCenter(){
        return new Point2D.Double((circle.getFullBoundsReference().getCenter2D().getX()),
        (circle.getFullBoundsReference().getCenter2D().getY()));
    }

    @Override
    public Point2D.Double getPoint() {
        return new Point2D.Double(this.circle.getGlobalBounds().getCenterX(),
        this.circle.getGlobalBounds().getCenterY());
    }

    public Node getXMLNode(Document doc, int number){
        Element breakpointNode= doc.createElement(breakpointTag); 
        breakpointNode.setAttribute(breakpointNumberTag, String.valueOf(number));
        breakpointNode.setAttribute(shapePosXAttr, String.valueOf(this.circle.getX()+this.circle.getXOffset()));
        breakpointNode.setAttribute(shapePosYAttr, String.valueOf(this.circle.getY()+this.circle.getYOffset()));
        
        return breakpointNode;
    }

    @Override
    public String toString() {
        return "Breakpoint{x:" +this.circle.getX() +" y:"+this.circle.getY()+"}";
    }

    public AbstractEdge getParentEdge() {
        return parentEdge;
    }
    
    
}
