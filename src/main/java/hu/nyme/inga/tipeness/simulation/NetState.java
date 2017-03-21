/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package hu.nyme.inga.tipeness.simulation;

import hu.nyme.inga.tipeness.petrinetelements.Place;
import hu.nyme.inga.tipeness.petrinetelements.BasicTimedTransition;
import hu.nyme.inga.tipeness.petrinetelements.ImmediateTransition;
import java.util.HashMap;
import java.util.HashSet;


public class NetState {

    private HashSet<Place> places;
    private HashSet<ImmediateTransition> immedTransitions;
    private HashSet<BasicTimedTransition> memoryTransitions;

    private double previousTime;
    private double time;
    private EventQueue eventqueue;
    private boolean endCurrentUnit;
    private Event currentEvent;

    public NetState(ConfigParser configParser) {
        this.time = 0.0;
        this.currentEvent = null;
        this.eventqueue = new EventQueue();

        this.places = configParser.copyPlaces();
        this.immedTransitions = configParser.getImmedTranstions();
        this.memoryTransitions = configParser.getMemoryTransitions();
    }

    public NetState(ConfigParser configParser, HashMap<String, Integer> placeList) {
        this.time = 0.0;
        this.currentEvent = null;
        this.eventqueue = new EventQueue();
        this.places = configParser.copyPlaces();
        this.immedTransitions = configParser.getImmedTranstions();
        this.memoryTransitions = configParser.getMemoryTransitions();
    }    

    public HashSet<Place> getPlaces() {
        return places;
    }

    public Place getPlaceByName(String placeName) {
        for(Place place: this.places){
            if (place.getName().equals(placeName)){
                return place;
            }
        }
        return null;
    }
    public EventQueue getEventqueue() {
        return eventqueue;
    }

    public Event getCurrentEvent(){        
        return currentEvent;
    }

    public void setCurrentEvent(Event currentEvent) {
        this.currentEvent = currentEvent;
    }

    public void setTime(double time) {
        this.time = time;
    }

    public HashSet<ImmediateTransition> getImmedTransitions() {
        return immedTransitions;
    }

    public HashSet<BasicTimedTransition> getMemoryTransitions() {
        return memoryTransitions;
    }

    public double getTime() {
        return time;
    }

    public double getPreviousTime() {
        return previousTime;
    }

    public boolean isEndCurrentUnit() {
        return endCurrentUnit;
    }

    public void setPreviousTime(double previousTime) {
        this.previousTime = previousTime;
    }

}
