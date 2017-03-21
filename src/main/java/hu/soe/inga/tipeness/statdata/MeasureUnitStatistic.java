/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hu.soe.inga.tipeness.statdata;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import hu.soe.inga.tipeness.simulation.ConfigParser;
import hu.soe.inga.tipeness.simulation.NetState;
import hu.soe.inga.tipeness.statistics.Statistics;


public abstract class MeasureUnitStatistic {

    public int numberOfN;
    public double sumLength;
    public MeasureUnit prevMeasureUnit;
    public ArrayList<MeasureUnit> measureUnitList;
    public HashMap<String, StatValues> estimatedAvgPlaceList;
    private final ConfigParser configParser;

    public MeasureUnitStatistic(ConfigParser configParser) {
        sumLength = 0;
        numberOfN = 0;
        measureUnitList = new ArrayList<>();
        this.configParser = configParser;
        estimatedAvgPlaceList = new HashMap<>();
        for (String placeName : configParser.getListAvgTokenList()) {
            estimatedAvgPlaceList.put(placeName, new StatValues());
        }
        for (String placeName : configParser.getWatchAvgTokenList()) {
            estimatedAvgPlaceList.put(placeName, new StatValues());
        }
    }

    public void update(NetState netState, MeasureUnit measureUnit) {
        numberOfN++;
        updateEstimates(netState, measureUnit);
        this.sumLength += measureUnit.length;
        prevMeasureUnit = measureUnit;
    }

    protected abstract void updateEstimates(NetState netState, MeasureUnit measureUnit);

    public abstract boolean isAccurate();

    protected boolean areAvgTokenNumEstimatesAccurate() {
        if (estimatedAvgPlaceList.isEmpty()) {
            return true;
        }
        for (String placeName : configParser.getWatchAvgTokenList()) {
            if (!Statistics.isAccurateEV(estimatedAvgPlaceList.get(placeName).avg, numberOfN, estimatedAvgPlaceList.get(placeName).variance,
                    configParser.getMaxRelError(), configParser.getAlpha())) {
                return false;
            }
        }
        return true;
    }

    private void setPrevMeasureUnit(MeasureUnit measureUnit) {
        prevMeasureUnit = measureUnit;
    }

    protected void updateAvgTokenNumEstimates(NetState netState, MeasureUnit measureUnit) {
        for (String placeName : estimatedAvgPlaceList.keySet()) {
            StatValues tempMeasureUnitStatValues = measureUnit.avgPalceList.get(placeName);
            double previousAvg = estimatedAvgPlaceList.get(placeName).avg;
            estimatedAvgPlaceList.get(placeName).avg = Statistics.avgWithDistinctWeights(tempMeasureUnitStatValues.avg,
                    measureUnit.length, previousAvg, sumLength);
            estimatedAvgPlaceList.get(placeName).variance = Statistics.variance(tempMeasureUnitStatValues.avg,
                    estimatedAvgPlaceList.get(placeName).variance, previousAvg, estimatedAvgPlaceList.get(placeName).avg, sumLength,
                    sumLength + measureUnit.length);
        }
        
    }

    public String outResults() {
        StringBuilder sb = new StringBuilder();
        String nl = System.lineSeparator();
        sb.append("RESULTS:").append(nl);
        sb.append(configParser.outSystemParams(this));
        return sb.append(outAvgs()).toString();
    }

    public void outPureAvgResults() throws IOException {
        String nl = System.lineSeparator();
        FileWriter fw = new FileWriter(configParser.getOutFileNamePath(), true);
        BufferedWriter bw = new BufferedWriter(fw);
        for (String s : estimatedAvgPlaceList.keySet()) {
            bw.append(s).append("\t").append(Double.toString(estimatedAvgPlaceList.get(s).avg)).append(nl);
        }
        bw.close();
    }

    protected String outAvgs() {
        
        String nl = System.lineSeparator();
        StringBuilder sb = new StringBuilder();
        sb.append("------------------------------").append(nl);
        sb.append("Average tokennumbers after ").append(numberOfN).append(" number of N")
                .append(nl);
        sb.append("Significant values:").append(nl);
        for (String placeName : configParser.getWatchAvgTokenList()) {
            sb.append("Average tokennumber at ").append(placeName).append(": ").append(estimatedAvgPlaceList.get(placeName).avg).append(nl);
        }  
        sb.append("Non significant values:").append(nl);
        for (String placeName : configParser.getListAvgTokenList()) {
            if (!configParser.getWatchAvgTokenList().contains(placeName)){
                sb.append("Average tokennumber at ").append(placeName).append(": ").append(estimatedAvgPlaceList.get(placeName).avg).append(nl);
            }            
        }  
        return sb.toString();
    }

    public ConfigParser getConfigParser() {
        return configParser;
    }

    public HashMap<String, StatValues> getEstimatedAvgPlaceList() {
        return estimatedAvgPlaceList;
    }

}
