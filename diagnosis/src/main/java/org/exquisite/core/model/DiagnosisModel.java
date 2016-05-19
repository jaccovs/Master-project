package org.exquisite.core.model;

import java.util.*;

/**
 * Contains the knowledge base for constraint problems.
 *
 * @param <F> Formulas, Statements, Axioms, Logical Sentences, Constraints etc.
 * @author Dietmar
 */
public class DiagnosisModel<F> extends Observable implements Observer {

    /**
     * Weights of formulas.
     */
    private Map<F, Float> formulaWeights = new HashMap<>();

    /**
     * The set of formulas which we assume to be always correct (background knowledge)
     */
    private List<F> correctFormulas = ObservableList.observableArrayList();

    /**
     * The set of the statements which could be faulty = KB (knowledge base).
     */
    private List<F> possiblyFaultyFormulas = ObservableList.observableArrayList();

    /**
     * The positive examples.
     */
    private List<F> consistentExamples = ObservableList.observableArrayList();

    /**
     * The negative examples
     */
    private List<F> inconsistentExamples = ObservableList.observableArrayList();

    /**
     * List of formulas, that should not be entailed.
     */
    private List<F> notEntailedExamples = ObservableList.observableArrayList();

    /**
     * List of formulas, that should be entailed.
     */
    private List<F> entailedExamples = ObservableList.observableArrayList();

    /**
     * Entailed Testcases = Queries that are answered positively by the user. Each element of the list is
     * exactly the set of formulas in a query.
     */
    //private List<Set<F>> entailedTestCases = ObservableList.observableArrayList(); // NEW

    /**
     * Not Entailed Testcases = Queries that are answered negatively by the user. Each element of the list is
     * exactly the set of formulas in a query.
     */
    //private List<Set<F>> notEntailedTestCases = ObservableList.observableArrayList(); // NEW

    /**
     * A copy constructor that copies all lists and links the pointers to non-changing information
     *
     * @param orig the original model to copy
     */
    public DiagnosisModel(DiagnosisModel<F> orig) {
        // only copy the
        this.correctFormulas = ObservableList.observableList(orig.correctFormulas, this);
        this.inconsistentExamples = ObservableList.observableList(orig.inconsistentExamples, this);
        this.consistentExamples = ObservableList.observableList(orig.consistentExamples, this);
        this.notEntailedExamples = ObservableList.observableList(orig.notEntailedExamples, this);
        this.entailedExamples = ObservableList.observableList(orig.entailedExamples, this);
        this.possiblyFaultyFormulas = ObservableList.observableList(orig.possiblyFaultyFormulas, this);
        this.formulaWeights = new HashMap<>(orig.formulaWeights);
    }

    /**
     * Creates an empty diagnosis model. Note that the observers will not be notified if
     * elements of lists are changed directly!
     */
    public DiagnosisModel() {
    }


    /**
     * Getter for the correct formulas (or background knowledge).
     *
     * @return
     */
    public List<F> getCorrectFormulas() {
        return correctFormulas;
    }

    /**
     * Sets the correct statements (or background knowledge).
     *
     * @param correctFormulas
     */
    public void setCorrectFormulas(Collection<F> correctFormulas) {
        this.correctFormulas = ObservableList.observableList(correctFormulas, this);
        setChanged();
        notifyObservers(this.correctFormulas);
    }

    /**
     * Getter for the possibly faulty formulas (or knowledge base).
     *
     * @return
     */
    public List<F> getPossiblyFaultyFormulas() {
        return possiblyFaultyFormulas;
    }

    /**
     * Setter for the possibly faulty formulas (or knowledge base).
     *
     * @param possiblyFaultyFormulas
     */
    public void setPossiblyFaultyFormulas(
            Collection<F> possiblyFaultyFormulas) {
        this.possiblyFaultyFormulas = ObservableList.observableList(possiblyFaultyFormulas, this);
        setChanged();
        notifyObservers(this.possiblyFaultyFormulas);
    }

    /**
     * returns the positive examples
     *
     * @return
     */
    public List<F> getConsistentExamples() {
        return consistentExamples;
    }


    /**
     * Setter for the positive examples
     *
     * @param consistentExamples
     */
    public void setConsistentExamples(Collection<F> consistentExamples) {
        this.consistentExamples = ObservableList.observableList(consistentExamples, this);
        setChanged();
        notifyObservers(this.consistentExamples);
    }

    /**
     * Setter for the negative examples
     *
     * @return
     */
    public List<F> getInconsistentExamples() {
        return inconsistentExamples;
    }


    public void setInconsistentExamples(Collection<F> inconsistentExamples) {
        this.inconsistentExamples = ObservableList.observableList(inconsistentExamples, this);
        setChanged();
        notifyObservers(this.inconsistentExamples);
    }

    public List<F> getNotEntailedExamples() {
        return notEntailedExamples;
    }

    public void setNotEntailedExamples(Collection<F> notEntailedExamples) {
        this.notEntailedExamples = ObservableList.observableList(notEntailedExamples, this);
        setChanged();
        notifyObservers(this.notEntailedExamples);
    }

    @Override
    public void update(Observable o, Object arg) {
        setChanged();
        notifyObservers(o);
    }

    public Map<F, Float> getFormulaWeights() {
        return formulaWeights;
    }

    public void setFormulaWeights(Map<F, Float> formulaWeights) {
        this.formulaWeights = formulaWeights;
    }

    public List<F> getEntailedExamples() {
        return entailedExamples;
    }

    public void setEntailedExamples(Collection<F> entailedExamples) {
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

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("DiagnosisModel{");
        sb.append(possiblyFaultyFormulas.size()).append(" possiblyFaultyFormulas=").append(new ArrayList<>(possiblyFaultyFormulas));
        sb.append(',').append(correctFormulas.size()).append(" correctFormulas=").append(new ArrayList<>(correctFormulas));
        sb.append(',').append(entailedExamples.size()).append(" entailedExamples=").append(new ArrayList<>(entailedExamples));
        sb.append(',').append(notEntailedExamples.size()).append(" notEntailedExamples=").append(new ArrayList<>(notEntailedExamples));
        sb.append(',').append(consistentExamples.size()).append(" consistentExamples=").append(new ArrayList<>(consistentExamples));
        sb.append(',').append(inconsistentExamples.size()).append(" inconsistentExamples=").append(new ArrayList<>(inconsistentExamples));
        sb.append(", formulaWeights=").append(formulaWeights);
        sb.append('}');
        return sb.toString();
    }
}
