/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hu.soe.inga.tipeness.petrinetelements;


import hu.soe.inga.tipeness.simulation.XMLConstants;
import static hu.soe.inga.tipeness.simulation.XMLConstants.arcWeightTag;
import static hu.soe.inga.tipeness.simulation.XMLConstants.nodeNameTag;
import java.awt.Color;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Collections;
import org.piccolo2d.nodes.PPath;
import org.piccolo2d.util.PBounds;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 *
 * @author Andrew
 */
public abstract class AbstractEdge extends PPath.Double implements XMLConstants, Selectable{
    ArrayList<Link> links = new ArrayList<>();
    ArrayList<Breakpoint> breakpoints = new ArrayList<>();
    PPath endShape;
    PetriNetNode start;
    PetriNetNode end;
    NodeText weight;    
   
    public AbstractEdge(PetriNetNode start, PetriNetNode end) {        
        this.start = start;
        this.end = end;        
        
        endShape= new PPath.Double();        
        weight= new NodeText(((Integer)getDefaultArcWeight()).toString());
        links= new ArrayList<>();
        addLinkToList(new Link(start, end, this));
        links.get(0).line.addChild(weight);        
        updateLink();
    }
    
    public AbstractEdge(PetriNetNode start, PetriNetNode end, ArrayList<BreakpointXMLNode> breakpointXMLNodeList) {        
        this.start = start;
        this.end = end;
        
        endShape= new PPath.Double();        
        weight= new NodeText(((Integer)getDefaultArcWeight()).toString());
        initBreakpoints(breakpointXMLNodeList);
        links.get(0).line.addChild(weight);        
        updateLink();
    }
    public void updateLink() {  
        
        for (Link link : this.links) {
            link.updateLink();            
        }
        endShape.reset();
        endShape= createEndShape();
        links.get(links.size()-1).addChild(endShape);        
        endShape.raiseToTop();
        repaint();
        updateWeightPosition();
    }  
    public abstract PPath createEndShape();
    
     public void deleteBreakpoint(Breakpoint breakpoint){        
        int intersectingIndex=0;
        for (int i = 0; i < links.size(); i++) {            
            if (links.get(i).end.getPoint().equals(breakpoint.getPoint())){                
                intersectingIndex=i;                
            }
        }
        links.get(intersectingIndex).end=links.get(intersectingIndex+1).end;
        
        
        this.removeChild(links.get(intersectingIndex+1));
        links.remove(intersectingIndex+1);
        breakpoints.remove(breakpoint);
        this.removeChild(breakpoint);
        this.updateLink();
    }
    
    public void initBreakpoints(ArrayList<BreakpointXMLNode> breakpointXMLNodeList) {
        Collections.sort(breakpointXMLNodeList);
        if (breakpointXMLNodeList.isEmpty()){
            this.links= new ArrayList<>();                     
            addLinkToList(new Link(this.start, this.end, this));            
        } else {
            Breakpoint firstBreakpoint=new Breakpoint(breakpointXMLNodeList.get(0).getPosition(),this);
            
            addLinkToList(new Link(this.start, firstBreakpoint, this));              
            addBreakpointToList(firstBreakpoint);
            for (int i = 1; i < breakpointXMLNodeList.size(); i++) { 
                firstBreakpoint=this.breakpoints.get(breakpoints.size()-1);
                //addBreakpointToList(firstBreakpoint);
                Breakpoint secondBreakpoint=new Breakpoint(breakpointXMLNodeList.get(i).getPosition(),this);
                addLinkToList(new Link(firstBreakpoint, secondBreakpoint, this));                
                addBreakpointToList(secondBreakpoint);                
            }
            Breakpoint lastBreakpoint=this.breakpoints.get(breakpoints.size()-1);
            addLinkToList(new Link(lastBreakpoint, this.end, this));            
        }
    }
     
