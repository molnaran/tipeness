/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package hu.soe.inga.tipeness.petrinetelements;

import java.util.HashMap;
import hu.soe.inga.tipeness.simulation.NetState;
import hu.soe.inga.tipeness.simulation.XMLConstants;
import static hu.soe.inga.tipeness.simulation.XMLConstants.nodeNameTag;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.HashSet;
import org.piccolo2d.nodes.PPath;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;


public abstract class Transition extends PetriNetNode implements XMLConstants{
    
    int HEIGHT=50;    
    public static enum MemoryPolicy {
        resampling, enablingMemory, ageMemory
    };

    private HashSet<InputEdge> input;
    private HashSet<InhibitorEdge> inhibitor;
    private HashSet<OutputEdge> output;

    private HashMap<BasicTimedTransition, MemoryPolicyAtTransition> memoryPolicyList;    
    public Transition(String nodeName, Point2D.Double insertLocation) {
        super(nodeName);
        initShape(insertLocation);
        input = new HashSet<>();
        inhibitor = new HashSet<>();
        output = new HashSet<>();
        memoryPolicyList = new HashMap<>();
    }    

    public int getEnablingDegree(NetState netState) {
        int enablingDegree = -1;
        if (!isEnabled(netState)) {
            return 0;
        }
        int currentFire;
        for (InputEdge s : input) {
            currentFire = netState.getPlaceByName(s.getConnectedPlaceName()).getCurrent() / s.getArcWeight();
            if (enablingDegree == -1) {
                enablingDegree = currentFire;
            }
            if ((currentFire < enablingDegree)) {
                enablingDegree = currentFire;
            }
        }        
        return enablingDegree;
    }

    public void fire(NetState netState) {
        if (isEnabled(netState)) {
            if (!input.isEmpty()) {
                for (InputEdge inputEdge : input) {
                    netState.getPlaceByName(inputEdge.getConnectedPlaceName()).decreaseToken(inputEdge.getArcWeight());
                }
            }
            if (!output.isEmpty()) {
                for (OutputEdge outputEdge : output) {
                    netState.getPlaceByName(outputEdge.getConnectedPlaceName()).increaseToken(outputEdge.getArcWeight());
                }
            }
        }
    }

    public boolean isEnabled(NetState netState) {
        for (InhibitorEdge inhibitorEdge : inhibitor) {
            if (inhibitorEdge.getArcWeight() <= netState.getPlaceByName(inhibitorEdge.getConnectedPlaceName()).getCurrent()) {
                return false;
            }
        }
        for (InputEdge inputEdge : input) {
            if (inputEdge.getArcWeight() != 0 && netState.getPlaceByName(inputEdge.getConnectedPlaceName()).getCurrent() < inputEdge.getArcWeight()) {
                return false;
            }
        }
        return true;
    }

    @Override
    public String toString() {
        String nl = System.lineSeparator();
        StringBuilder sb = new StringBuilder();

        sb.append("Transition name: ").append(this.getName()).append(nl);
        sb.append("Transition type: ").append(this.getClass().getSimpleName()).append(nl);
        sb.append("Input places:").append(nl);
        for (InputEdge inputEdge : input) {
            sb.append(inputEdge.toString());
        }
        sb.append("Inhibitor places:").append(nl);
        for (InhibitorEdge inhibitorEdge : inhibitor) {
            sb.append(inhibitorEdge.toString());
        }
        sb.append("Output places:").append(nl);
        for (OutputEdge outputEdge : output) {
            sb.append(outputEdge.toString());
        }
        sb.append("Used memory policies after firing:").append(nl);
        for (BasicTimedTransition transition : memoryPolicyList.keySet()) {
            if (memoryPolicyList.get(transition).getMemoryPolicy() != Transition.MemoryPolicy.enablingMemory){
                sb.append(memoryPolicyList.get(transition)).append(nl);
            }            
        }
        
        return sb.toString();
    }   

    public HashSet<InputEdge> getInput() {
        return input;
    }

