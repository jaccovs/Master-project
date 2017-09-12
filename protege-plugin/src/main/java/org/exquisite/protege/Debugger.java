package org.exquisite.protege;

import org.exquisite.core.DiagnosisException;
import org.exquisite.core.DiagnosisRuntimeException;
import org.exquisite.core.IExquisiteProgressMonitor;
import org.exquisite.core.costestimators.CardinalityCostEstimator;
import org.exquisite.core.costestimators.ICostsEstimator;
import org.exquisite.core.costestimators.OWLAxiomKeywordCostsEstimator;
import org.exquisite.core.costestimators.SimpleCostsEstimator;
import org.exquisite.core.engines.AbstractDiagnosisEngine;
import org.exquisite.core.engines.IDiagnosisEngine;
import org.exquisite.core.model.Diagnosis;
import org.exquisite.core.model.DiagnosisModel;
import org.exquisite.core.query.Answer;
import org.exquisite.core.query.Query;
import org.exquisite.core.query.querycomputation.IQueryComputation;
import org.exquisite.core.query.querycomputation.heuristic.HeuristicConfiguration;
import org.exquisite.core.query.querycomputation.heuristic.HeuristicQueryComputation;
import org.exquisite.core.query.querycomputation.heuristic.partitionmeasures.*;
import org.exquisite.core.query.querycomputation.heuristic.sortcriteria.MinMaxFormulaWeights;
import org.exquisite.core.query.querycomputation.heuristic.sortcriteria.MinQueryCardinality;
import org.exquisite.core.query.querycomputation.heuristic.sortcriteria.MinSumFormulaWeights;
import org.exquisite.protege.model.DebuggingSession;
import org.exquisite.protege.model.TestcasesModel;
import org.exquisite.protege.model.error.AbstractErrorHandler;
import org.exquisite.protege.model.error.QueryErrorHandler;
import org.exquisite.protege.model.event.EventType;
import org.exquisite.protege.model.event.OntologyDebuggerChangeEvent;
import org.exquisite.protege.model.exception.DiagnosisModelCreationException;
import org.exquisite.protege.model.listener.OntologyChangeListener;
import org.exquisite.protege.model.preferences.DebuggerConfiguration;
import org.exquisite.protege.model.preferences.DiagnosisEngineFactory;
import org.exquisite.protege.model.state.PagingState;
import org.exquisite.protege.ui.dialog.DebuggingDialog;
import org.exquisite.protege.ui.list.item.AxiomListItem;
import org.exquisite.protege.ui.panel.repair.RepairDiagnosisPanel;
import org.exquisite.protege.ui.progress.DebuggerProgressUI;
import org.protege.editor.core.log.LogBanner;
import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.model.OWLModelManager;
import org.protege.editor.owl.model.inference.OWLReasonerManager;
import org.protege.editor.owl.model.inference.ReasonerStatus;
import org.protege.editor.owl.ui.UIHelper;
import org.semanticweb.owlapi.model.OWLLogicalAxiom;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.reasoner.ReasonerInternalException;
import org.semanticweb.owlapi.reasoner.ReasonerProgressMonitor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import javax.swing.event.ChangeListener;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

import static org.exquisite.protege.Debugger.ErrorStatus.*;

public class Debugger {

    private Logger logger = LoggerFactory.getLogger(Debugger.class.getCanonicalName());

    public enum TestcaseType {ENTAILED_TC, NON_ENTAILED_TC, ORIGINAL_ENTAILED_TC, ORIGINAL_NON_ENTAILED_TC, ACQUIRED_ENTAILED_TC, ACQUIRED_NON_ENTAILED_TC}

    public enum ErrorStatus {NO_CONFLICT_EXCEPTION, SOLVER_EXCEPTION, INCONSISTENT_THEORY_EXCEPTION,
        NO_QUERY, ONLY_ONE_DIAG, NO_ERROR, UNKNOWN_RM, UNKNOWN_SORTCRITERION, RUNTIME_EXCEPTION}

    public enum QuerySearchStatus { IDLE, ASKING_QUERY }

    /**
     * When calling doStopSession() use this flag to inform the user, why the debugging session has stopped.
     */
    public enum SessionStopReason { PREFERENCES_CHANGED, INVOKED_BY_USER, CONSISTENT_ONTOLOGY,
        ERROR_OCCURRED, DEBUGGER_RESET, REASONER_CHANGED, ONTOLOGY_RELOADED, ONTOLOGY_CHANGED, SESSION_RESTARTED,
        DEBUGGING_ONTOLOGY_SELECTED, REPAIR_FINISHED
    }

    private DebuggingSession debuggingSession;

    private QuerySearchStatus querySearchStatus = QuerySearchStatus.IDLE;

