/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hu.soe.inga.tipeness.petrinetelements;

import hu.soe.inga.tipeness.petrinetelements.Transition;
import java.util.ArrayList;

/**
 *
 * @author Andrew
 */
public class OutputEdge extends AbstractIOEdge{

    public OutputEdge(Transition start, Place end) {
        super(start, end);
    } 
    
    public OutputEdge(PetriNetNode start, PetriNetNode end, ArrayList<BreakpointXMLNode> breakpointXMLNodeList) {
        super(start, end, breakpointXMLNodeList);
        
    }
    @Override
    public String getConnectedPlaceName() {
        return ((Place)end).getName();
    }

    @Override
    public String getEdgeTypeTag() {
        return outputEdgeTag;
    }
    @Override
    public Transition getConnectedTransition() {
        return (Transition)start;
    }
}
