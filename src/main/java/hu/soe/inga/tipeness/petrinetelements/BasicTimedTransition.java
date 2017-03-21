/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hu.soe.inga.tipeness.petrinetelements;

import hu.soe.inga.tipeness.simulation.NetState;
import java.awt.geom.Point2D;
import umontreal.iro.lecuyer.rng.RandRijndael;


public abstract class BasicTimedTransition extends Transition {

    final int WIDTH = 20;    
    private String removedByTransition;
    private final RandRijndael rrj;
    private boolean hasRemTime;
    private double remainingTime;
    
    public BasicTimedTransition(String transitionName, Point2D.Double insertLocation) {
        super(transitionName, insertLocation);     
        this.rrj = new RandRijndael();
        this.hasRemTime = false;
    }   

    public abstract void generateWorkTime(NetState netState);

    @Override
    public void fire(NetState netState) {
        super.fire(netState);
        this.hasRemTime = false;
        this.generateWorkTime(netState);
    }

    public RandRijndael getRandom() {
        return rrj;
    }

    public double getRemTime() {
        return remainingTime;
    }

    public void setRemTime(double remTime) {
        this.remainingTime = remTime;
    }

    public boolean isHasRemTime() {
        return hasRemTime;
    }

    public void setHasRemTime(boolean hasRemTime) {
        this.hasRemTime = hasRemTime;
    }

    public String getRemovedByTransition() {
        return removedByTransition;
    }

    public void setRemovedByTransition(String removedByTransition) {
        this.removedByTransition = removedByTransition;
    }
    
    @Override
    public int getShapeWidth() {
        return this.WIDTH;
    }    
}