    private DiagnosisEngineFactory diagnosisEngineFactory;

    private ErrorStatus errorStatus = NO_ERROR;

    private Set<ChangeListener> changeListeners = new LinkedHashSet<>();

    private Set<Diagnosis<OWLLogicalAxiom>> diagnoses = new HashSet<>();

    private Set<Diagnosis<OWLLogicalAxiom>> previousDiagnoses = new HashSet<>();

    private Set<Set<OWLLogicalAxiom>> conflicts = new HashSet<>();

    private Answer<OWLLogicalAxiom> answer = new Answer<>();

    private Answer<OWLLogicalAxiom> previousAnswer = new Answer<>();

    private List<Answer<OWLLogicalAxiom>> queryHistory = new LinkedList<>();

    private IQueryComputation<OWLLogicalAxiom> qc = null;

    private OWLEditorKit editorKit = null;

    private final OWLReasonerManager reasonerManager;

    private Double cautiousParameter, previousCautiousParameter;

    private TestcasesModel testcases;

    private DiagnosisModel<OWLLogicalAxiom> diagnosisModel;

    // state information for the paging mode of the InputOntologyView
    private PagingState pagingState;

    /**
     * Singleton instance of an listener to ontology changes. Registered in the EditorKitHook.
     */
    private OntologyChangeListener ontologyChangeListener;

    private DebuggerProgressUI progressUI;

    private org.protege.editor.owl.ui.inference.ReasonerProgressUI reasonerProgressUI;

    public Debugger(OWLEditorKit editorKit) {
        this.editorKit = editorKit;
        OWLModelManager modelManager = editorKit.getModelManager();
        this.reasonerManager = modelManager.getOWLReasonerManager();
        this.diagnosisEngineFactory = new DiagnosisEngineFactory(this, modelManager.getActiveOntology(), this.reasonerManager);
        this.debuggingSession = new DebuggingSession();
        this.testcases = new TestcasesModel(this);
        this.diagnosisModel = new DiagnosisModel<>();
        this.pagingState = new PagingState();
        this.progressUI = new DebuggerProgressUI(this);
        this.reasonerProgressUI = new org.protege.editor.owl.ui.inference.ReasonerProgressUI(this.getEditorKit());
    }

    /**
     * Signals to the debugger that the diagnosis model has to be synced with the underlying ontology
     * because of an event (such as ontology change, reload, etc.) that demands the recreation of the diagnosis model.
     */
    public void syncDiagnosisModel() {
        try {
            this.diagnosisModel = diagnosisEngineFactory.createDiagnosisModel();
            notifyListeners(new OntologyDebuggerChangeEvent(this, EventType.SESSION_STATE_CHANGED));
        } catch (DiagnosisModelCreationException e) {
            logger.error("An error occurred during creation of a new diagnosis model for " +
                    DebuggingDialog.getOntologyName(getDiagnosisEngineFactory().getOntology()), e);
        }
    }

    void createNewDiagnosisModel() throws DiagnosisModelCreationException {
        this.diagnosisModel = diagnosisEngineFactory.createDiagnosisModel();
    }

    public IExquisiteProgressMonitor getExquisiteProgressMonitor() {
        return progressUI;
    }

    public ReasonerProgressMonitor getReasonerProgressMonitor() {
        return reasonerProgressUI;
    }

    public OWLEditorKit getEditorKit() {
        return editorKit;
    }

    public PagingState getPagingState() {
        return pagingState;
    }

    public DiagnosisModel<OWLLogicalAxiom> getDiagnosisModel() {
        return diagnosisModel;
    }

    public DiagnosisEngineFactory getDiagnosisEngineFactory() {
        return diagnosisEngineFactory;
    }

    public Query<OWLLogicalAxiom> getActualQuery() {
        return answer.query;
    }

    public Set<Diagnosis<OWLLogicalAxiom>> getDiagnoses() {
        return diagnoses;
    }

    public Set<Set<OWLLogicalAxiom>> getConflicts() {
        return conflicts;
    }

    private ErrorStatus getErrorStatus() {
        return errorStatus;
    }

    public List<Answer<OWLLogicalAxiom>> getQueryHistory() {
        return queryHistory;
    }

    public QuerySearchStatus getQuerySearchStatus() {
        return querySearchStatus;
    }

    public boolean isMarkedEntailed(OWLLogicalAxiom axiom) {
        return answer.positive.contains(axiom);
    }

    public boolean isMarkedNonEntailed(OWLLogicalAxiom axiom) {
        return answer.negative.contains(axiom);
    }

    public int sizeOfEntailedAndNonEntailedAxioms() {
        return answer.positive.size() + answer.negative.size();
    }

    public boolean isSessionRunning() {
        return debuggingSession.getState() == DebuggingSession.State.RUNNING;
    }

