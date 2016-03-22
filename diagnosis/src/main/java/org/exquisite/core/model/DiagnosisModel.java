package org.exquisite.core.model;

import java.util.*;

/**
 * Contains the knowledge base for constraint problems
 *
 * @author Dietmar
 */
public class DiagnosisModel<T> extends Observable implements Observer {

    /**
     * Weights of constraints
     */
    private Map<T, Float> statementWeights = new HashMap<>();
    /**
     * The set of formulas which we assume to be always correct (background knowledge)
     */
    private List<T> correctStatements = ObservableList.observableArrayList();
    /**
     * The set of the statements which could be faulty = KB (knowledge base).
     */
    private List<T> possiblyFaultyStatements = ObservableList.observableArrayList();
    /**
     * The positive examples
     */
    private List<T> consistentExamples = ObservableList.observableArrayList();
    /**
     * The negative examples
     */
    private List<T> inconsistentExamples = ObservableList.observableArrayList();
    /**
     * List of statements, that should not be entailed
     */
    private List<T> notEntailedExamples = ObservableList.observableArrayList();

    /**
     * List of statements, that should be entailed
     */
    private List<T> entailedExamples = ObservableList.observableArrayList();

    /**
     * Entailed Testcases = Queries that are answered positively by the user. Each element of the list is
     * exactly the set of formulas in a query.
     */
    //private List<Set<T>> entailedTestCases = ObservableList.observableArrayList(); // NEW

    /**
     * Not Entailed Testcases = Queries that are answered negatively by the user. Each element of the list is
     * exactly the set of formulas in a query.
     */
    //private List<Set<T>> notEntailedTestCases = ObservableList.observableArrayList(); // NEW

    /**
     * A copy constructor that copies all lists and links the pointers to non-changing information
     *
     * @param orig the original model to copy
     */
    public DiagnosisModel(DiagnosisModel<T> orig) {
        // only copy the
        this.correctStatements = ObservableList.observableList(orig.correctStatements, this);
        this.inconsistentExamples = ObservableList.observableList(orig.inconsistentExamples, this);
        this.consistentExamples = ObservableList.observableList(orig.consistentExamples, this);
        this.notEntailedExamples = ObservableList.observableList(orig.notEntailedExamples, this);
        this.entailedExamples = ObservableList.observableList(orig.entailedExamples, this);
        this.possiblyFaultyStatements = ObservableList.observableList(orig.possiblyFaultyStatements, this);
        this.statementWeights = new HashMap<>(orig.statementWeights);
    }

    /**
     * Creates an empty diagnosis model. Note that the observers will not be notified if
     * elements of lists are changed directly!
     */
    public DiagnosisModel() {
    }


    /**
     * Getter for the correct statements
     *
     * @return
     */
    public List<T> getCorrectStatements() {
        return correctStatements;
    }

    /**
     * Sets the correct statements
     *
     * @param correctStatements
     */
    public void setCorrectStatements(List<T> correctStatements) {
        this.correctStatements = ObservableList.observableList(correctStatements, this);
        setChanged();
        notifyObservers(this.correctStatements);
    }

    /**
     * Getter for the possibly faulty statements
     *
     * @return
     */
    public List<T> getPossiblyFaultyStatements() {
        return possiblyFaultyStatements;
    }

    /**
     * Setter for the possibly faulty statements
     *
     * @param possiblyFaultyStatements
     */
    public void setPossiblyFaultyStatements(
            Collection<T> possiblyFaultyStatements) {
        this.possiblyFaultyStatements = ObservableList.observableList(possiblyFaultyStatements, this);
        setChanged();
        notifyObservers(this.possiblyFaultyStatements);
    }

    /**
     * returns the positive examples
     *
     * @return
     */
    public List<T> getConsistentExamples() {
        return consistentExamples;
    }


    /**
     * Setter for the positive examples
     *
     * @param consistentExamples
     */
    public void setConsistentExamples(Collection<T> consistentExamples) {
        this.consistentExamples = ObservableList.observableList(consistentExamples, this);
        setChanged();
        notifyObservers(this.consistentExamples);
    }

    /**
     * Setter for the negative examples
     *
     * @return
     */
    public List<T> getInconsistentExamples() {
        return inconsistentExamples;
    }


    public void setInconsistentExamples(Collection<T> inconsistentExamples) {
        this.inconsistentExamples = ObservableList.observableList(inconsistentExamples, this);
        setChanged();
        notifyObservers(this.inconsistentExamples);
    }

    public List<T> getNotEntailedExamples() {
        return notEntailedExamples;
    }

    public void setNotEntailedExamples(Collection<T> notEntailedExamples) {
        this.notEntailedExamples = ObservableList.observableList(notEntailedExamples, this);
        setChanged();
        notifyObservers(this.notEntailedExamples);
    }

    @Override
    public void update(Observable o, Object arg) {
        setChanged();
        notifyObservers(o);
    }

    public Map<T, Float> getStatementWeights() {
        return statementWeights;
    }

    public void setStatementWeights(Map<T, Float> statementWeights) {
        this.statementWeights = statementWeights;
    }

    public List<T> getEntailedExamples() {
        return entailedExamples;
    }

    public void setEntailedExamples(Collection<T> entailedExamples) {
        this.entailedExamples = ObservableList.observableList(entailedExamples, this);
        setChanged();
        notifyObservers(this.entailedExamples);
    }

    /**
     * Call this method to initialize the model before computation of diagnoses. This method implements all general
     * checks and transformations of the provided statements and examples. Override this method to add initialization
     * routines specific for the used knowledge representation and reasoning formalism.
     * <p>
     * By default this method ensures that the sets of examples, possibly faulty and correct statements are disjoint
     * and consistent one with each other.
     */
    public void initialize() {

    }
}
