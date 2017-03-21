/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hu.nyme.inga.tipeness.statdata;

import hu.nyme.inga.tipeness.statistics.Statistics;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import hu.nyme.inga.tipeness.simulation.ConfigParser;
import hu.nyme.inga.tipeness.simulation.NetState;


public class ReplicationStatistic extends MeasureUnitStatistic {

    public HashMap<String, StatValues> esimatedTokenList;

    public ReplicationStatistic(ConfigParser configParser) {
        super(configParser);
        esimatedTokenList = new HashMap<>();
        for (String placeName : getConfigParser().getListTokenList()) {
            esimatedTokenList.put(placeName, new StatValues());
        }
        for (String placeName : getConfigParser().getWatchTokenList()) {
            esimatedTokenList.put(placeName, new StatValues());
        }
    }

    @Override
    public void update(NetState netState, MeasureUnit measureUnit) {
        if (!this.esimatedTokenList.isEmpty()) {
            for (String placeName : esimatedTokenList.keySet()) {
                measureUnit.tokenPalceList.put(placeName, netState.getPlaceByName(placeName).getCurrent());
            }
        }
        super.update(netState, measureUnit);
    }

    @Override
    protected void updateEstimates(NetState netState, MeasureUnit measureUnit) {
        super.updateAvgTokenNumEstimates(netState, measureUnit);
        updateTokenNumEstimates(netState, measureUnit);
    }

    protected void updateTokenNumEstimates(NetState netState, MeasureUnit measureUnit) {
        for (String placeName : esimatedTokenList.keySet()) {
            double currentValue = netState.getPlaceByName(placeName).getCurrent();
            double previousAvg = esimatedTokenList.get(placeName).avg;
            esimatedTokenList.get(placeName).avg = Statistics.avgWithDistinctWeights(currentValue, measureUnit.length, previousAvg, sumLength);
            esimatedTokenList.get(placeName).variance = Statistics.variance(currentValue, esimatedTokenList.get(placeName).variance, previousAvg,
                    esimatedTokenList.get(placeName).avg, sumLength, sumLength + measureUnit.length);
        }
    }

    @Override
    public boolean isAccurate() {
        if (this.numberOfN < getConfigParser().getMinSampleSize()) {
            return false;
        }
        return (super.areAvgTokenNumEstimatesAccurate() && areTokenNumEstimatesAccurate());
    }

    protected boolean areTokenNumEstimatesAccurate() {
        for (String placeName : getConfigParser().getWatchTokenList()) {
            if (!Statistics.isAccurateEV(esimatedTokenList.get(placeName).avg, numberOfN, esimatedTokenList.get(placeName).variance,
                    getConfigParser().getMaxRelError(), getConfigParser().getAlpha())) {
                return false;
            }
        }
        return true;
    }

    @Override
    public String outResults() {
        StringBuilder sb = new StringBuilder();        
        sb.append(super.outResults());
        sb.append(outValues());
        return sb.toString();
    }

    public void outPureTokenResults() throws IOException {
        String nl = System.lineSeparator();
        FileWriter fw = new FileWriter(getConfigParser().getOutFileNamePath(), true);
        BufferedWriter bw = new BufferedWriter(fw);
        for (String placeName : esimatedTokenList.keySet()) {
            bw.append(placeName).append("\t").append(Double.toString(esimatedTokenList.get(placeName).avg)).append(nl);
        }
        bw.close();
    }

    protected String outValues() {
        System.out.println(esimatedTokenList.keySet().size());
        if (!esimatedTokenList.keySet().isEmpty()) {
            String nl = System.lineSeparator();
            StringBuilder sb = new StringBuilder();
            sb.append("------------------------------").append(nl); 
            sb.append("Significant values:").append(nl);
            for (String placeName : super.getConfigParser().getWatchTokenList()) {
                sb.append("Tokennumber at ").append(placeName).append(": ").append(esimatedTokenList.get(placeName).avg).append(nl);
            }
            sb.append("Non significant values:").append(nl);
            for (String placeName : super.getConfigParser().getListTokenList()) {
                if (!super.getConfigParser().getWatchTokenList().contains(placeName)){
                    sb.append("Tokennumber at ").append(placeName).append(": ").append(esimatedTokenList.get(placeName).avg).append(nl);
                }                
            }
            sb.append("------------------------------").append(nl);
            return sb.toString();
        }
        return "";
    }
}