    public boolean isRepairing() {
        return debuggingSession.getState() == DebuggingSession.State.REPARING;
    }

    public boolean isSessionStopped() {
        return debuggingSession.getState() == DebuggingSession.State.STOPPED;
    }

    public TestcasesModel getTestcases() {
        return this.testcases;
    }

    void addChangeListener(ChangeListener listener) {
        changeListeners.add(listener);
    }

    private void removeChangeListener(ChangeListener listener) {
        changeListeners.remove(listener);
    }

    private void notifyListeners(OntologyDebuggerChangeEvent e) {
        for (ChangeListener listener : changeListeners)
            listener.stateChanged(e);
    }

    OntologyChangeListener getOntologyChangeListener() {
        if (this.ontologyChangeListener == null)
            this.ontologyChangeListener = new OntologyChangeListener(this);
        return this.ontologyChangeListener;
    }

    public void doStartDebuggingAsync(QueryErrorHandler errorHandler) {
        new Thread(() -> doStartDebugging(errorHandler)).start();
    }

    /**
     * Starts a new debugging session. This step initiates the search for a diagnosis and the presentation of a
     * query.
     *
     * @param errorHandler An error handler.
     */
    private void doStartDebugging(QueryErrorHandler errorHandler) {
        if (!isSessionRunning() && !isRepairing()) {
            if (reasonerManager.getReasonerStatus() == ReasonerStatus.NO_REASONER_FACTORY_CHOSEN) {
                DebuggingDialog.showNoReasonerSelectedMessage();
                return;
            }

            try {
                logger.info(LogBanner.start("Starting new Debugging Session"));
                debuggingSession.startSession();                // sets the start session flag
                diagnosisEngineFactory.reset();                 // create new engine
                this.diagnosisModel = diagnosisEngineFactory.consistencyCheck(this.getDiagnosisModel());
                notifyListeners(new OntologyDebuggerChangeEvent(this, EventType.SESSION_STATE_CHANGED));
                doCalculateDiagnosesAndGetQuery(errorHandler);  // calculate diagnoses and compute query
            } catch (DiagnosisModelCreationException e) {
                logger.error(e.getMessage(), e);
                DebuggingDialog.showErrorDialog("Error consistency check", e.getMessage(), e);
                doStopDebugging(SessionStopReason.ERROR_OCCURRED);
            } catch (DiagnosisRuntimeException e) {
                logger.error(e.getMessage(), e);
                DebuggingDialog.showErrorDialog("Diagnosis runtime exception occurred", e.getMessage(), e);
                doStopDebugging(SessionStopReason.ERROR_OCCURRED);
            } catch (RuntimeException e) {
                logger.error(e.getMessage(), e);
                DebuggingDialog.showErrorDialog("Unexpected exception occurred", e.getMessage(), e);
                doStopDebugging(SessionStopReason.ERROR_OCCURRED);
            }
        }
    }

    /**
     * Stops the current running debugging session and restarts with another one.
     *
     * @param errorHandler An error handler.
     */
    public void doRestartDebugging(QueryErrorHandler errorHandler) {
        doStopDebugging(SessionStopReason.SESSION_RESTARTED);
        doStartDebuggingAsync(errorHandler);
    }

