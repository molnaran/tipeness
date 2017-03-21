/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hu.nyme.inga.tipeness.petrinetelements;

import java.awt.Color;
import java.awt.geom.Point2D;
import org.piccolo2d.PNode;
import org.piccolo2d.nodes.PText;

/**
 *
 * @author Andrew
 */
public class NodeText extends PNode implements Highlightable{
    PText nameNode;
    
    public NodeText(String name) {
        this.nameNode= new PText(name);
        this.addChild(nameNode);
    }
    public PText getNameNode() {
        return nameNode;
    }
    
    @Override
    public void setName(String name){
        this.nameNode.setText(name);
    }
    
    public void setTextNodePosition(Point2D.Double point){
        nameNode.setX(point.x);
        nameNode.setY(point.y);
    }
    
    @Override
    public double getWidth(){
        return this.nameNode.getWidth();
    }
    
    public String getText(){
        return this.nameNode.getText();
    }
    
    public void setText(String text){
        this.nameNode.setText(text);
    }
    
    
    public double getXWOffset(){
        return this.nameNode.getX()+this.getNameNode().getXOffset();
    }
    public double getYWOffset(){
        return this.nameNode.getY()+this.getNameNode().getYOffset();
    }
    @Override
    public void highlightNode(Color color){
        this.nameNode.setTextPaint(color);
    }
    
    @Override
    public void removeHighlight() {
        this.nameNode.setPaint(Color.BLACK);
    }
}
