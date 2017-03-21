/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hu.soe.inga.tipeness.petrinetelements;

import java.awt.BasicStroke;
import java.awt.Color;
import org.piccolo2d.PNode;
import org.piccolo2d.nodes.PPath;

/**
 *
 * @author Andrew
 */
public class Link extends PNode{

    EndpointInterface start;
    EndpointInterface end;
    PPath line;
    AbstractEdge parentEdge;

    public Link(EndpointInterface start, EndpointInterface end, AbstractEdge parentEdge) {
        this.start = start;
        this.end = end;
        line = PPath.createLine(start.getPoint().x, start.getPoint().y, end.getPoint().x, end.getPoint().y);
        line.setStroke(new BasicStroke(2));
        this.addChild(line);
        this.parentEdge = parentEdge;
    }

    public PPath getLine() {
        return line;
    }

    public void updateLink() {

//        line.reset();
//        line=PPath.createLine(start.getPoint().x, start.getPoint().y, end.getPoint().x, end.getPoint().y);
        line.reset();
        line = PPath.createLine(start.getPoint().x, start.getPoint().y, end.getPoint().x, end.getPoint().y);
        line.setStroke(new BasicStroke(2));
        this.addChild(line);
        line.lowerToBottom();

    }

    public void highlight(Color color) {
        line.setStrokePaint(color);
    }

    public AbstractEdge getParentEdge() {
        return parentEdge;
    }
    
}