    public HashSet<InhibitorEdge> getInhibitor() {
        return inhibitor;
    }

    public HashSet<OutputEdge> getOutput() {
        return output;
    }
    
    private void initShape(Point2D.Double insertLocation){        
        shape=PPath.createRectangle(insertLocation.x, insertLocation.y, this.getShapeWidth(), HEIGHT);        
        shape.setStroke(new BasicStroke(2));     
        
        createNameNode();
        this.addChild(shape);        
    }
    
    @Override
    public Point2D.Double getIntersectionPoint(Point2D.Double lastBreakpoint){
        double rectCenterX=this.getInnerShape().getGlobalBounds().getCenterX();
        double rectCenterY=this.getInnerShape().getGlobalBounds().getCenterY();
        
        double lineStartX=lastBreakpoint.getX();
        double lineStartY=lastBreakpoint.getY();   
        
        
        double intersectDistY=(Math.abs(lineStartY-rectCenterY)*this.getShapeWidth()/2)/Math.abs(lineStartX-rectCenterX);
        double intersectDistX;
        
        if (intersectDistY>=this.HEIGHT/2){
            intersectDistX=(Math.abs(lineStartX-rectCenterX)*this.HEIGHT/2)/Math.abs(lineStartY-rectCenterY);
            intersectDistY=this.HEIGHT/2;
        }else{
            intersectDistX=this.getShapeWidth()/2;
        }
        Point2D.Double point= new Point2D.Double();
        if (lineStartX>rectCenterX){
            point.x=rectCenterX+intersectDistX;
        }else{
            point.x=rectCenterX-intersectDistX;
        }
        
        if (lineStartY>rectCenterY){
            point.y=rectCenterY+intersectDistY;
        }else{
            point.y=rectCenterY-intersectDistY;
        }
        return point;
    }   
    
    public abstract int getShapeWidth();
    
    public abstract Color getDefaultColor();    
        
    @Override
    public Node getXMLNode(Document doc){
        Node transitionNode= super.getXMLNode(doc);
        
        for (BasicTimedTransition transition: memoryPolicyList.keySet()){
                  
            Element memoryNode;
            Element transNameNode;            
            Element policyNode;
            switch (memoryPolicyList.get(transition).getMemoryPolicy()) {
                case resampling:
                    memoryNode = doc.createElement(memoryMainTag);
                    transNameNode = doc.createElement(nodeNameTag);
                    transNameNode.appendChild(doc.createTextNode(transition.getName()));                    
                    policyNode= doc.createElement(memoryPolicyTag);
                    policyNode.appendChild(doc.createTextNode(resamplingMemoryTag));
                    
                    memoryNode.appendChild(transNameNode);
                    memoryNode.appendChild(policyNode);
                    transitionNode.appendChild(memoryNode);
                    break;
                case enablingMemory:
                    memoryNode = doc.createElement(memoryMainTag);
                    transNameNode = doc.createElement(nodeNameTag);
                    transNameNode.appendChild(doc.createTextNode(transition.getName()));                    
                    policyNode= doc.createElement(memoryPolicyTag);
                    policyNode.appendChild(doc.createTextNode(enablingMemoryTag));
                    
                    memoryNode.appendChild(transNameNode);
                    memoryNode.appendChild(policyNode);
                    transitionNode.appendChild(memoryNode);
                    break;
                case ageMemory:
                    memoryNode = doc.createElement(memoryMainTag);
                    transNameNode = doc.createElement(nodeNameTag);
                    transNameNode.appendChild(doc.createTextNode(transition.getName()));                    
                    policyNode= doc.createElement(memoryPolicyTag);
                    policyNode.appendChild(doc.createTextNode(ageMemoryTag));
                    
                    memoryNode.appendChild(transNameNode);
                    memoryNode.appendChild(policyNode);
                    transitionNode.appendChild(memoryNode);
                    break;
            }
        }
        for(InputEdge inputEdge: input){
            Node inputEdgeNode=inputEdge.getXMLNode(doc);
            transitionNode.appendChild(inputEdgeNode);
        }
        
        for(OutputEdge outputEdge : output){
            Node outputEdgeNode=outputEdge.getXMLNode(doc);
            transitionNode.appendChild(outputEdgeNode);
        }
        
        for(InhibitorEdge inhibitorEdge: inhibitor){
            Node inhibitorEdgeNode=inhibitorEdge.getXMLNode(doc);
            transitionNode.appendChild(inhibitorEdgeNode);
        }
        return transitionNode;
    }

