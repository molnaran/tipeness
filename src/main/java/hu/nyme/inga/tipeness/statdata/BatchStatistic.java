/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hu.nyme.inga.tipeness.statdata;

import hu.nyme.inga.tipeness.simulation.ConfigParser;
import hu.nyme.inga.tipeness.statistics.Statistics;
import java.util.HashMap;


public class BatchStatistic extends BatchAbstractStatistic {

    public BatchStatistic(ConfigParser configParser) {
        super(configParser);
        estimatedAvgDiffPlaceList = new HashMap<>();
        for (String place : getConfigParser().getListDiffTokenList()) {
            estimatedAvgDiffPlaceList.put(place, new StatValues());
            estimatedAvgPlaceList.put(place, new StatValues());
        }
        for (String place : getConfigParser().getWatchDiffTokenList()) {
            estimatedAvgDiffPlaceList.put(place, new StatValues());
            estimatedAvgPlaceList.put(place, new StatValues());
        }
    }

    @Override
    public boolean isAccurate() {
        if (this.numberOfN < getConfigParser().getMinSampleSize()) {
            return false;
        }
        return (super.areAvgTokenNumEstimatesAccurate() && areAvgTokenDiffEstimatesAccurate());
    }

    protected boolean areAvgTokenDiffEstimatesAccurate() {
        ConfigParser currentConfig = getConfigParser();
        if (estimatedAvgDiffPlaceList.isEmpty()) {
            return true;
        }
        for (String placeName : currentConfig.getWatchDiffTokenList()) {
            if (!Statistics.isAccurateEV(estimatedAvgDiffPlaceList.get(placeName).avg, numberOfN,
                    estimatedAvgDiffPlaceList.get(placeName).variance, currentConfig.getMaxRelError(), currentConfig.getAlpha())) {
                return false;
            }
        }
        return true;
    }

    @Override
    public String outResults() {
        StringBuilder sb = new StringBuilder();
        sb.append(super.outResults());        
        return sb.append(outDiffValues()).toString();
    }

    private String outDiffValues() {
        if (!super.getConfigParser().getWatchDiffTokenList().isEmpty()) {
            String nl = System.lineSeparator();
            StringBuilder sb = new StringBuilder();
            sb.append("------------------------------").append(nl);
            sb.append("Average difference in tokennumbers ").append(" after ").append(numberOfN).append(" number of batches")
                    .append(nl);
            sb.append("Significant values:").append(nl);
            for (String s : super.getConfigParser().getWatchDiffTokenList()) {
                sb.append("Difference at ").append(s).append(": ").append(estimatedAvgDiffPlaceList.get(s).avg).append(nl);
            }
            sb.append("Non significant values:").append(nl);
            for (String placeName : super.getConfigParser().getListDiffTokenList()) {
                if (!super.getConfigParser().getWatchDiffTokenList().contains(placeName)) {
                    sb.append("Difference at ").append(placeName).append(": ").append(estimatedAvgDiffPlaceList.get(placeName).avg).append(nl);
                }
            }
            sb.append("------------------------------").append(nl);
            return sb.toString();
        }
        return "";
    }    
}
