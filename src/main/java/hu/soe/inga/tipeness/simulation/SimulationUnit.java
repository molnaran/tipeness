/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hu.soe.inga.tipeness.simulation;

import hu.soe.inga.tipeness.statdata.AnalysisStatistic;
import hu.soe.inga.tipeness.statdata.BatchStatistic;
import hu.soe.inga.tipeness.statdata.MeasureUnitStatistic;
import hu.soe.inga.tipeness.statdata.ReplicationStatistic;
import java.io.FileWriter;
import java.io.PrintWriter;
import javax.swing.JOptionPane;

public class SimulationUnit implements Runnable {

    public boolean interrupted=false;
    private ConfigParser configParser;
    private MeasureUnitStatistic measureUnitStatistic;

    public SimulationUnit(String paramFilePath) throws InvalidParamfileException{
        configParser = new ConfigParser(paramFilePath);
        switch (configParser.getmType()) {
            case analysis:
                measureUnitStatistic = new AnalysisStatistic(configParser);
                break;
            case batchmean:
                measureUnitStatistic = new BatchStatistic(configParser);
                break;
            case repdel:
                measureUnitStatistic = new ReplicationStatistic(configParser);
                break;
        }
    }

    public SimulationUnit(ConfigParser configParser) {
        this.configParser = configParser;
        switch (configParser.getmType()) {
            case analysis:
                measureUnitStatistic = new AnalysisStatistic(configParser);
                break;
            case batchmean:
                measureUnitStatistic = new BatchStatistic(configParser);
                break;
            case repdel:
                measureUnitStatistic = new ReplicationStatistic(configParser);
                break;
        }
    }

    public ConfigParser getConfigParser() {
        return configParser;
    }

    public MeasureUnitStatistic getMeasureUnitStatistic() {
        switch (configParser.getmType()) {
            case analysis:
                return (AnalysisStatistic) measureUnitStatistic;
            case batchmean:
                return (BatchStatistic) measureUnitStatistic;
            case repdel:
                return (ReplicationStatistic) measureUnitStatistic;
            default:
                return measureUnitStatistic;
        }
    }

    @Override
    public void run() {
        try {
            switch (configParser.getmType()) {
                case analysis:
                    SimulationMethod.analysisSimulation(this);
                    break;
                case batchmean:
                    SimulationMethod.batchMeansSimulation(this);
                    break;
                case repdel:
                    SimulationMethod.replicationSimulation(this);
                    break;
            }
        } catch (OutOfEventException oe) {
            JOptionPane.showMessageDialog(null, "The simulation could not finish because the generated events have run out!");
        }
        writeResultsToFile();
    }

    public void writeResultsToFile() {
        try {
            FileWriter fileWriter = new FileWriter(this.getConfigParser().getOutFileNamePath(), true);
            PrintWriter out = new PrintWriter(fileWriter);
            out.print(this.getConfigParser().outPetrinet());
            if (interrupted){
                out.println("-------------------INTERRUPTED SIMULATION------------------------");
                out.print(this.getMeasureUnitStatistic().outResults());
                out.println("-------------------INTERRUPTED SIMULATION------------------------");
            }else{                
                out.print(this.getMeasureUnitStatistic().outResults());
            }
            out.close();
        } catch (Exception ex) {
            System.out.println(ex.toString());
        }

    }
}