    public void addBreakpoint(Point2D position){        
        Link intersectingLink=getIntersectingLink(position);   
       
        if (intersectingLink != null) {
            Breakpoint breakpoint;
            if (intersectingLink.end instanceof Breakpoint) {
                Breakpoint breakpoint2 = (Breakpoint) intersectingLink.end;                
                breakpoint= new Breakpoint(position, this);                
                addBreakpointToList(breakpoints.indexOf(breakpoint2), breakpoint);
            } else{
                breakpoint=new Breakpoint(position, this);                
                addBreakpointToList(breakpoint);
            }
            Link newLink = new Link(breakpoint, intersectingLink.end, this);
            intersectingLink.end = breakpoint;
            addLinkToList(links.indexOf(intersectingLink) + 1, newLink);            
        }        
    }
    public void updateWeightPosition() {
        Link lastLink = links.get(links.size() - 1);
        Point2D.Double position= new Point2D.Double();
        position.x=(lastLink.line.getBounds().getCenterX());
        position.y=(lastLink.line.getBounds().getCenterY()) - weight.getHeight();
        weight.setTextNodePosition(position);
    }    
    public int getArcWeight(){
        return Integer.valueOf(this.weight.getText());
    }    
    
    public int getDefaultArcWeight(){
        return 1;
    }
    
    @Override
    public String toString(){
        String nl = System.lineSeparator();
        StringBuilder sb = new StringBuilder();
        sb.append(this.getConnectedPlaceName()).append(", ").append(this.getArcWeight()).append(nl);
        return sb.toString();
    }
    public Node getXMLNode(Document doc){
        Element edgeNode = doc.createElement(getEdgeTypeTag());

        Element edgePlaceNameNode = doc.createElement(nodeNameTag);
        edgePlaceNameNode.appendChild(doc.createTextNode(this.getConnectedPlaceName()));
        Element edgeArcNode = doc.createElement(arcWeightTag);
        edgeArcNode.appendChild(doc.createTextNode(String.valueOf(this.getArcWeight())));

        edgeNode.appendChild(edgePlaceNameNode);
        edgeNode.appendChild(edgeArcNode);
        for (Breakpoint breakpoint: breakpoints){
            Node breakpointNode= breakpoint.getXMLNode(doc, breakpoints.indexOf(breakpoint));            
            edgeNode.appendChild(breakpointNode);
        }
        return edgeNode;
    }
    
    private void addBreakpointToList(Breakpoint breakpoint){
        addBreakpointToList(breakpoints.size(), breakpoint);
    }
    
    private void addBreakpointToList(int index, Breakpoint breakpoint) {
        breakpoints.add(index, breakpoint);
        this.addChild(breakpoint);
    }

    private Link getIntersectingLink(Point2D position) {
        Link intersectingLink = null;
        for (int i = 0; i < links.size(); i++) {
            if (links.get(i).getLine().intersects(new PBounds(position.getX(), position.getY(), Breakpoint.DIAMETER, Breakpoint.DIAMETER))) {
                intersectingLink = links.get(i);
            }
        }
        return intersectingLink;
    }
    public void setArcWeight(int weight){        
        this.weight.setName(((Integer)weight).toString());
        updateWeightPosition();
    }
    
    private void addLinkToList(Link link) {
        addLinkToList(links.size(), link);
    }
    private void addLinkToList(int index, Link link) {
        links.add(index, link);
        this.addChild(link);
        link.lowerToBottom();
        updateLink();
    }
    
    @Override
    public void highlightNode(Color color){
        for (Link link: links){
            link.highlight(color);
        }
        endShape.setStrokePaint(color);
        this.weight.nameNode.setTextPaint(color);
    }
    @Override
    public void removeHighlight(){
        for (Link link: links){
            link.highlight(Color.BLACK);
        }
        endShape.setStrokePaint(Color.BLACK);
        this.weight.nameNode.setTextPaint(Color.BLACK);
    }    

    public PetriNetNode getStart() {
        return start;
    }

    public PetriNetNode getEnd() {
        return end;
    }

    public void setStart(PetriNetNode start) {
        this.start = start;
    }

    public void setEnd(PetriNetNode end) {
        this.end = end;
    }
    
    
    
    public abstract String getEdgeTypeTag();

    public abstract String getConnectedPlaceName();
    
    public abstract Transition getConnectedTransition();
            
}
