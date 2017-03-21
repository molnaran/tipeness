/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hu.nyme.inga.tipeness.petrinetelements;

import hu.nyme.inga.tipeness.simulation.XMLConstants;
import static hu.nyme.inga.tipeness.simulation.XMLConstants.nodeNameTag;
import static hu.nyme.inga.tipeness.simulation.XMLConstants.shapePosXAttr;
import static hu.nyme.inga.tipeness.simulation.XMLConstants.shapePosYAttr;
import java.awt.Color;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import org.piccolo2d.PNode;
import org.piccolo2d.nodes.PPath;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 *
 * @author Andrew
 */
public abstract class PetriNetNode extends PNode implements EndpointInterface, XMLConstants, Selectable{   
    
    protected PPath shape;
    private NodeText nodeText;
    
    
    public PetriNetNode(String nodeName){
        this.nodeText= new NodeText(nodeName.toLowerCase());        
    }
    
    public PPath getInnerShape(){
        return this.shape;
    }    
    
    public void createNameNode(){
        shape.addChild(nodeText);
        Point2D.Double textPosition= new Point2D.Double();
        textPosition.x=(shape.getBounds().getCenterX() - nodeText.getWidth() / 2);        
        textPosition.y=(shape.getY()+shape.getHeight()+2);
        nodeText.setTextNodePosition(textPosition);
    }
    
    
    @Override
    public String getName(){
        return this.nodeText.getText();
    }
    
    @Override
    public Point2D.Double getPoint() {        
        return new Point2D.Double(this.shape.getGlobalBounds().getCenterX(),
        this.shape.getGlobalBounds().getCenterY());
    }
        
    @Override
    public void setName(String name){
        this.nodeText.setText(name);
        Point2D.Double textPosition= new Point2D.Double();
        textPosition.x=(shape.getBounds().getCenterX() - nodeText.getWidth() / 2);        
        textPosition.y=(shape.getY()+shape.getHeight()+2);
        this.nodeText.setTextNodePosition(textPosition);
    }
    
    public abstract void setPosition(Point2D.Double centerPoint);
        
    public Node getXMLNode(Document doc){
        Element node= doc.createElement(getTransitionTag());
        Element nodeNameNode= doc.createElement(nodeNameTag);
        nodeNameNode.appendChild(doc.createTextNode(this.getName()));        
        
        node.setAttribute(shapePosXAttr, String.valueOf(this.shape.getX()+this.getInnerShape().getXOffset()));
        node.setAttribute(shapePosYAttr, String.valueOf(this.shape.getY()+this.getInnerShape().getYOffset()));
        
        node.setAttribute(textPosXAttr, String.valueOf(this.nodeText.getXWOffset()+this.getInnerShape().getXOffset()));
        node.setAttribute(textPosYAttr, String.valueOf(this.nodeText.getYWOffset()+this.getInnerShape().getYOffset()));
        node.appendChild(nodeNameNode);   
        return node;
    }
    public void setInnerShapeLocation(Point2D.Double location){
        this.shape.setX(location.x);
        this.shape.setY(location.y);
    }    

    public NodeText getNodeText() {
        return nodeText;
    }
    @Override
    public void highlightNode(Color color){
        this.shape.setPaint(color);
        this.nodeText.highlightNode(color);
    }
    @Override
    public void removeHighlight(){
        this.shape.setPaint(getDefaultShapeFill());
        this.nodeText.highlightNode(Color.BLACK);
    }
    public abstract Point2D.Double getIntersectionPoint(Point2D.Double lastBreakpoint);
        
    public abstract Color getDefaultShapeFill();
    public abstract String getTransitionTag();
           
    public abstract void addEdge(AbstractEdge edge);
    
    public abstract void removeEdge(AbstractEdge edge);
    
    public abstract ArrayList<AbstractEdge> getConnectedEdges();
}