    public void initInputEdges(HashSet<InputEdge> initInputEdges){
        this.input= new HashSet<>();
        this.input.addAll(initInputEdges);
    }
    
    public void initOutputEdges(HashSet<OutputEdge> initOutputEdges){
        this.output= new HashSet<>();
        this.output.addAll(initOutputEdges);
    }
    
    public void initInhibitorEdges(HashSet<InhibitorEdge> initInhibitorEdges){
        this.inhibitor= new HashSet<>();
        this.inhibitor.addAll(initInhibitorEdges);
    }
    
    public HashMap<BasicTimedTransition, MemoryPolicyAtTransition> getMemoryPolicyList() {
        return memoryPolicyList;
    }
    
    @Override
    public void addEdge(AbstractEdge edge){
        if (edge instanceof InputEdge){
            InputEdge inputEdge= (InputEdge)edge;
            this.input.add(inputEdge);
        }else if (edge instanceof InhibitorEdge){
            InhibitorEdge inhibitorEdge= (InhibitorEdge)edge;
            this.inhibitor.add(inhibitorEdge);
        }else if (edge instanceof OutputEdge){
            OutputEdge outputEdge= (OutputEdge)edge;
            this.output.add(outputEdge);
        }
    }
    @Override
    public void removeEdge(AbstractEdge edge){
        if (edge instanceof InputEdge){
            InputEdge inputEdge= (InputEdge)edge;
            input.remove(inputEdge);
        }else if (edge instanceof InhibitorEdge){
            InhibitorEdge inhibitorEdge=(InhibitorEdge)edge;
            inhibitor.remove(inhibitorEdge);
        }else if (edge instanceof OutputEdge){
            OutputEdge outputEdge=(OutputEdge)edge;
            output.remove(outputEdge);
        }
    }
    
    public void addMemoryPolicyAtFiring(BasicTimedTransition basicTimedTransition, MemoryPolicy memoryPolicy){
        MemoryPolicyAtTransition memoryPolicyAtTransition= new MemoryPolicyAtTransition(basicTimedTransition, memoryPolicy);
        if (!memoryPolicyAtTransition.getTransition().equals(basicTimedTransition)){
            memoryPolicyList.put(basicTimedTransition, memoryPolicyAtTransition);
        }        
    }
    
    public void replaceMemoryPolicyHashMap(HashMap<BasicTimedTransition, MemoryPolicyAtTransition> handledMemoryPolicyTransitions){
        this.memoryPolicyList= new HashMap<>();
        if (this instanceof BasicTimedTransition) {
            BasicTimedTransition current = (BasicTimedTransition) this;
            handledMemoryPolicyTransitions.remove(current);
        }
        this.memoryPolicyList.putAll(handledMemoryPolicyTransitions);
    }

    
    public void removeMemoryPolicyAtFiring(BasicTimedTransition transition){
        memoryPolicyList.remove(transition);
    }
    @Override
    public ArrayList<AbstractEdge> getConnectedEdges() {
        ArrayList<AbstractEdge> allEdges= new ArrayList<>();
        allEdges.addAll(input);
        allEdges.addAll(inhibitor);
        allEdges.addAll(output);
        
        return allEdges;
    }
    
    @Override
    public void setPosition(Point2D.Double centerPoint){
        this.shape.setX(centerPoint.x-this.getWidth()/2);
        
    }
        
}
