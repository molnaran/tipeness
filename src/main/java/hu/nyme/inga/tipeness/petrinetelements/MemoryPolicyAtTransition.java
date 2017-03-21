/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hu.nyme.inga.tipeness.petrinetelements;

import hu.nyme.inga.tipeness.petrinetelements.Transition.MemoryPolicy;
import java.util.Objects;

/**
 *
 * @author Andrew
 */
public class MemoryPolicyAtTransition {
    BasicTimedTransition transition;
    public MemoryPolicy memoryPolicy;

    public MemoryPolicyAtTransition(BasicTimedTransition transition) {
        this.transition = transition;
        this.memoryPolicy = MemoryPolicy.enablingMemory;
    }
    
    public MemoryPolicyAtTransition(BasicTimedTransition transition, MemoryPolicy memoryPolicy) {
        this.transition = transition;
        this.memoryPolicy = memoryPolicy;
    }
    
    

    @Override
    public String toString() {
        return transition.getName() + ": " + memoryPolicy;
    }

    public BasicTimedTransition getTransition() {
        return transition;
    }

    public void setTransitionName(BasicTimedTransition transition) {
        this.transition = transition;
    }

    public MemoryPolicy getMemoryPolicy() {
        return memoryPolicy;
    }

    public void setMemoryPolicy(MemoryPolicy memoryPolicy) {
        this.memoryPolicy = memoryPolicy;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 47 * hash + Objects.hashCode(this.transition);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final MemoryPolicyAtTransition other = (MemoryPolicyAtTransition) obj;
        if (!Objects.equals(this.transition, other.transition)) {
            return false;
        }
        return true;

    }
    
    
}
