/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package hu.nyme.inga.tipeness.simulation;

import hu.nyme.inga.tipeness.petrinetelements.MemoryPolicyAtTransition;
import hu.nyme.inga.tipeness.petrinetelements.Transition;
import hu.nyme.inga.tipeness.petrinetelements.ExponentialTransition;
import java.util.ArrayList;
import java.util.HashMap;
import hu.nyme.inga.tipeness.statistics.Statistics;
import hu.nyme.inga.tipeness.petrinetelements.BasicTimedTransition;
import hu.nyme.inga.tipeness.petrinetelements.ImmediateTransition;
import static hu.nyme.inga.tipeness.petrinetelements.Transition.MemoryPolicy.ageMemory;
import static hu.nyme.inga.tipeness.petrinetelements.Transition.MemoryPolicy.enablingMemory;
import static hu.nyme.inga.tipeness.petrinetelements.Transition.MemoryPolicy.resampling;


public class EventQueue {

    private final ArrayList<Event> eventQueue;

    public EventQueue() {
        this.eventQueue = new ArrayList<>();
    }

    public void updateQueue(ConfigParser configParser, NetState netState) throws OutOfEventException {
        updateImmedTransitions(netState);
        if (netState.getCurrentEvent() == null) {
            for (BasicTimedTransition basicTimedTransition : netState.getMemoryTransitions()) {
                if (basicTimedTransition instanceof ExponentialTransition) {
                    ExponentialTransition exponentialTransition = (ExponentialTransition) basicTimedTransition;
                    updateExpTransition(exponentialTransition, netState);
                } else {
                    BasicTimedTransition timedTranstion = (BasicTimedTransition) basicTimedTransition;
                    updateTimedTransitionWResampling(timedTranstion, netState);
                }
            }
        } else {
            Transition firedTransition = netState.getCurrentEvent().getTransition();
            HashMap<BasicTimedTransition, MemoryPolicyAtTransition> affectedTransitions = firedTransition.getMemoryPolicyList();
            for (BasicTimedTransition basicTimedTransition : netState.getMemoryTransitions()) {                
                if (affectedTransitions.containsKey(basicTimedTransition)) {                    
                    if (basicTimedTransition instanceof ExponentialTransition) {
                        ExponentialTransition exponentioalTransition = (ExponentialTransition) basicTimedTransition;
                        updateExpTransition(exponentioalTransition, netState);
                    } else {
                        switch (affectedTransitions.get(basicTimedTransition).getMemoryPolicy()) {
                            case resampling:
                                updateTimedTransitionWResampling(basicTimedTransition, netState);
                                break;
                            case enablingMemory:
                                updateTimedTransitionWEnabling(basicTimedTransition, netState);
                                break;
                            case ageMemory:
                                updateTimedTransitionWAgeMemory(basicTimedTransition, netState);
                                break;
                        }
                    }
                }else{
                    if (basicTimedTransition instanceof ExponentialTransition) {
                        ExponentialTransition exponentioalTransition = (ExponentialTransition) basicTimedTransition;
                        updateExpTransition(exponentioalTransition, netState);
                    } else {
                        BasicTimedTransition timedTransition = (BasicTimedTransition) basicTimedTransition;
                        updateTimedTransitionWEnabling(timedTransition, netState);
                    }
                }
            }
        }

    }

    private void updateTimedTransitionWResampling(BasicTimedTransition timedTransition, NetState netState) {
        removeEvent(getEventByTransName(timedTransition));
        timedTransition.generateWorkTime(netState);
        if (timedTransition.isEnabled(netState)) {
            addEvent(new Event(timedTransition, netState.getTime() + timedTransition.getRemTime(), netState));
        }
    }

    private void updateTimedTransitionWEnabling(BasicTimedTransition timedTransition, NetState netState) {
        if (timedTransition.isEnabled(netState)) {
            if (getEventByTransName(timedTransition) == null) {
                addEvent(new Event(timedTransition, netState.getTime() + timedTransition.getRemTime(), netState));
            }
        } else {
            if (getEventByTransName(timedTransition) != null) {
                removeEvent(getEventByTransName(timedTransition));
                timedTransition.generateWorkTime(netState);
            }
        }
    }

