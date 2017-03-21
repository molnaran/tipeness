/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hu.nyme.inga.tipeness.petrinetelements;

import java.awt.geom.Point2D;

/**
 *
 * @author Andrew
 */
public class BreakpointXMLNode implements Comparable<BreakpointXMLNode>{
    public int number;
    public Point2D position;

    public BreakpointXMLNode(int number, Point2D position) {
        this.number = number;
        this.position = new Point2D.Double(position.getX(), position.getY());
    }

    @Override
    public int compareTo(BreakpointXMLNode compareToBreakpoint) {
        if (this.number<compareToBreakpoint.getNumber()){
            return -1;
        }else if (this.number>compareToBreakpoint.getNumber()){
            return 1;
        }else{
            return 0;
        }        
    }

    public int getNumber() {
        return number;
    }

    public Point2D getPosition() {
        return this.position;
    }

    @Override
    public String toString() {
        return "BreakpointXMLNode{" + "position=" + position + '}';
    }
    
}
