/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package hu.soe.inga.tipeness.petrinetelements;

import hu.soe.inga.tipeness.simulation.InvalidParamfileException;
import hu.soe.inga.tipeness.simulation.NetState;
import hu.soe.inga.tipeness.simulation.ShowError;
import static hu.soe.inga.tipeness.simulation.XMLConstants.delayTag;
import java.awt.Color;
import java.awt.geom.Point2D;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import umontreal.iro.lecuyer.randvar.ExponentialGen;

public class ExponentialTransition extends BasicTimedTransition {

    Color DEFAULTCOLOR = Color.WHITE;
    private ExponentialGen exponentialGen;

    @Override
    public String getTransitionTag() {
        return expTransitionTag;
    }

    public enum ServerType {

        exclusive, infinite
    };
    private ServerType sType;

    public ExponentialTransition(String transitionName, Point2D.Double insertLocation) {
        super(transitionName, insertLocation);
        this.exponentialGen = new ExponentialGen(this.getRandom(), 1);
        this.sType = ServerType.exclusive;
        this.getInnerShape().setPaint(Color.WHITE);
    }

    @Override
    public Color getDefaultColor() {
        return DEFAULTCOLOR;
    }

    @Override
    public void generateWorkTime(NetState netState) {
        if (!isEnabled(netState)) {
            return;
        }
        if (sType == ServerType.infinite) {
            ExponentialGen tempgen = new ExponentialGen(this.getRandom(), ((this.exponentialGen.getLambda()) * this.getEnablingDegree(netState)));
            this.setRemTime(tempgen.nextDouble());
        } else {
            this.setRemTime(exponentialGen.nextDouble());
        }
    }

    public ServerType getsType() {
        return sType;
    }

    public void setsType(ServerType sType) {
        this.sType = sType;
    }

    @Override
    public String toString() {
        String nl = System.lineSeparator();
        StringBuilder sb = new StringBuilder(super.toString());
        switch (sType) {
            case exclusive:
                sb.append("Servertype: exclusive").append(nl);
                break;
            case infinite:
                sb.append("Servertype: infinite").append(nl);
                break;
        }
        sb.append("---------------------------------------------").append(nl);
        return sb.toString();
    }

    @Override
    public Node getXMLNode(Document doc) {
        Node expTransititionNode = super.getXMLNode(doc);
        Node delayNode = doc.createElement(delayTag);
        double delay = 1 / this.exponentialGen.getLambda();
        delayNode.appendChild(doc.createTextNode(String.valueOf(delay)));

        expTransititionNode.appendChild(delayNode);
        return expTransititionNode;
    }

    public void setExpGen(double delay) {
        this.exponentialGen = new ExponentialGen(getRandom(), 1 / delay);
    }

    @Override
    public Color getDefaultShapeFill() {
        return Color.WHITE;
    }

    public double getDelay() {
        return 1 / this.exponentialGen.getLambda();
    }
}