    private void updateTimedTransitionWAgeMemory(BasicTimedTransition timedTransition, NetState netState) {
        if (timedTransition.isEnabled(netState)) {
            if (getEventByTransName(timedTransition) == null) {
                addEvent(new Event(timedTransition, netState.getTime() + timedTransition.getRemTime(), netState));    
            }
        } else {
            if (getEventByTransName(timedTransition) != null) {
                timedTransition.setRemTime(getEventByTransName(timedTransition).getTime() - netState.getTime());
                removeEvent(getEventByTransName(timedTransition));
            }
        }

    }

    private void updateExpTransition(ExponentialTransition exponentialTransition, NetState netState) {
        if (!exponentialTransition.isEnabled(netState)) {
            removeEvent(getEventByTransName(exponentialTransition));
        } else {
            if (exponentialTransition.getsType() == ExponentialTransition.ServerType.exclusive) {
                if (getEventByTransName(exponentialTransition) == null) {
                    exponentialTransition.generateWorkTime(netState);
                    addEvent(new Event(exponentialTransition, netState.getTime() + exponentialTransition.getRemTime(), netState));
                }
            } else {
                if (getEventByTransName(exponentialTransition) == null) {
                    exponentialTransition.generateWorkTime(netState);
                    addEvent(new Event(exponentialTransition, netState.getTime() + exponentialTransition.getRemTime(), netState));
                } else {
                    if (getEventByTransName(exponentialTransition).getEnablingDegree() != exponentialTransition.getEnablingDegree(netState)) {
                        removeEvent(getEventByTransName(exponentialTransition));
                        exponentialTransition.generateWorkTime(netState);
                        addEvent(new Event(exponentialTransition, netState.getTime() + exponentialTransition.getRemTime(), netState));
                    }
                }
            }
        }
    }

    private void updateImmedTransitions(NetState netState) {
        if (!netState.getImmedTransitions().isEmpty()) {
            ArrayList<ImmediateTransition> maxPriorityList = new ArrayList<>();
            for (ImmediateTransition immedtranTransition : netState.getImmedTransitions()) {
                if (immedtranTransition.isEnabled(netState)) {
                    if (maxPriorityList.isEmpty()) {
                        maxPriorityList.add(immedtranTransition);
                    } else {
                        if (immedtranTransition.getPriority() > maxPriorityList.get(0).getPriority()) {
                            maxPriorityList.clear();
                            maxPriorityList.add(immedtranTransition);
                        } else if (immedtranTransition.getPriority() == maxPriorityList.get(0).getPriority()) {
                            maxPriorityList.add(immedtranTransition);
                        }
                    }
                } else {
                    Event event = getEventByTransName(immedtranTransition);
                    if (event != null) {
                        removeEvent(event);
                    }
                }
            }
            if (!maxPriorityList.isEmpty()) {
                ImmediateTransition immediateTransition = getRandomElementwWeight(maxPriorityList, netState);
                addEvent(new Event(immediateTransition, netState.getTime(), netState));
            }
        }
    }

    private void addEvent(Event newEvent) {
        if (eventQueue.isEmpty()) {
            eventQueue.add(newEvent);
        } else {
            for (int i = 0; i < eventQueue.size(); i++) {
                if (newEvent.getTime() < eventQueue.get(i).getTime()) {
                    eventQueue.add(i, newEvent);
                    return;
                }
            }
            eventQueue.add(newEvent);
        }
    }

    private void removeEvent(Event remEvent) {
        eventQueue.remove(remEvent);
    }

    public Event getNextEvent() throws OutOfEventException {
        if (!eventQueue.isEmpty()) {
            return eventQueue.remove(0);
        }else{
            throw new OutOfEventException();
        }
    }

    public ArrayList<Event> getEventQueue() {
        return eventQueue;
    }

    private Event getEventByTransName(Transition transition) {
        for (Event event : eventQueue) {
            if (event.getTransition().equals(transition)) {                
                return event;
            }
        }
        return null;
    }

    private ImmediateTransition getRandomElementwWeight(ArrayList<ImmediateTransition> concurrentTransitionList, NetState netState) {
        HashMap<ImmediateTransition, Double> minList = new HashMap<>();
        for (ImmediateTransition immediateTransition : concurrentTransitionList) {
            minList.put(immediateTransition, immediateTransition.getWeight());
        }
        ImmediateTransition immediateTransition = null;
        try {
            immediateTransition = Statistics.getRandomElementFromWeightedElements(minList);
        } catch (Exception e) {
            System.out.println("Error at the weightcalculation!");
        }
        return immediateTransition;
    }
}
