/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hu.nyme.inga.tipeness.statdata;

import hu.nyme.inga.tipeness.petrinetelements.Place;
import hu.nyme.inga.tipeness.statistics.Statistics;
import java.util.HashMap;
import hu.nyme.inga.tipeness.simulation.ConfigParser;


public class AnalysisStatistic extends BatchAbstractStatistic {

    public enum StabilityEnum {
        stable, unstable, unpredictable, underthreshold, unknown
    };

    private HashMap<String, StabilityEnum> stabilityList;
    private final double THRESHOLD = 0.001;

    public AnalysisStatistic(ConfigParser configParser) {
        super(configParser);
        estimatedAvgDiffPlaceList = new HashMap<>();
        for (Place place: configParser.getPlaces()){
            super.getEstimatedAvgDiffPlaceList().put(place.getName(), new StatValues());
            super.getEstimatedAvgPlaceList().put(place.getName(), new StatValues());
        }
        this.stabilityList = new HashMap<>();
    }

    @Override
    public boolean isAccurate() {
        if (numberOfN < getConfigParser().getMinSampleSize()) {
            return false;
        }
        for (Place place : getConfigParser().getPlaces()) {
            if (getPlaceStability(place.getName()) == StabilityEnum.unknown) {                
                return false;
            }
        }
        return true;
    }

    @Override
    public String outResults() {
        
        String nl = System.lineSeparator();
        StringBuilder sb = new StringBuilder();
        sb.append(getConfigParser().outSystemParams(this));
        sb.append("------------------------------").append(nl).append(nl); 
        sb.append("RESULTS: ").append(nl).append(nl);
        for (Place place : getConfigParser().getPlaces()) {
            String placeName=place.getName();
            switch (getPlaceStability(placeName)) {
                case stable:
                    sb.append(placeName).append(" is stable! (avg: ").append(estimatedAvgPlaceList.get(placeName).avg)
                            .append(")").append(", (diff: ")
                            .append(estimatedAvgDiffPlaceList.get(placeName).avg).append(")").append(nl);
                    break;
                case unstable:
                    sb.append(placeName).append(" is not stable! (avg: ").append(estimatedAvgPlaceList.get(placeName).avg)
                            .append(")").append(", (diff: ")
                            .append(estimatedAvgDiffPlaceList.get(placeName).avg).append(")").append(nl);
                    break;
                case unpredictable:
                    sb.append("Stability at the ").append(placeName).append(" place can not be determined (both the average tokennumber and the average difference reached"
                            + "the given accuracy)! We recommend increasing the batch size and reducing the value of the maximal relaitve error! (avg: ").append(estimatedAvgPlaceList.get(placeName).avg)
                            .append(")").append(", (diff: ")
                            .append(estimatedAvgDiffPlaceList.get(placeName).avg).append(")").append(nl);
                    break;
                case underthreshold:
                    sb.append(placeName).append(" is stable, but it is not recommended as significant place!").append(nl);
                    break;
                case unknown:
                    sb.append("Problem at ").append(placeName).append(" place!").append(nl);
                    break;
            }
        }
        return sb.toString();
    }    
    
    private StabilityEnum getPlaceStability(String placeName) {        
        if (estimatedAvgDiffPlaceList.get(placeName).avg == 0 || estimatedAvgPlaceList.get(placeName).avg == 0) {
            return StabilityEnum.stable;
        }else if (Statistics.hasEstimatedValueOf(0, estimatedAvgDiffPlaceList.get(placeName).avg, numberOfN, estimatedAvgDiffPlaceList.get(placeName).variance, getConfigParser().getAlpha()) 
                && Statistics.isAccurateEV(estimatedAvgDiffPlaceList.get(placeName).avg, numberOfN, estimatedAvgDiffPlaceList.get(placeName).variance, getConfigParser().getMaxRelError(), getConfigParser().getAlpha())){
            return StabilityEnum.unpredictable;
        }
        else if (Statistics.hasEstimatedValueOf(0, estimatedAvgDiffPlaceList.get(placeName).avg, numberOfN, estimatedAvgDiffPlaceList.get(placeName).variance, getConfigParser().getAlpha())){
            return StabilityEnum.stable;
        }else if (Statistics.isAccurateEV(estimatedAvgDiffPlaceList.get(placeName).avg, numberOfN, estimatedAvgDiffPlaceList.get(placeName).variance, getConfigParser().getMaxRelError(), getConfigParser().getAlpha())){
            return StabilityEnum.unstable;
        }else {
            return StabilityEnum.unknown;
        }
    }
}