    /**
     * Stop diagnosis session -> reset engine, diagnoses, conflicts, queries and history.
     */
    public void doStopDebugging(SessionStopReason reason) {
        if (!isRepairing()) {
            if (isSessionRunning()) {
                logger.info(LogBanner.start("Stopping Debugging Session"));

                try {
                    diagnosisEngineFactory.dispose();
                } catch (RuntimeException rex) {
                    logger.error("A runtime exception occurred while disposing diagnosis engine", rex);
                }

                this.diagnoses.clear();                                     // reset diagnoses, conflicts
                this.conflicts.clear();

                this.previousDiagnoses.clear();

                resetQuery();                                               // reset queries
                resetQueryHistory();                                        // reset history
                testcases.reset();
                debuggingSession.stopSession();                             // stop session

                this.cautiousParameter = null;
                this.previousCautiousParameter = null;

                notifyListeners(new OntologyDebuggerChangeEvent(this, EventType.SESSION_STATE_CHANGED));

                // notify the user why session has stopped
                String reasonMsg = null;
                switch (reason) {
                    case ERROR_OCCURRED:
                        reasonMsg = "an unexpected error occured!";
                        break;
                    case ONTOLOGY_RELOADED:
                        syncDiagnosisModel();
                        reasonMsg = "the ontology " + DebuggingDialog.getOntologyName(diagnosisEngineFactory.getOntology()) + " has been reloaded.";
                        break;
                    case ONTOLOGY_CHANGED:
                        syncDiagnosisModel();
                        reasonMsg = "the ontology " + DebuggingDialog.getOntologyName(diagnosisEngineFactory.getOntology()) + " has been modified!";
                        break;
                    case PREFERENCES_CHANGED:
                        syncDiagnosisModel();
                        reasonMsg = "the preferences have been modified!";
                        break;
                    case REASONER_CHANGED:
                        syncDiagnosisModel();
                        reasonMsg = "the reasoner has been changed!";
                        break;
                    case DEBUGGING_ONTOLOGY_SELECTED:
                        reasonMsg = "an anonymous debugging ontology has been selected!";
                        break;
                    case INVOKED_BY_USER:     // no message necessary
                    case DEBUGGER_RESET:      // no message necessary
                    case CONSISTENT_ONTOLOGY: // no message necessary
                    case SESSION_RESTARTED:   // no message necessary
                    case REPAIR_FINISHED:     // no message necessary
                        final DebuggerConfiguration configuration = diagnosisEngineFactory.getSearchConfiguration();
                        if (configuration.reduceIncoherency && configuration.extractModules) {
                            // During the debugging session the diagnosis model also has been reduced to set of axioms from
                            // the extracted module and needs to be recreated.
                            syncDiagnosisModel();
                        }
                        break;
                    default:
                        reasonMsg = reason.toString();
                        logger.warn("unknown reason: " + reason);
                }

                if (reasonMsg != null)
                    DebuggingDialog.showDebuggingSessionStoppedMessage(diagnosisEngineFactory.getOntology(), reasonMsg);
            } else {
                switch (reason) {
                    case REASONER_CHANGED:
                    case PREFERENCES_CHANGED:
                    case ONTOLOGY_CHANGED:
                    case ONTOLOGY_RELOADED:
                        syncDiagnosisModel();
                        break;
                }
            }
        }
    }

    /**
     * Reset debugger ->  reset test cases + doStopDebugging()
     */
    public void doResetDebugger() {
        doStopDebugging(SessionStopReason.DEBUGGER_RESET);
    }

    /**
     * Reset the diagnoses engine and doFullReset().
     */
    void doReload() {
        doStopDebugging(SessionStopReason.ONTOLOGY_RELOADED);
    }

    public void doStartRepair() {
        if (!isRepairing() && isSessionRunning() && getDiagnoses().size() == 1) {
            this.debuggingSession.startRepair();

            try {
                RepairDiagnosisPanel repairPanel = new RepairDiagnosisPanel(getEditorKit());
                int ret = new UIHelper(editorKit).showDialog("Repair for " + getDiagnoses().toString(), repairPanel, JOptionPane.OK_CANCEL_OPTION);

                switch (ret) {
                    case JOptionPane.CLOSED_OPTION:
                    case JOptionPane.CANCEL_OPTION:
                        repairPanel.reset();
                    case JOptionPane.OK_OPTION:
                        repairPanel.dispose();
                        this.debuggingSession.stopRepair();
                        if (repairPanel.hasChanged()) {
                            doStopDebugging(SessionStopReason.REPAIR_FINISHED);
                        }
                        break;
                }
            } catch (OWLOntologyCreationException e) {
                logger.error(e.getMessage(), e);
                DebuggingDialog.showErrorDialog("Unexpected exception occurred", e.getMessage(), e);
            }


        }
    }

    private void resetQuery() {
        this.previousAnswer = this.answer;
        this.answer = new Answer<>();

        this.querySearchStatus = QuerySearchStatus.IDLE;
        if (this.qc!=null) qc.reset();
    }

    private void resetQueryHistory() {
        this.queryHistory.clear();
    }

    /**
     * Moves a set of correct axioms to the set of possibly faulty axioms in the diagnosis model.
     *
     * @param selectedCorrectAxioms The selected, yet correct, axioms that shall become possibly faulty.
     */
    public void moveToPossiblyFaultyAxioms(List<AxiomListItem> selectedCorrectAxioms) {
        logger.debug("moving " + selectedCorrectAxioms + " from background to possiblyFaultyAxioms");
        List<OWLLogicalAxiom> axioms = selectedCorrectAxioms.stream().map(AxiomListItem::getAxiom).collect(Collectors.toList());
        diagnosisModel.getCorrectFormulas().removeAll(axioms);
        diagnosisModel.getPossiblyFaultyFormulas().addAll(axioms);
        notifyListeners(new OntologyDebuggerChangeEvent(this, EventType.INPUT_ONTOLOGY_CHANGED));
    }

