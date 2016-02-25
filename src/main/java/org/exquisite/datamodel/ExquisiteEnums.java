package org.exquisite.datamodel;

/**
 * A collection of enums used throughout exquisite-service. Placed here in a central location for convenience.
 *
 * @author David
 */
public class ExquisiteEnums {

    /**
     * Describes the various types of messages that could be
     * relayed between server & client.
     *
     * @author Thomas
     */
    public enum ContentTypes {
        Diagnoses,    //message items containing diagnoses
        Info,        //informational message to display to user.
        Error,        //error message to display to user.
        System,        //message intended for internal system use.
        Cellnames,  //message contains a list of cell names
        Fragments    //message contains a list of fragments
    }

    /**
     * An enum for tests.diagnosis engine configuration descriptions.
     */
    public enum EngineType {
        HSDagStandardQX,    //HSDagEngine with standard QX implementation.
        ParaHSDagStandardQX, //ParallelHSDagEngine with standard QX implementation.
        FullParaHSDagStandardQX, // full parallelization
        HeuristicSearch, // depth first
        Hybrid, // Hybrid dfs + bfs
        MiniZinc, // MiniZinc based search engine
        PRDFS, // Parallel Random Depth-First Search
        InverseQuickXplain, // Inverse QuickXplain
        MXPandInvQXP, // MergeXplain + Inverse QuickXplain
        SFL, // Spectrum-based Fault Localization
    }

    /**
     * A collection of messages indicating the current state of the server to a client.
     */
    public enum StatusCodes {
        BUSY,    //If the server is not in a position to serve the request.
        ERROR,    //Returned if client request was acknowledged and completed successfully.
        OK        //Returned if there was a failure in completing the request from the
        //client, due to some internal problem.
    }

    /**
     *
     */
    public enum ExquisiteFlag {
        Original,
        Copy
    }

    public enum ExquisiteLocaleFlag {
        German,
        EnglishGB
    }

    /**
     * @author Arash
     */
    public enum ExquisiteTestcaseFlag {
        Normal,
        EverythingWrong,
        EverythingCorrect
    }

    /**
     * Describes the different values the constraints in a positive example could represent
     * e.g. an input value, the expected value of an ouput cell or that a value of a cell is
     * considered correct for the example in question.
     *
     * @author David     *
     */
    public enum ExampleConstraintValueTypes {
        InputValue,
        ExpectedValue,
        CorrectValue,
    }
}