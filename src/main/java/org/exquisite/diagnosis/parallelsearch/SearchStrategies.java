/**
 *
 */
package org.exquisite.diagnosis.parallelsearch;

/**
 * A collection of pre-defined search strategies
 *
 * @author Arash
 */
public enum SearchStrategies {

    Default,

    AssignInterval,

    AssignOrForbidIntVarVal,
    AssignOrForbidIntVarVal_DomDegBin,
    AssignOrForbidIntVarVal_DomDDegBin,
    AssignOrForbidIntVarVal_DomWDegBin,
    AssignOrForbidIntVarVal_RandomIntBinSearch,

    AssignOrForbidIntVarValPair,

    AssignSetVar,

    AssignVar,
    AssignVar_DomDeg,
    AssignVar_DomDDeg,
    AssignVar_DomWDeg,
    AssignVar_Lexicographic,
    AssignVar_MinDomIncDom,
    AssignVar_MinDomDecDom,
    AssignVar_MinDomMinVal,
    AssignVar_RandomIntSearch,


    DomOverWDegBranchingNew,
    DomOverWDegBranchingNew_IncDomWDeg,

    DomOverWDegBinBranchingNew,
    DomOverWDegBinBranchingNew_IncDomWDegBin,

    ImpactBasedBranching,

//	PackDynRemovals, 

//	SetTimes, 

//	TaskOverWDegBinBranching;
}