    /**
     * Moves a list of possibly faulty axioms to the set of correct axioms in the diagnosis model.
     *
     * @param selectedPossiblyFaultyAxioms The selected, yet possibly faulty, axioms that shall become correct axioms.
     */
    public void moveToToCorrectAxioms(List<AxiomListItem> selectedPossiblyFaultyAxioms) {
        logger.debug("moving " + selectedPossiblyFaultyAxioms + " from possiblyFaultyAxioms to correctAxioms");
        List<OWLLogicalAxiom> axioms = selectedPossiblyFaultyAxioms.stream().map(AxiomListItem::getAxiom).collect(Collectors.toList());
        diagnosisModel.getPossiblyFaultyFormulas().removeAll(axioms);
        diagnosisModel.getCorrectFormulas().addAll(axioms);
        notifyListeners(new OntologyDebuggerChangeEvent(this, EventType.INPUT_ONTOLOGY_CHANGED));
    }

    /**
     * This method removes axioms of a certain TestcaseType from the history and <strong>additionally</strong>
     * recalculates the diagnoses and computes a new query afterwards (asynchronously).
     * @param axioms entailed or non entailed axioms from acquired or original test cases (answers)
     * @param type either ORIGINAL_ENTAILED_TC, ORIGINAL_NON_ENTAILED_TC, ACQUIRED_ENTAILED_TC or ACQUIRED_NON_ENTAILED_TC.
     */
    public void doRemoveTestcaseAsync(Set<OWLLogicalAxiom> axioms, TestcaseType type) {
        doRemoveTestcase(axioms, type);
        // When removing acquired test cases (see method doRemoveQueryHistoryAnswer())
        // this method is called twice.
        // We need to calculate the queries only after the second call of this method.
        if (isSessionRunning()) {
            new Thread(() -> doCalculateDiagnosesAndGetQuery(new QueryErrorHandler())).start();
        }
    }

    public void doRemoveTestcase(Set<OWLLogicalAxiom> axioms, TestcaseType type) {
        this.testcases.removeTestcase(axioms, type);

        // We also have to synchronize the query history (if the user removed the test case from the acquired test cases)
        if (axioms.size() == 1 && (type == TestcaseType.ACQUIRED_ENTAILED_TC || type == TestcaseType.ACQUIRED_NON_ENTAILED_TC)) {
            // It is possible that the user used the remove button in the acquired test cases view
            // Now search for the answer in the history
            Answer<OWLLogicalAxiom> answer = null;
            Iterator<Answer<OWLLogicalAxiom>> it = this.queryHistory.iterator();
            while (it.hasNext() && answer == null) {
                Answer<OWLLogicalAxiom> anAnswer = it.next();
                switch (type) {
                    case ACQUIRED_ENTAILED_TC:
                        if (anAnswer.positive.removeAll(axioms)) // note that only one axiom is removed
                            answer = anAnswer;
                        break;
                    case ACQUIRED_NON_ENTAILED_TC:
                        if (anAnswer.negative.removeAll(axioms)) // note that only one axiom is removed
                            answer = anAnswer;
                        break;
                    default:
                        throw new DiagnosisRuntimeException("Unexpected test case type used to clean up history: " + type);
                }
            }

            // we found an answer in the history we have to remove from history if the answer is empty
            if (answer != null && answer.positive.isEmpty() && answer.negative.isEmpty()) {
                this.queryHistory.remove(answer);
                logger.debug("Removed from history: " + answer);
            }

        }

        // When removing acquired test cases (see method doRemoveQueryHistoryAnswer())
        // this method is called twice.
        // We need to calculate the queries only after the second call of this method.
        //if (shallNewQueryBeComputed && isSessionRunning())
        //    doCalculateDiagnosesAndGetQuery(new QueryErrorHandler());
    }

    public void doAddTestcase(Set<OWLLogicalAxiom> axioms, TestcaseType type, AbstractErrorHandler errorHandler) {
        this.testcases.addTestcase(axioms, type);
        if(!getErrorStatus().equals(NO_ERROR))
            errorHandler.errorHappened(getErrorStatus());
    }

    /**
     * Check if the set of new acquired test cases is empty. This check is called by the ResetDebuggerAction.
     *
     * @return <code>true</code> if there are no acquired test cases yet, otherwise <code>false</code>.
     */
    public boolean areTestcasesEmpty() {
        return testcases.areTestcasesEmpty();
    }

    public void updateConfig(DebuggerConfiguration newConfiguration) {
        getDiagnosisEngineFactory().updateConfig(newConfiguration);
    }

    public void doAddAxiomsMarkedEntailed(OWLLogicalAxiom axiom) {
        this.answer.positive.add(axiom);
        notifyListeners(new OntologyDebuggerChangeEvent(this, EventType.QUERY_ANSWER_EVENT));
    }

    public void doAddAxiomsMarkedNonEntailed(OWLLogicalAxiom axiom) {
        this.answer.negative.add(axiom);
        notifyListeners(new OntologyDebuggerChangeEvent(this, EventType.QUERY_ANSWER_EVENT));
    }

