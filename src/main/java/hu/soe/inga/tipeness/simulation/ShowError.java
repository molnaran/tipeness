package hu.soe.inga.tipeness.simulation;
/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

public class ShowError {

    public enum ErrorType {

        wrongInputFilePath, wrongTokenNum, wrongPlaceName, wrongTransName, wrongPriority,
        wrongArcWeight, wrongDelay, wrongIOPlaceName, wrongServerType, wrongSignPlace, nullSignPlace,
        wrongMinSampleSize, wrongAlphaValue, wrongTransientAccuracy, wrongRelErrorValue, wrongBatchLength, wrongTokenPlace,
        wrongAvgTokenPlace, wrongDiffTokenPlace, wrongMemoryPolicy, wrongTransWeight, wrongMemoryType, wrongGammaParam,
        wrongDetParam, wrongNormDistParam, wrongTerminatingTime, wrongMethodType, wrongWarmupLength,
        noWatchAvgOrDiffPlaceAtBatch, noWatchTokenOrAvgPlaceAtRepDel, wrongFireCondition, wrongFirePolicyTransname, wrongLocationParam,
        errorAddingEdgeToTransition, wrongBreakpointNum
    };
    public ErrorType error;

    public static String showError(ErrorType error) {
        switch (error) {
            case wrongTokenNum:
                return("The token number is not valid!");
                
            case wrongPlaceName:
                return("The 'placename' is not valid!");
                
            case wrongTransName:
                return("The 'transitionname' is not valid!");
                
            case wrongPriority:
                return("The given 'priority' is not valid!");
                
            case wrongArcWeight:
                return("The 'arcweight' is not valid!");
                
            case wrongDelay:
                return("The given 'delay' is not valid!");
                
            case wrongIOPlaceName:
                return("The name of an input/output place is not valid!");
                
            case wrongServerType:
                return("The 'servertype' is not valid!");
                
            case wrongSignPlace:
                return("The name of a significant place is not valid!");
                
            case wrongMinSampleSize:
                return("The value of minimal number of batches/replications is not valid!");
                
            case wrongAlphaValue:
                return("The chosen confidence level is not valid!");
                
            case nullSignPlace:
                return("There is no significant place and no maximum runtime is given! This can result in infinite runtime!");
                
            case wrongTransientAccuracy:
                return("The value of transient accuracy is not valid!");
                
            case wrongRelErrorValue:
                return("The given value of the maximal relative error is not valid!");
                
            case wrongBatchLength:
                return("The given length of batches is not valid!");
                
            case wrongInputFilePath:
                return("The parameter file is not readable!");
                
            case wrongTokenPlace:
                return("The name of a place is not valid at tokenlist!");
                
            case wrongAvgTokenPlace:
                return("The name of a place is not valid at avgtokenlist!");
                
            case wrongDiffTokenPlace:
                return("The name of a place is not valid at difftokenlist!");
                
            case wrongMemoryPolicy:
                return("The memory policy can only be resampling, enabling memory or age memory!");
                
            case wrongFirePolicyTransname:
                return("A given transition name is not valid at the memory policy option!");
                
            case wrongTransWeight:
                return("The given transition weight must be a number greater than zero!");
                
            case wrongMemoryType:
                return("The given 'memorytype' is not valid!!");
                
            case wrongGammaParam:
                return("The given parameters for the gamma distribution are not valid!");
                
            case wrongDetParam:
                return("The given parameters for the deterministic distribution are not valid!");
                
            case wrongNormDistParam:
                return("The given parameters for the normal distribution are not valid!");
                
            case wrongTerminatingTime:
                return("The value of terminatingtime is not valid!");
                
            case wrongWarmupLength:
                return("The value of warmuplength is not valid!");
                
            case wrongMethodType:
                return("The desired method cannot be found!");
                
            case noWatchAvgOrDiffPlaceAtBatch:
                return("At least one place must be defined with 'avgtoken' or 'difftoken' tag if using batchMeans method!");
                
            case noWatchTokenOrAvgPlaceAtRepDel:
                return("At least one place must be defined with 'avgtoken' or 'token' tag if using batchMeans method!");
                
            case wrongFireCondition:
                return("The given condition can not be parsed!");
                
            case wrongLocationParam:
                return("The given location parameter is not valid!");
                
            case errorAddingEdgeToTransition:
                return("Error adding an adge to transition!");
                
            case wrongBreakpointNum:
                return("Wrong breakpoint number!");
                
            default:
                return("Error during the filereading!");
                
        }        
    }

    public static String showError(ErrorType error, String where) {
        switch (error) {
            case wrongTokenNum:
                return("The token number is not valid at the " + where + " place!");
                
            case wrongPriority:
                return("The given priority is not valid at the " + where + " transition!");
                
            case wrongArcWeight:
                return("An arcweight is not valid at the " + where + " transition!");
                
            case wrongDelay:
                return("The given delay is not valid at the " + where + " transition!");
                
            case wrongIOPlaceName:
                return("The name of an input/output place is not valid at the " + where + " transition!");
                
            case wrongServerType:
                return("The servertype is not valid at the " + where + " transition!");
                
            case wrongTransName:
                return("The transitionname is not valid! (" + where + ")");
                
            case wrongTokenPlace:
                return("The name of the place is not valid at the watched tokenlist (" + where + ")!");
                
            case wrongAvgTokenPlace:
                return("The name of the place is not valid at the watched avgtokenlist (" + where + ")!");
                
            case wrongMemoryPolicy:
                return("Wrong memory policy is given at the " + where + " transition. The memory policy can only be resampling,"
                        + "enabling memory or age memory!");
                
            case wrongTransWeight:
                return("The given weight at the " + where + " transition must be a number greater than zero!");
                
            case wrongMemoryType:
                return("The given memorytype is not valid at the " + where + " transition!");
                
            case wrongGammaParam:
                return("The given parameters for the gamma distribution are not valid at the " + where + " transition!");
                
            case wrongDetParam:
                return("The given parameters for the deterministic distribution at the " + where + " transition are not valid!");
                
            case wrongNormDistParam:
                return("The given parameters for the normal distribution at the " + where + " transition are not valid!");
                
            case wrongFireCondition:
                return("The given condition can not be parsed at the " + where + " transition!");
                
            case wrongFirePolicyTransname:
                return("A given transition name at the " + where + " transition is not valid at the memory policy option!");
                
            case wrongLocationParam:
                return("The given location parameter is not valid at " + where + "!");
                
            case errorAddingEdgeToTransition:
                return("Error adding an adge to "+ where+ "transition!");
                
            case wrongBreakpointNum:
                return("Wrong breakpoint number at " + where+"!");
                
            default:
                return("Error during the filereading!");
                
        }        
    }
}
