/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hu.nyme.inga.tipeness.simulation;

import hu.nyme.inga.tipeness.petrinetelements.Place;
import hu.nyme.inga.tipeness.statdata.AnalysisStatistic;
import hu.nyme.inga.tipeness.statdata.ReplicationStatistic;
import hu.nyme.inga.tipeness.statdata.BatchStatistic;
import java.util.HashMap;
import hu.nyme.inga.tipeness.statdata.MeasureUnit;
import javax.swing.JOptionPane;
import static javax.swing.JOptionPane.ERROR_MESSAGE;


public class SimulationMethod {

    public static void replicationSimulation(SimulationUnit simUnit) throws OutOfEventException {
        ConfigParser currentConfig = simUnit.getConfigParser();
        ReplicationStatistic repStat = (ReplicationStatistic) simUnit.getMeasureUnitStatistic();
        HashMap<String, Integer> initialMarking = runWarmingupPeriod(currentConfig);
        if (currentConfig.getWatchTokenList().isEmpty() && currentConfig.getWatchAvgTokenList().isEmpty()){
            JOptionPane.showMessageDialog(null, "At least one place must be defined with 'avgtoken' or 'token' tag if using repdel method!","ASD", ERROR_MESSAGE);
            return;
        }
        while (!repStat.isAccurate()) {
            NetState netState = new NetState(currentConfig, initialMarking);
            EventQueue eq = netState.getEventqueue();
            eq.updateQueue(currentConfig, netState);
            MeasureUnit currentReplication = new MeasureUnit(currentConfig);
            while (!simUnit.interrupted && !currentReplication.hasUnitEnded()) {
                netState.setCurrentEvent(eq.getNextEvent());
                Event currentEvent = netState.getCurrentEvent();

                netState.setTime(currentEvent.getTime());
                currentReplication.updateTimers(netState.getTime() - netState.getPreviousTime());
                currentReplication.calculateStatValues(netState);
                if (currentReplication.hasUnitEnded()) {
                    repStat.update(netState, currentReplication);
                }
                currentEvent.getTransition().fire(netState);
                netState.getEventqueue().updateQueue(currentConfig, netState);
                netState.setPreviousTime(netState.getTime());
            }
        }
    }

    public static void batchMeansSimulation(SimulationUnit simUnit) throws OutOfEventException {

        BatchStatistic batchStat = (BatchStatistic) simUnit.getMeasureUnitStatistic();
        ConfigParser currentConfig = simUnit.getConfigParser();
        if (currentConfig.getWatchDiffTokenList().isEmpty() && currentConfig.getWatchAvgTokenList().isEmpty()) {
            JOptionPane.showMessageDialog(null, "At least one place must be defined with 'avgtoken' or 'difftoken' tag if using batchMeans method!", "ASD", ERROR_MESSAGE);
            return;
        }

        HashMap<String, Integer> initialMarking = runWarmingupPeriod(currentConfig);
        NetState netState = new NetState(currentConfig, initialMarking);
        EventQueue eq = netState.getEventqueue();
        eq.updateQueue(currentConfig, netState);
        MeasureUnit currentBatch = new MeasureUnit(currentConfig);
        while (!simUnit.interrupted && !batchStat.isAccurate() && !eq.getEventQueue().isEmpty()) {
            netState.setCurrentEvent(eq.getNextEvent());
            Event currentEvent = netState.getCurrentEvent();
            netState.setTime(currentEvent.getTime());
            currentBatch.updateTimers(netState.getTime() - netState.getPreviousTime());

            currentBatch.calculateStatValues(netState);
            currentEvent.getTransition().fire(netState);
            netState.getEventqueue().updateQueue(currentConfig, netState);
            if (currentBatch.hasUnitEnded()) {
                batchStat.update(netState, currentBatch);
                currentBatch = new MeasureUnit(currentConfig);
            }
            netState.setPreviousTime(netState.getTime());
        }
    }

    public static void analysisSimulation(SimulationUnit simUnit) throws OutOfEventException {

        AnalysisStatistic analysisStat = (AnalysisStatistic) simUnit.getMeasureUnitStatistic();
        ConfigParser currentConfig = simUnit.getConfigParser();

        HashMap<String, Integer> initialMarking = runWarmingupPeriod(currentConfig);
        NetState netState = new NetState(currentConfig, initialMarking);
        EventQueue eq = netState.getEventqueue();
        eq.updateQueue(currentConfig, netState);
        MeasureUnit currentBatch = new MeasureUnit(currentConfig);        
        while (!simUnit.interrupted && !analysisStat.isAccurate() && !eq.getEventQueue().isEmpty()) {
            
            netState.setCurrentEvent(eq.getNextEvent());
            Event currentEvent = netState.getCurrentEvent();
            netState.setTime(currentEvent.getTime());
            currentBatch.updateTimers(netState.getTime() - netState.getPreviousTime());

            currentBatch.calculateStatValues(netState);
            currentEvent.getTransition().fire(netState);
            netState.getEventqueue().updateQueue(currentConfig, netState);

            if (currentBatch.hasUnitEnded()) {
                analysisStat.update(netState, currentBatch);
                currentBatch = new MeasureUnit(currentConfig);
            }
            netState.setPreviousTime(netState.getTime());
        }        
    }

    private static HashMap<String, Integer> runWarmingupPeriod(ConfigParser configParser) throws OutOfEventException {
        NetState netState = new NetState(configParser);
        EventQueue eq = netState.getEventqueue();
        eq.updateQueue(configParser, netState);
        while (netState.getTime() <= configParser.getWarmupLength()) {
            netState.setCurrentEvent(eq.getNextEvent());
            Event currentEvent = netState.getCurrentEvent();
            netState.setTime(currentEvent.getTime());
            currentEvent.getTransition().fire(netState);
            netState.getEventqueue().updateQueue(configParser, netState);
        }
        HashMap<String, Integer> tokenDistribution= new HashMap<>();
        for (Place place: netState.getPlaces()){
            tokenDistribution.put(place.getName(), place.getCurrent());
        }
        return tokenDistribution;
    }
}