    public void doRemoveAxiomsMarkedEntailed(OWLLogicalAxiom axiom) {
        this.answer.positive.remove(axiom);
        notifyListeners(new OntologyDebuggerChangeEvent(this, EventType.QUERY_ANSWER_EVENT));
    }

    public void doRemoveAxiomsMarkedNonEntailed(OWLLogicalAxiom axiom) {
        this.answer.negative.remove(axiom);
        notifyListeners(new OntologyDebuggerChangeEvent(this, EventType.QUERY_ANSWER_EVENT));
    }


    public void doCommitAndGetNewQueryAsync(QueryErrorHandler errorHandler) {
        new Thread(() -> doCommitAndGetNewQuery(errorHandler)).start();
    }

    /**
     * Commit the response from the expert, calculate the new diagnoses and get the new queries.
     *
     * @param errorHandler An error handler.
     */
    public void doCommitAndGetNewQuery(QueryErrorHandler errorHandler) {
        this.previousDiagnoses = new HashSet<>(this.diagnoses);
        doCommitQuery();
        doCalculateDiagnosesAndGetQuery(errorHandler);
    }

    public void doGetAlternativeQuery() {
        JOptionPane.showMessageDialog(null, "The function is not implemented yet", "Not Implemented", JOptionPane.INFORMATION_MESSAGE);
    }

    private void doCommitQuery() {
        if (!this.answer.positive.isEmpty()) {
            doAddTestcase(this.answer.positive, TestcaseType.ACQUIRED_ENTAILED_TC, new QueryErrorHandler());
        }
        if (!this.answer.negative.isEmpty()) {
            doAddTestcase(this.answer.negative, TestcaseType.ACQUIRED_NON_ENTAILED_TC, new QueryErrorHandler());
        }

        this.queryHistory.add(this.answer);

        resetQuery();
    }

    /**
     * Calculate the diagnoses.
     *
     * @param errorHandler An error handler
     * @return <code>true</code> if no error occurred, otherwise <code>false</code>.
     */
    private boolean doCalculateDiagnoses(AbstractErrorHandler errorHandler) {
        final IDiagnosisEngine<OWLLogicalAxiom> diagnosisEngine = diagnosisEngineFactory.getDiagnosisEngine();

        this.diagnoses.clear();
        this.conflicts.clear();
        diagnosisEngine.resetEngine();

        // set the cost estimator
        if (diagnosisEngine instanceof AbstractDiagnosisEngine) {
            final AbstractDiagnosisEngine abstractDiagnosisEngine = ((AbstractDiagnosisEngine) diagnosisEngine);
            final ICostsEstimator currentCostsEstimator = abstractDiagnosisEngine.getCostsEstimator();
            switch (diagnosisEngineFactory.getSearchConfiguration().costEstimator) {
                case EQUAL:
                    if (! (currentCostsEstimator instanceof SimpleCostsEstimator))
                        abstractDiagnosisEngine.setCostsEstimator(new SimpleCostsEstimator());
                    break;
                case CARD:
                    if (! (currentCostsEstimator instanceof CardinalityCostEstimator))
                        abstractDiagnosisEngine.setCostsEstimator(new CardinalityCostEstimator());
                    break;
                case SYNTAX:
                    if (! (currentCostsEstimator instanceof OWLAxiomKeywordCostsEstimator))
                        abstractDiagnosisEngine.setCostsEstimator(new OWLAxiomKeywordCostsEstimator(getDiagnosisModel()));
                    break;
                default:
                    logger.warn("Cost estimator " + diagnosisEngineFactory.getSearchConfiguration().costEstimator + " is unknown. Using " + currentCostsEstimator + " as cost estimator.");
            };

        }

        // set the maximum number of diagnoses to be calculated
        final int n = diagnosisEngineFactory.getSearchConfiguration().numOfLeadingDiags;
        diagnosisEngine.setMaxNumberOfDiagnoses(n);
        try {
            logger.debug("Calculating at most {} diagnoses ...", n);
            diagnoses.addAll(diagnosisEngine.calculateDiagnoses());
            conflicts.addAll(diagnosisEngine.getConflicts());
            logger.debug("Found {} diagnoses.", diagnoses.size());
            logger.debug("Diagnoses: " + diagnoses.toString());
            logger.debug("Diagnoses are based on {} conflicts", conflicts.size());
            logger.debug("Conflicts: " + conflicts.toString());
            return true;
        } catch (DiagnosisException | ReasonerInternalException e) {
            errorHandler.errorHappened(SOLVER_EXCEPTION, e);
            logger.error("Exception occurred while calculating diagnoses.", e);
            diagnoses.clear(); // reset diagnoses and conflicts
            conflicts.clear();
            return false;
        } catch (RuntimeException e) {
            errorHandler.errorHappened(RUNTIME_EXCEPTION, e);
            logger.error("Unexpected exception occurred while calculating diagnoses.", e);
            diagnoses.clear(); // reset diagnoses and conflicts
            conflicts.clear();
            return false;
        }

    }

