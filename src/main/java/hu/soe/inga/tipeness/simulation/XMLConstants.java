/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hu.soe.inga.tipeness.simulation;

/**
 *
 * @author Andrew
 */
public interface XMLConstants {
    final String roolElementTag="petrinet";
    final String expTransitionTag="exptransition";
    final String nodeNameTag="name";
    final String placeTag="place";
    final String tokenNumTag="token";
    final String immedTransitionTag="immedtransition";
    final String detTransitionTag="dettransition";
    final String gammaTransitionTag="gammatransition";
    final String normalTransitionTag="normaltransition";
    final String inhibitorEdgeTag="inhibitor";
    final String inputEdgeTag="inplace";
    final String outputEdgeTag="outplace";
    final String breakpointTag="breakpoint";
    final String breakpointNumberTag="id";
    final String arcWeightTag="arc";
    final String priorityTag="priority";
    final String weightTag="weight";
    final String conditionTag="condition";
    final String serverTypeTag="servertype";
    final String memoryMainTag="memory";
    final String memoryPolicyTag="policy";
    final String resamplingMemoryTag="resampling";
    final String enablingMemoryTag="enabling";
    final String ageMemoryTag="age";
    final String gammaShapeTag="shape";
    final String gammaRateTag="rate";
    final String normalMeanTag="mean";
    final String normalVarianceTag="variance";
    final String delayTag="delay";
    final String simulationParamsTag="system";
    final String minSampleSizeTag="minsamplesize";
    final String confidenceLevelTag="confidencelevel";
    final String watchTokenTag="token";
    final String listTokenTag="token";
    final String watchAvgTokenTag="avgtoken";
    final String watchDiffTokenTag="difftoken";
    final String listAvgTokenTag="listavgtoken";
    final String listDiffTokenTag="listdifftoken";
    final String simulationMethodTag="method";
    final String replicationMethodTag="repdel";
    final String batchMeansMethodTag="batchmean";
    final String analysisMethodTag="analysis";
    final String batchLengthTag="batch";
    final String terminatingTimeTag="terminatingtime";
    final String warmupLengthTag="warmuplength";
    final String maxrelerrorTag="maxrelerror";
    final String outfilepathTag="outfilepath";
    final String shapePosXAttr="positionX";
    final String shapePosYAttr="positionY";
    final String textPosXAttr="textpositionX";
    final String textPosYAttr="textpositionY";
   
}
