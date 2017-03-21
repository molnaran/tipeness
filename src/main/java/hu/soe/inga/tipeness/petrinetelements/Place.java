/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package hu.soe.inga.tipeness.petrinetelements;

import hu.soe.inga.tipeness.simulation.XMLConstants;
import java.awt.BasicStroke;
import java.awt.Color;
import static java.awt.Component.CENTER_ALIGNMENT;
import java.awt.geom.Point2D;
import java.io.Serializable;
import java.util.ArrayList;
import org.piccolo2d.nodes.PPath;
import org.piccolo2d.nodes.PText;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

public class Place extends PetriNetNode implements Serializable, XMLConstants {

    int DIAMETER = 30;
    Color DEFAULTCOLOR = Color.WHITE;
    ArrayList<AbstractEdge> edges;
    PText tokenText;

    private int currentToken;
    
    public Place(String name, Point2D.Double locationShape) {
        super(name);
        this.currentToken = 0;
        this.edges = new ArrayList<>();
        initShape(locationShape, currentToken);
    }    

    public int getCurrent() {
        return currentToken;
    }

    public void decreaseToken(int delta) {
        this.currentToken -= delta;
    }

    public void increaseToken(int delta) {
        this.currentToken += delta;
    }

    @Override
    public String toString() {
        String nl = System.lineSeparator();
        StringBuilder sb = new StringBuilder();
        sb.append("Placename: ").append(this.getName()).append(nl);
        sb.append("Tokennumber: ").append(this.currentToken).append(nl);
        sb.append("---------------------------------").append(nl);
        return sb.toString();
    }

    public Place copy(){
        Place copyPlace=new Place(this.getName(), this.getShapeCenter());
        copyPlace.setTokenNumber(currentToken);
        return copyPlace;
    }    

    private void initShape(Point2D.Double shapeLocation, int tokenNum) {
        shape = PPath.createEllipse(shapeLocation.x, shapeLocation.y, DIAMETER, DIAMETER);
        this.addChild(shape);
        shape.setPaint(DEFAULTCOLOR);
        shape.setStroke(new BasicStroke(2));

        createNameNode();

        tokenText = new PText(String.valueOf(tokenNum));
        tokenText.setPickable(false);
        tokenText.lowerToBottom();
        shape.addChild(tokenText);

        tokenText.setHorizontalAlignment(CENTER_ALIGNMENT);
        tokenText.setX(shape.getBounds().getCenterX() - tokenText.getWidth() / 2);
        tokenText.setY(shape.getBounds().getCenterY() - tokenText.getHeight() / 2);

    }

    @Override
    public Point2D.Double getIntersectionPoint(Point2D.Double lastBreakpoint) {
        double circleCenterX = this.getInnerShape().getGlobalBounds().getCenterX();
        double circleCenterY = this.getInnerShape().getGlobalBounds().getCenterY();

        double lineStartX = lastBreakpoint.getX();
        double lineStartY = lastBreakpoint.getY();
        double atfogo = Math.sqrt(Math.pow(lineStartX - circleCenterX, 2) + Math.pow(lineStartY - circleCenterY, 2));

        double intersectDistX = (Math.abs(lineStartX - circleCenterX) * this.DIAMETER / 2) / atfogo;
        double intersectDistY = (Math.abs(lineStartY - circleCenterY) * this.DIAMETER / 2) / atfogo;

        Point2D.Double point = new Point2D.Double();
        if (lineStartX > circleCenterX) {
            point.x = circleCenterX + intersectDistX;
        } else {
            point.x = circleCenterX - intersectDistX;
        }

        if (lineStartY > circleCenterY) {
            point.y = circleCenterY + intersectDistY;
        } else {
            point.y = circleCenterY - intersectDistY;
        }
        return point;
    }
    
    public Point2D.Double getShapeCenter() {
        return new Point2D.Double(shape.getFullBoundsReference().getCenterX(), shape.getFullBoundsReference().getCenterY());
    }

    public int getDIAMETER() {
        return DIAMETER;
    }

    public ArrayList<AbstractEdge> getEdges() {
        return this.edges;
    }
    
    public void setTokenNumber(int tokenNumber){
        this.currentToken=tokenNumber;
        this.tokenText.setText(String.valueOf(tokenNumber));
        setTextPosition();
    }

    @Override
    public void setPosition(Point2D.Double position){        
        tokenText.setHorizontalAlignment(CENTER_ALIGNMENT);
        tokenText.setX(this.getInnerShape().getBounds().getCenterX() - tokenText.getWidth() / 2);
        tokenText.setY(this.getInnerShape().getBounds().getCenterY() - tokenText.getHeight() / 2);
        
    }
    
    private void setTextPosition(){        
        tokenText.setHorizontalAlignment(CENTER_ALIGNMENT);
        tokenText.setX(this.getInnerShape().getBounds().getCenterX() - tokenText.getWidth() / 2);
        tokenText.setY(this.getInnerShape().getBounds().getCenterY() - tokenText.getHeight() / 2);
        
    }
    @Override
    public Node getXMLNode(Document doc) {
        Node placeNode = super.getXMLNode(doc);
        Element tokenNumNode = doc.createElement(tokenNumTag);

        placeNode.appendChild(tokenNumNode);
        tokenNumNode.appendChild(doc.createTextNode(String.valueOf(this.getCurrent())));

        return placeNode;
    }

    @Override
    public String getTransitionTag() {
        return placeTag;
    }

    @Override
    public void addEdge(AbstractEdge edge) {
        this.edges.add(edge);
    }

    @Override
    public void removeEdge(AbstractEdge edge) {
        this.edges.remove(edge);
    }

    @Override
    public ArrayList<AbstractEdge> getConnectedEdges() {
        return this.edges;
    }

    @Override
    public Color getDefaultShapeFill() {
        return Color.WHITE;
    }
    
    @Override
    public void highlightNode(Color color){
        super.highlightNode(color);
        this.tokenText.setTextPaint(Color.WHITE);
    }

    @Override
    public void removeHighlight(){
        super.removeHighlight();
        this.tokenText.setTextPaint(Color.black);
    }
    
}