    /**
     * First calculate diagnoses and compute queries afterwards.
     *
     * @param errorHandler The error handler.
     */
    private void doCalculateDiagnosesAndGetQuery(QueryErrorHandler errorHandler) {
        boolean noErrorOccur = doCalculateDiagnoses(errorHandler);
        if (noErrorOccur) {
            switch (diagnoses.size()) {
                case 0:
                    doStopDebugging(SessionStopReason.CONSISTENT_ONTOLOGY);
                    if (diagnosisEngineFactory.getSearchConfiguration().reduceIncoherency)
                        DebuggingDialog.showCoherentOntologyMessage(getDiagnosisEngineFactory().getOntology());
                    else
                        DebuggingDialog.showConsistentOntologyMessage(getDiagnosisEngineFactory().getOntology());
                    break;
                case 1:
                    notifyListeners(new OntologyDebuggerChangeEvent(this, EventType.DIAGNOSIS_FOUND));
                    DebuggingDialog.showDiagnosisFoundMessage(diagnoses, getDiagnosisEngineFactory().getOntology());
                    break;
                default:
                    noErrorOccur = doGetQuery(errorHandler);
                    if (!noErrorOccur)
                        doStopDebugging(SessionStopReason.ERROR_OCCURRED);
                    break;
            }
        } else {
            doStopDebugging(SessionStopReason.ERROR_OCCURRED);
        }
    }

    /**
     * The main method to calculate a new query.
     * The calling method has to check that the size of diagnoses is at least 2.
     *
     * @param errorHandler An error handler.
     */
    private boolean doGetQuery(QueryErrorHandler errorHandler) {

        final IDiagnosisEngine<OWLLogicalAxiom> diagnosisEngine = diagnosisEngineFactory.getDiagnosisEngine();
        final DebuggerConfiguration preference = diagnosisEngineFactory.getSearchConfiguration();
        HeuristicConfiguration<OWLLogicalAxiom> heuristicConfiguration = new HeuristicConfiguration<>((AbstractDiagnosisEngine)diagnosisEngine,this.progressUI);

        heuristicConfiguration.setMinQueries(1);
        heuristicConfiguration.setMaxQueries(1);

        heuristicConfiguration.setEnrichQueries(preference.enrichQuery);
        switch (preference.rm) {
            case ENT:
                heuristicConfiguration.setRm(
                        new EntropyBasedMeasure<>(new BigDecimal(String.valueOf(preference.entropyThreshold))));
                break;
            case SPL:
                heuristicConfiguration.setRm(
                        new SplitInHalfMeasure<>(new BigDecimal(String.valueOf(preference.entropyThreshold))));
                break;
            case RIO:

                heuristicConfiguration.setRm(
                        new RiskOptimizationMeasure<>(new BigDecimal(String.valueOf(preference.entropyThreshold)),
                        new BigDecimal(String.valueOf(preference.cardinalityThreshold)),
                        new BigDecimal(String.valueOf(updateCautiousParameter(preference.cautiousParameter)))));
                break;
            case KL:
                heuristicConfiguration.setRm(
                        new KLMeasure(new BigDecimal(String.valueOf(preference.entropyThreshold))));
                break;
            case EMCb:
                heuristicConfiguration.setRm(new EMCbMeasure());
                break;
            case BME:
                heuristicConfiguration.setRm(
                        new BMEMeasure(new BigDecimal(String.valueOf(preference.cardinalityThreshold))));
                break;
            default:
                errorHandler.errorHappened(ErrorStatus.UNKNOWN_RM);
                return false;
        }

        switch (preference.sortCriterion) {
            case MINCARD:
                heuristicConfiguration.setSortCriterion(new MinQueryCardinality<>());
                break;
            case MINSUM:
                heuristicConfiguration.setSortCriterion(new MinSumFormulaWeights<>(new OWLAxiomKeywordCostsEstimator(getDiagnosisModel()).getFormulaWeights(getDiagnosisEngineFactory().getOntology())));
                break;
            case MINMAX:
                heuristicConfiguration.setSortCriterion(new MinMaxFormulaWeights<>(new OWLAxiomKeywordCostsEstimator(getDiagnosisModel()).getFormulaWeights(getDiagnosisEngineFactory().getOntology())));
                break;
            default:
                errorHandler.errorHappened(ErrorStatus.UNKNOWN_SORTCRITERION);
                return false;
        }

        qc = new HeuristicQueryComputation<>(heuristicConfiguration);

        try {

            qc.initialize(diagnoses);

            if (qc.hasNext()) {
                this.answer.query = qc.next();
                logger.debug("query configuration: " + qc);
            } else {
                errorHandler.errorHappened(ErrorStatus.NO_QUERY);
                errorStatus = ErrorStatus.NO_QUERY;
                resetQuery();
                return false;
            }
            querySearchStatus = QuerySearchStatus.ASKING_QUERY;
            return true;
        } catch (DiagnosisException e) {
            errorHandler.errorHappened(ErrorStatus.SOLVER_EXCEPTION, e);
            return false;
        } catch (RuntimeException e) {
            errorHandler.errorHappened(ErrorStatus.RUNTIME_EXCEPTION, e);
            return false;
        } finally {
            notifyListeners(new OntologyDebuggerChangeEvent(this, EventType.QUERY_CALCULATED));
        }
    }

