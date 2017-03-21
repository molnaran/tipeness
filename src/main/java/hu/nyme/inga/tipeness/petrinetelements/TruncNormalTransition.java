/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package hu.nyme.inga.tipeness.petrinetelements;

import hu.nyme.inga.tipeness.simulation.InvalidParamfileException;
import hu.nyme.inga.tipeness.simulation.ShowError;
import hu.nyme.inga.tipeness.simulation.NetState;
import java.awt.Color;
import java.awt.geom.Point2D;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import umontreal.iro.lecuyer.randvar.NormalGen;


public class TruncNormalTransition extends BasicTimedTransition {

    Color DEFAULTCOLOR = Color.GRAY;
    private NormalGen normalGen;
    private double mean;
    private double variance;
    
    public TruncNormalTransition(String transitionName, Point2D.Double insertLocation) {
        super(transitionName, insertLocation);
        this.normalGen = new NormalGen(getRandom());
        this.getInnerShape().setPaint(DEFAULTCOLOR);
    } 
    
    
    @Override
    public String toString() {
        String nl = System.lineSeparator();
        StringBuilder sb = new StringBuilder(super.toString());
        sb.append("Mean: ").append(mean).append(nl);
        sb.append("Variance: ").append(variance).append(nl);
        sb.append("---------------------------------------").append(nl);
        return sb.toString();
    }
    
    @Override
    public void generateWorkTime(NetState netState) {
        double time = normalGen.nextDouble();
        while (time < 0) {
            time = normalGen.nextDouble();
        }
        this.setRemTime(time);
    }
    
    @Override
    public Color getDefaultColor() {
        return DEFAULTCOLOR;
    }

    public void setNormalGen(double mean, double variance){     
        this.normalGen = new NormalGen(getRandom(), mean, variance);
    }

    @Override
    public Node getXMLNode(Document doc){
        Node normalTransititionNode=super.getXMLNode(doc);
        Node normalMeanNode= doc.createElement(normalMeanTag);
        double normalMean=this.normalGen.getMu();
        normalMeanNode.appendChild(doc.createTextNode(String.valueOf(normalMean)));
        
        Node varianceNode= doc.createElement(normalVarianceTag);
        double normalVariance=this.normalGen.getSigma();
        varianceNode.appendChild(doc.createTextNode(String.valueOf(normalVariance)));
        normalTransititionNode.appendChild(normalMeanNode);
        normalTransititionNode.appendChild(varianceNode);
        return normalTransititionNode;
    }

    @Override
    public String getTransitionTag() {
        return normalTransitionTag;
    }
    
    @Override
    public Color getDefaultShapeFill() {
        return Color.GRAY;
    }
    
    public double getMean(){
        return this.normalGen.getMu();
    }
    
    public double getVariance(){
        return this.normalGen.getSigma();
    }    
    
}
