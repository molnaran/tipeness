/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package hu.nyme.inga.tipeness.simulation;

import hu.nyme.inga.tipeness.petrinetelements.Transition;


public class Event {

    private final double occurenceTime;
    private final Transition transition;
    private final int enablingDegree;

    public Event(Transition transition, double time, NetState netState) {
        this.transition = transition;
        this.occurenceTime = time;
        this.enablingDegree = transition.getEnablingDegree(netState);
    }

    public Transition getTransition() {
        return transition;
    }

    public double getTime() {
        return occurenceTime;
    }


    public int getEnablingDegree() {
        return enablingDegree;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("-------------------------------------\n");
        sb.append("Event transition: ").append(transition.getName()).append("\n");
        sb.append("Time: ").append(occurenceTime).append("\n");
        sb.append("Enabling degree: ").append(enablingDegree).append("\n");
        sb.append("-------------------------------------\n");
        return sb.toString();
    }
}