    /**
     * Method that learns the cautious parameter for RIO for each new query generation.
     *
     * @param preferenceCautiousParameter the unmodifiable cautious parameter from the preferences.
     */
    private Double updateCautiousParameter(Double preferenceCautiousParameter) {
        if (previousDiagnoses.size() > 0) {
            previousCautiousParameter = cautiousParameter;
            final double epsilon = 0.25;
            final double intervalLength = (Math.floor((double)previousDiagnoses.size() / 2d) - 1d) / (double)previousDiagnoses.size();

            logger.debug("epsilon: " + epsilon);
            logger.debug("intervalLength: " + intervalLength);
            logger.debug("old cautiousParameter: " + previousCautiousParameter);
            logger.debug("previousDiagnoses#: " + previousDiagnoses.size());
            logger.debug("diagnoses#: " + diagnoses.size());

            double eliminationRate = calculateEliminationRate();
            logger.debug("eliminationRate: " + eliminationRate);

            double adjustmentFactor = ((Math.floor((double)previousDiagnoses.size() / 2.0 - epsilon) + 0.5) / (double)previousDiagnoses.size()) - eliminationRate;
            logger.debug("adjustmentFactor: " + adjustmentFactor);

            double adjustedCautiousParameter = previousCautiousParameter + (2 * intervalLength * adjustmentFactor);
            final double minCautiousValue = 1.0 / (double) diagnoses.size();
            final double maxCautiousValue = Math.floor((double)diagnoses.size() / 2.0) / (double)diagnoses.size();

            logger.debug(adjustedCautiousParameter + " in [" + minCautiousValue + "," + maxCautiousValue + "] ?");

            if (adjustedCautiousParameter < minCautiousValue)
                adjustedCautiousParameter = minCautiousValue;
            if (adjustedCautiousParameter > maxCautiousValue)
                adjustedCautiousParameter = maxCautiousValue;

            cautiousParameter = adjustedCautiousParameter;
            logger.debug("NEW cautiousParameter: " + cautiousParameter);

        } else {
            cautiousParameter = preferenceCautiousParameter;
            previousCautiousParameter = null;
        }
        return cautiousParameter;
    }

    private Double calculateEliminationRate() {
        Double eliminationRate;
        if (this.previousAnswer.negative.size() >= 1)
            // this calculates the lower bound of the actual elimination rate
            eliminationRate = (double)this.previousAnswer.query.qPartition.dx.size() / (double)previousDiagnoses.size();
        else if (this.previousAnswer.positive.size() == this.previousAnswer.query.formulas.size())
            // this calculates the lower bound of the actual elimination rate
            eliminationRate = (double)this.previousAnswer.query.qPartition.dnx.size() / (double)previousDiagnoses.size();
        else
            // an approximation of the elimination rate (saves the costs to expensive reasoner calls)
            eliminationRate = ( (double)this.previousAnswer.query.qPartition.dnx.size() / (double)previousDiagnoses.size() ) * this.previousAnswer.positive.size() / this.previousAnswer.query.formulas.size();

        return eliminationRate;
    }

    public void doRemoveQueryHistoryAnswer(Answer<OWLLogicalAxiom> answer) {
        queryHistory.remove(answer);
        doRemoveTestcase(answer.positive, TestcaseType.ACQUIRED_ENTAILED_TC);
        doRemoveTestcaseAsync(answer.negative, TestcaseType.ACQUIRED_NON_ENTAILED_TC);
    }

    public void dispose(EditorKitHook editorKitHook) {
        removeChangeListener(editorKitHook);
        this.diagnosisEngineFactory.dispose();
    }

    @Override
    public String toString() {
        return "OntologyDebugger{" + "ontology=" + diagnosisEngineFactory.getOntology() +
                "reasonerManager=" + diagnosisEngineFactory.getReasonerManager() +
                '}';
    }

}
