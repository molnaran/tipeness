/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package hu.soe.inga.tipeness.statistics;

import hu.soe.inga.tipeness.petrinetelements.ImmediateTransition;
import java.util.HashMap;
import java.util.Random;
import umontreal.iro.lecuyer.probdist.StudentDist;
import umontreal.iro.lecuyer.probdist.StudentDistQuick;


public class Statistics {

    public static ConfidenceInterval confidenceEV(double avg, int n, double variance, double alpha) {

        double deviation = Math.sqrt(variance);
        double t = StudentDistQuick.inverseF(n, 1 - (alpha / 2));
        double error = t * deviation / Math.sqrt(n);
        return new ConfidenceInterval(avg - error, avg + error);
    }

    public static double variance(double value, double prevVariance, double previousAvg,
            double avg, double previousTime, double time) {
        double newVariance = 0;
        double d = avg - previousAvg;
        if ((time) != 0) {
            newVariance = (previousTime / time) * prevVariance;
            newVariance += ((time - previousTime) * Math.pow((value - previousAvg), 2)) / time;
            newVariance += Math.pow(d, 2) + 2 * previousAvg * d - 2 * d * avg;
        }
        return newVariance;
    }

    public static double avgWithContinousTime(double value, double oldavg, double previousTime, double time) {
        if ((time) > 0) {
            double newAvg = oldavg * (previousTime) + (time - previousTime) * value;
            newAvg /= time;
            return newAvg;
        } else {
            return oldavg;
        }
    }

    public static double avgWithDistinctWeights(double currValue, double currWeight, double oldAvg, double oldWeight) {
        if (currWeight + oldWeight > 0) {
            double newavg = oldAvg * oldWeight + currWeight * currValue;
            newavg /= currWeight + oldWeight;
            return newavg;
        }
        return oldAvg;
    }

    public static boolean intervalEstimationEV(double newValue, double avg, int n, double variance, double alpha) {
        ConfidenceInterval con = confidenceEV(avg, n, variance, alpha);
        return (con.min < newValue && newValue < con.max);
    }

    public static double getAbsError(double avg, double accuracy) {
        return Math.abs(avg * accuracy);
    }

    public static boolean isAccurateEV(double avg, int n, double variance, double accuracy, double alpha) {
        double relerror = (avg * accuracy);
        double deviation = Math.sqrt(variance);
        double conferror = StudentDistQuick.inverseF(n, 1 - (alpha / 2)) * deviation / Math.sqrt(n);
        return (conferror <= relerror);

    }
    
    public static boolean hasEstimatedValueOf(double expectedValue, double avg, int n, double variance, double alpha) {
        double tCriticalMin=StudentDist.inverseF(n-1, alpha/2);
        double tCriticalMax=StudentDist.inverseF(n-1, 1-(alpha/2));
        double deviation = Math.sqrt(variance);       
        
        double tTrial=(avg-expectedValue)/(deviation/Math.sqrt(n));        
        return (tCriticalMin<tTrial && tTrial<tCriticalMax);
        
    }

    public static ImmediateTransition getRandomElementFromWeightedElements(HashMap<ImmediateTransition, Double> concurrentImmedTransitions)
            throws Exception {
        Random rnd = new Random();
        double sumWeight = 0.0;
        for (ImmediateTransition immediateTransition : concurrentImmedTransitions.keySet()) {
            sumWeight += concurrentImmedTransitions.get(immediateTransition);
        }
        double random = rnd.nextDouble() * sumWeight;
        double currentVal = 0.0;
        for (ImmediateTransition immediateTransition : concurrentImmedTransitions.keySet()) {
            currentVal += concurrentImmedTransitions.get(immediateTransition);
            if (random <= currentVal) {
                return immediateTransition;
            }
        }
        throw new Exception("Collection count and weights must be greater than 0!");
    }
}
