/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hu.soe.inga.tipeness.petrinetelements;

import hu.soe.inga.tipeness.simulation.NetState;
import static hu.soe.inga.tipeness.simulation.XMLConstants.delayTag;
import java.awt.Color;
import java.awt.geom.Point2D;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import umontreal.iro.lecuyer.randvar.ConstantGen;

public class DeterministicTransition extends BasicTimedTransition {

    Color DEFAULTCOLOR = Color.BLACK;
    private ConstantGen constantGen;

    public DeterministicTransition(String transitionName, Point2D.Double insertLocation){
        super(transitionName, insertLocation);
        this.getInnerShape().setPaint(DEFAULTCOLOR);        
        this.constantGen = new ConstantGen(1);
    }

    @Override
    public void generateWorkTime(NetState netState) {
        this.setRemTime(constantGen.nextDouble());
    }

    @Override
    public String toString() {
        String nl = System.lineSeparator();
        String sb = super.toString() + "-------------------------------------------" + nl;
        return sb;
    }
    

    @Override
    public Color getDefaultColor() {
        return DEFAULTCOLOR;
    }

    @Override
    public String getTransitionTag() {
        return detTransitionTag;
    }

    @Override
    public Node getXMLNode(Document doc){
        Node basicTimedTransititionNode=super.getXMLNode(doc);
        Node delayNode= doc.createElement(delayTag);
        double delay=this.constantGen.nextDouble();
        delayNode.appendChild(doc.createTextNode(String.valueOf(delay)));
        
        basicTimedTransititionNode.appendChild(delayNode);
        return basicTimedTransititionNode;
    }

    public void setConstantGen(double delay) {
        this.constantGen = new ConstantGen(delay);
    }

    @Override
    public Color getDefaultShapeFill() {
        return Color.BLACK;
    }
    
    public double getDelay(){
        return this.constantGen.nextDouble();
    }
}
