/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package hu.soe.inga.tipeness.statdata;

import java.io.Serializable;


public class StatValues implements Serializable {

    public double avg;
    public double variance;

    public StatValues copy() {
        StatValues curr = new StatValues();
        curr.avg = this.avg;
        curr.variance = this.variance;
        return curr;
    }

    @Override
    public String toString() {
        String nl = System.lineSeparator();
        StringBuilder sb = new StringBuilder();
        sb.append("Average: ").append(avg).append(nl);
        sb.append("Variance: ").append(variance).append(nl);
        return sb.toString();
    }
}
