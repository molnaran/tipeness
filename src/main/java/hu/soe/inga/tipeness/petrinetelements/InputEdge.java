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
public class InputEdge extends AbstractIOEdge{

    public InputEdge(Place start, Transition end) {
        super(start, end);
    }
    public InputEdge(PetriNetNode start, PetriNetNode end, ArrayList<BreakpointXMLNode> breakpointXMLNodeList) {
        super(start, end, breakpointXMLNodeList);
        
    }
    @Override
    public String getConnectedPlaceName(){
        return ((Place)start).getName();
    }

    @Override
    public String getEdgeTypeTag() {
        return inputEdgeTag;
    }

    @Override
    public Transition getConnectedTransition() {
        return (Transition)end;
    }
}
