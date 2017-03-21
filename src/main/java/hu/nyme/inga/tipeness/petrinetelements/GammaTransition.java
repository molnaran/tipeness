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
import umontreal.iro.lecuyer.randvar.GammaGen;


public class GammaTransition extends BasicTimedTransition {

    Color DEFAULTCOLOR = Color.GRAY;
    private GammaGen gammaGen;      

    public GammaTransition(String transitionName, Point2D.Double insertLocation) {
        super(transitionName, insertLocation);
        gammaGen = new GammaGen(getRandom(), 1, 1);
    }   
    
    @Override
    public void generateWorkTime(NetState netState) {
        this.setRemTime(gammaGen.nextDouble());
    }

    @Override
    public String toString() {
        String nl = System.lineSeparator();
        StringBuilder sb = new StringBuilder(super.toString());
        sb.append("Shape parameter: ").append(this.gammaGen.getAlpha()).append(nl);
        sb.append("Rate parameter: ").append(this.gammaGen.getLambda()).append(nl);
        sb.append("-------------------------------------------------").append(nl);
        return sb.toString();
    }

    @Override
    public Color getDefaultColor() {
        return DEFAULTCOLOR;
    }

    public double getShape(){
        return this.gammaGen.getAlpha();
    }
    
    public double getRate(){
        return this.gammaGen.getLambda();
    }
    
    public void setGammaGen(double shape, double rate){       
        this.gammaGen = new GammaGen(getRandom(), shape, rate);
    }

    @Override
    public Node getXMLNode(Document doc) {
        Node gammaTransititionNode = super.getXMLNode(doc);
        Node shapeNode = doc.createElement(gammaShapeTag);
        double gammaShape=this.gammaGen.getAlpha();
        shapeNode.appendChild(doc.createTextNode(String.valueOf(gammaShape)));
        gammaTransititionNode.appendChild(shapeNode);
        
        Node rateNode= doc.createElement(gammaRateTag);
        double gammeRate=this.gammaGen.getLambda();
        rateNode.appendChild(doc.createTextNode(String.valueOf(gammeRate)));
        gammaTransititionNode.appendChild(rateNode);
        return gammaTransititionNode;
    }
    
    @Override
    public String getTransitionTag() {
        return gammaTransitionTag;
    }
    @Override
    public Color getDefaultShapeFill() {
        return Color.DARK_GRAY;
    }
}
