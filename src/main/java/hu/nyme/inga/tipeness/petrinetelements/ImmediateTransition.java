/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package hu.nyme.inga.tipeness.petrinetelements;

import hu.nyme.inga.tipeness.simulation.InvalidParamfileException;
import org.mvel2.MVEL;
import hu.nyme.inga.tipeness.simulation.ShowError;
import hu.nyme.inga.tipeness.simulation.NetState;
import java.awt.Color;
import java.awt.geom.Point2D;
import java.util.HashSet;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;


public class ImmediateTransition extends Transition {

    final int WIDTH=7;
    Color DEFAULTCOLOR=Color.BLACK;
    private double priority;
    private double weight;
    private String conditionString;       
    
    public ImmediateTransition(String transitionName, Point2D.Double insertLocation) {
        super(transitionName, insertLocation);
        this.priority = 1;
        this.weight = 1;
        this.getInnerShape().setPaint(DEFAULTCOLOR);
    } 

    public double getPriority() {
        return priority;
    }

    public double getWeight() {
        return weight;
    }

    public void setPriority(double priority) {
        this.priority = priority;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }

    @Override
    public String toString() {
        String nl = System.lineSeparator();
        StringBuilder sb = new StringBuilder(super.toString());
        sb.append("Priority: ").append(priority).append(nl);
        sb.append("Weight: ").append(weight).append(nl);
        sb.append("---------------------------------").append(nl);
        return sb.toString();
    }

    public void setConditionString(String conditionString) {
        this.conditionString = conditionString;
    }

    
    
    @Override
    public boolean isEnabled(NetState netState){
        if (!super.isEnabled(netState)) {
            return false;
        } else {
            if (conditionString != null) {
                try{
                    return parsePlaceNameInCondition(netState);
                }catch(InvalidParamfileException ipe){
                    System.exit(-1);
                }                
            }
            return true;
        }

    }

    private boolean parsePlaceNameInCondition(NetState netState) throws InvalidParamfileException{        
        String tempConString = conditionString;
        tempConString = tempConString.toLowerCase();
        String placeName;        
        try {
            tempConString = tempConString.toLowerCase();
            if (tempConString.equals("")) {
            } else {
                String placePatern = "#";
                while (tempConString.contains("#")) {
                    placeName = tempConString.substring(tempConString.indexOf("#") + 1);
                    placeName = placeName.substring(0, placeName.indexOf(placePatern));
                    Place foundPlace=null;
                    for (Place place: netState.getPlaces()){
                        if (place.getName().equals(placeName)){
                            foundPlace=place;
                        }
                    }
                    if (foundPlace!=null) {
                        tempConString = tempConString.replaceAll("#" + placeName + "#", Integer.toString(foundPlace.getCurrent()));
                    } else {
                        throw new Exception();
                    }
                }
                return (boolean)MVEL.eval(tempConString);
            }
        } catch (ClassCastException ce) {
            throw new InvalidParamfileException(ShowError.showError(ShowError.ErrorType.wrongFireCondition,  super.getName()));
        } catch (Exception e) {
            throw new InvalidParamfileException(ShowError.showError(ShowError.ErrorType.wrongFireCondition, super.getName()));
        }
        return false;
    }
    
    public static void parseCondition(String conditionString, HashSet<Place> places) throws InvalidParamfileException{        
        String tempConString = conditionString;
        tempConString = tempConString.toLowerCase();
        String placeName;        
        try {
            tempConString = tempConString.toLowerCase();
            if (tempConString.equals("")) {
            } else {
                String placePatern = "#";
                while (tempConString.contains("#")) {
                    placeName = tempConString.substring(tempConString.indexOf("#") + 1);
                    placeName = placeName.substring(0, placeName.indexOf(placePatern));
                    Place foundPlace=null;
                    for (Place place: places){
                        if (place.getName().equals(placeName)){
                            foundPlace=place;
                        }
                    }
                    if (foundPlace!=null) {
                        tempConString = tempConString.replaceAll("#" + placeName + "#", Integer.toString(foundPlace.getCurrent()));
                    } else {
                        throw new Exception();
                    }
                }
                MVEL.eval(tempConString);
            }
        } catch (ClassCastException ce) {
            throw new InvalidParamfileException("Wrong condition");
        } catch (Exception e) {
            throw new InvalidParamfileException("Wrong condition");
        }
    }

    @Override
    public int getShapeWidth(){
        return this.WIDTH;
    }

    @Override
    public Color getDefaultColor() {
        return DEFAULTCOLOR;
    }

    @Override
    public String getTransitionTag() {
        return immedTransitionTag;
    }
    
    @Override
    public Node getXMLNode(Document doc){
        Node immedTransitionNode= super.getXMLNode(doc);
        Element priorityNode= doc.createElement(priorityTag);
        priorityNode.appendChild(doc.createTextNode(String.valueOf(priority)));
        immedTransitionNode.appendChild(priorityNode);
        Element weightNode= doc.createElement(weightTag);
        weightNode.appendChild(doc.createTextNode(String.valueOf(weight)));
        immedTransitionNode.appendChild(weightNode);
        if (conditionString!=null){
            Element conditionNode= doc.createElement(conditionTag);
            String xmlConditionString=conditionString.replace("<", "&lt;").replace(">", "&gt;");
            conditionNode.appendChild(doc.createTextNode(xmlConditionString));
            immedTransitionNode.appendChild(conditionNode);
        }
        return immedTransitionNode;
    }
    @Override
    public Color getDefaultShapeFill() {
        return Color.BLACK;
    }

    public String getConditionString() {
        return conditionString;
    }
    
    
}
