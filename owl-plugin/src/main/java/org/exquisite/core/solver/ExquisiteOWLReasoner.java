package org.exquisite.core.solver;

import org.exquisite.core.IExquisiteProgressMonitor;
import org.exquisite.core.model.DiagnosisModel;
import org.semanticweb.owlapi.model.*;
import org.semanticweb.owlapi.reasoner.*;
import org.semanticweb.owlapi.util.*;
import org.slf4j.LoggerFactory;
import uk.ac.manchester.cs.owlapi.modularity.ModuleType;
import uk.ac.manchester.cs.owlapi.modularity.SyntacticLocalityModuleExtractor;

import java.util.*;
import java.util.stream.Collectors;

import static org.exquisite.core.perfmeasures.PerfMeasurementManager.*;

/**
 * Reasoner for OWL-Ontologies.
 *
 * @author kostya
 */
public class ExquisiteOWLReasoner extends AbstractSolver<OWLLogicalAxiom> {

    private static org.slf4j.Logger logger = LoggerFactory.getLogger(ExquisiteOWLReasoner.class.getCanonicalName());

    private final OWLReasoner reasoner;
    private InferenceType[] inferenceTypes = new InferenceType[]{InferenceType.CLASS_HIERARCHY,
            InferenceType.CLASS_ASSERTIONS, InferenceType.DISJOINT_CLASSES, InferenceType.DIFFERENT_INDIVIDUALS,
            InferenceType.SAME_INDIVIDUAL};
    private HashSet<InferredAxiomGenerator<? extends OWLLogicalAxiom>> axiomGenerators = new HashSet<>();

    /**
     * An anonymous ontology that is used by the internal OWLReasoner for debugging sessions.
     */
    private OWLOntology debuggingOntology;

    /**
     * Default constructor of the reasoner
     *
     * @param dm              a diagnosis model
     * @param manager         ontology manager
     * @param reasonerFactory of a reasoner expressive enough to reason about consistency of the ontology
     * @throws OWLOntologyCreationException An exception which describes an error during the creation of an ontology.
     */
    public ExquisiteOWLReasoner(DiagnosisModel<OWLLogicalAxiom> dm,
                                OWLOntologyManager manager, OWLReasonerFactory reasonerFactory)
            throws OWLOntologyCreationException {
        super(dm);
        this.debuggingOntology = manager.createOntology(); // use of anonymous ontology as debugging ontology
        this.reasoner = reasonerFactory.createReasoner(debuggingOntology);
        checkDiagnosisModel();
    }

    /**
     * Creates a default diagnosis model which possibly faulty statements comprise:
     * 1) all logical axioms, if the ontology is inconsistent, and
     * 2) only *star* modules of unsatisfiable classes it the ontology is consistent, but its terminology is incoherent.
     * Moreover, in case of an incoherent ontology, for each unsatisfiable class the correct statements of the model
     * comprise an assertion axiom, thus reducing the incoherency to inconsistency. The latter is usually easier for a
     * reasoner to compute.
     *
     * @param ontology        for which a diagnosis model must be generated
     * @return a diagnosis model
     * @throws OWLOntologyCreationException´An exception which describes an error during the creation of an ontology.
     */
    public static DiagnosisModel<OWLLogicalAxiom> generateDiagnosisModel(OWLOntology ontology)
            throws OWLOntologyCreationException {

        final long start = System.currentTimeMillis();

        Set<OWLLogicalAxiom> possiblyFaulty = new TreeSet<>();
        DiagnosisModel<OWLLogicalAxiom> dm = new DiagnosisModel<>();
        for (OWLLogicalAxiom axiom : ontology.getLogicalAxioms()) {
            Set<OWLAnnotationProperty> propertiesInSignature = axiom.getAnnotationPropertiesInSignature();
            if (propertiesInSignature.iterator().hasNext()) {
                OWLAnnotationProperty property = propertiesInSignature.iterator().next();
                if (property.isComment()) {
                    OWLAnnotationValue annotationValue = axiom.getAnnotations(propertiesInSignature.iterator().next()).iterator().next().getValue();

                    String comment = (((OWLLiteral) annotationValue).getLiteral());
                    switch (comment) {
                        case "B":
                            dm.getCorrectFormulas().add(axiom);
                            break;
                        case "P":
                            dm.getEntailedExamples().add(axiom);
                            break;
                        case "N":
                            dm.getNotEntailedExamples().add(axiom);
                            break;
                        default:
                    }
                }
            }
        }

        possiblyFaulty.addAll(ontology.getLogicalAxioms());
        // make sure that all sets are disjoint
        possiblyFaulty.removeAll(dm.getCorrectFormulas());
        possiblyFaulty.removeAll(dm.getEntailedExamples());
        possiblyFaulty.removeAll(dm.getNotEntailedExamples());

        dm.setPossiblyFaultyFormulas(possiblyFaulty);

        logger.info("-------------------------- Generated Diagnosis Model ---------------------------");
        logger.info("Ontology: {}", ontology.getOntologyID());
        logger.info("Generated in {} ms", (System.currentTimeMillis() - start));
        logger.info("{} Possibly Faulty Formulas", dm.getPossiblyFaultyFormulas().size());
        logger.info("{} Correct Formulas", dm.getCorrectFormulas().size());
        logger.info("{} Entailed Examples", dm.getEntailedExamples().size());
        logger.info("{} Not-Entailed Examples", dm.getNotEntailedExamples().size());
        logger.info("--------------------------------------------------------------------------------");
        return dm;
    }

    public static DiagnosisModel<OWLLogicalAxiom> consistencyCheck(DiagnosisModel<OWLLogicalAxiom> dm, OWLOntology ontology,
                                                                   OWLReasonerFactory reasonerFactory, boolean extractModule, boolean reduceIncoherencyToInconsistency) {
        return consistencyCheck(dm, ontology, reasonerFactory, extractModule, reduceIncoherencyToInconsistency, null, null);
    }

    public static DiagnosisModel<OWLLogicalAxiom> consistencyCheck(DiagnosisModel<OWLLogicalAxiom> dm, OWLOntology ontology,
                                                                   OWLReasonerFactory reasonerFactory, boolean extractModule, boolean reduceIncoherencyToInconsistency, ReasonerProgressMonitor reasonerProgressMonitor, IExquisiteProgressMonitor exquisiteProgressMonitor)
    {

        try {

            final long start = System.currentTimeMillis();
            OWLReasoner reasoner = createReasoner(ontology, reasonerFactory, reasonerProgressMonitor);
            OWLOntologyManager manager = ontology.getOWLOntologyManager();

            // start a new progress monitor task for consistency/coherency check
            if (exquisiteProgressMonitor != null)
                exquisiteProgressMonitor.taskStarted((reduceIncoherencyToInconsistency?IExquisiteProgressMonitor.CONSISTENCY_COHERENCY_CHECK:IExquisiteProgressMonitor.CONSISTENCY_CHECK) + " using " + getReasonerName(reasoner));


            logger.info("------------------------ Settings for Consistency Check ------------------------");
            logger.info("Ontology: {}", ontology.getOntologyID());
            logger.info("OWLOntologyManager: {}", manager);
            logger.info("OWLReasonerFactory: {}", reasonerFactory);
            logger.info("OWLReasoner: {}", reasoner);
            logger.info("Configuration [extractModules]: {}", extractModule);
            logger.info("Configuration [reduceIncoherency]: {}", reduceIncoherencyToInconsistency);

            Set<OWLLogicalAxiom> possiblyFaulty = new TreeSet<>();

            // in case the ontology is consistent we assume that the user wants to debug the incoherency.
            if ( exquisiteProgressMonitor != null ) exquisiteProgressMonitor.taskBusy("checking consistency of ontology...");
            if (reasoner.isConsistent()) {
                if ( exquisiteProgressMonitor != null ) {
                    exquisiteProgressMonitor.taskBusy("... the ontology is consistent!");
                    exquisiteProgressMonitor.taskBusy("pre-computing inferences of " + InferenceType.CLASS_HIERARCHY + "...");
                }
                reasoner.precomputeInferences(InferenceType.CLASS_HIERARCHY);
                if ( exquisiteProgressMonitor != null ) exquisiteProgressMonitor.taskBusy("detecting entities of bottom hierarchy nodes...");
                Set<OWLClass> classes = reasoner.getBottomClassNode().getEntities();
                classes.remove(manager.getOWLDataFactory().getOWLNothing());

                if (extractModule && classes.size() > 0) {
                    if ( exquisiteProgressMonitor != null ) exquisiteProgressMonitor.taskBusy("module extraction with signature of " + classes.size() + " entities...");
                    SyntacticLocalityModuleExtractor extractor = new SyntacticLocalityModuleExtractor(manager,
                            ontology, ModuleType.STAR);

                    Set<OWLEntity> entities = classes.stream()
                            .map(o -> (OWLEntity) o).collect(Collectors.toSet());

                    possiblyFaulty = extractor.extract(entities).stream().filter(OWLLogicalAxiom.class::isInstance).
                            map(o -> (OWLLogicalAxiom) o).collect(Collectors.toSet());

                } else {
                    possiblyFaulty.addAll(ontology.getLogicalAxioms());
                }

                // instantiate unsat classes thus reducing the incoherency to inconsistency
                if (reduceIncoherencyToInconsistency) {
                    if ( exquisiteProgressMonitor != null ) exquisiteProgressMonitor.taskBusy("checking coherency of ontology...");
                    final List<OWLLogicalAxiom> correctFormulas = dm.getCorrectFormulas();
                    for (OWLClass cl : classes) {
                        OWLDataFactory df = manager.getOWLDataFactory();
                        OWLIndividual ind = df.getOWLAnonymousIndividual();
                        final OWLClassAssertionAxiom axiom = df.getOWLClassAssertionAxiom(cl, ind);
                        addIfAxiomIsAbsent(correctFormulas, axiom);
                    }
                }
            } else {
                if ( exquisiteProgressMonitor != null ) exquisiteProgressMonitor.taskBusy("... the ontology is inconsistent!");
                possiblyFaulty.addAll(ontology.getLogicalAxioms());
            }

            // make sure that all sets are disjoint
            possiblyFaulty.removeAll(dm.getCorrectFormulas());
            possiblyFaulty.removeAll(dm.getEntailedExamples());
            possiblyFaulty.removeAll(dm.getNotEntailedExamples());

            dm.setPossiblyFaultyFormulas(possiblyFaulty);
            reasoner.dispose();

            logger.info("-------------------------- Diagnosis Model ---------------------------");
            logger.info("Checked in {} ms", (System.currentTimeMillis() - start));
            logger.info("{} Possibly Faulty Formulas", dm.getPossiblyFaultyFormulas().size());
            logger.info("{} Correct Formulas", dm.getCorrectFormulas().size());
            logger.info("{} Entailed Examples", dm.getEntailedExamples().size());
            logger.info("{} Not-Entailed Examples", dm.getNotEntailedExamples().size());
            logger.info("--------------------------------------------------------------------------------");
            return dm;
        } finally {
            // In the advent of some exception (which might occur, depending on the reasoners and their support
            // of the given ontology, in any case we stop all tasks.
            if (reasonerProgressMonitor != null) reasonerProgressMonitor.reasonerTaskStopped();
            if (exquisiteProgressMonitor != null) exquisiteProgressMonitor.taskStopped();
        }
    }

    public static OWLReasoner createReasoner(OWLOntology ontology, OWLReasonerFactory factory, ReasonerProgressMonitor monitor) {
        OWLReasonerConfiguration configuration = new SimpleConfiguration(monitor);
        return factory.createReasoner(ontology, configuration);
    }

    /**
     * A method that checks it there is already an anonymous individual axiom defined in the list of correct formulas.
     *
     * @param correctFormulas
     * @param axiom
     * @return
     */
    private static boolean addIfAxiomIsAbsent(final List<OWLLogicalAxiom> correctFormulas, final OWLClassAssertionAxiom axiom) {
        boolean isAxiomAlreadyPresent = false;
        final int size = correctFormulas.size();
        for (int i = 0; i < size && !isAxiomAlreadyPresent; i++) {
            final OWLLogicalAxiom _ax = correctFormulas.get(i);
            if (_ax instanceof OWLClassAssertionAxiom) {
                final OWLClassAssertionAxiom ax = (OWLClassAssertionAxiom) _ax;
                final Set<OWLClass> axClassesInSignature = ax.getClassesInSignature();
                final OWLIndividual axIndividual = ax.getIndividual();

                final Set<OWLClass> axiomClassesInSignature = axiom.getClassesInSignature();
                final OWLIndividual axiomIndividual = axiom.getIndividual();

                final boolean areEqualClassesInSignature = axClassesInSignature.equals(axiomClassesInSignature);
                isAxiomAlreadyPresent = (areEqualClassesInSignature && axIndividual.isAnonymous() && axiomIndividual.isAnonymous());
            }
        }

        return !isAxiomAlreadyPresent && correctFormulas.add(axiom);
    }


    /**
     * Sets types of entailements that must be computed by {@link #calculateEntailments()}
     *
     * @param infType entailment type
     */
    public void setEntailmentTypes(InferenceType... infType) {
        this.inferenceTypes = infType;
        HashSet<InferredAxiomGenerator<? extends OWLLogicalAxiom>> types = new HashSet<>(12);

        for (InferenceType type : infType) {
            switch (type) {
                case DISJOINT_CLASSES:
                    types.add(new InferredDisjointClassesAxiomGenerator());
                    break;
                case CLASS_HIERARCHY:
                    types.add(new InferredSubClassAxiomGenerator());
                    types.add(new InferredEquivalentClassAxiomGenerator());
                    break;
                case OBJECT_PROPERTY_HIERARCHY:
                    types.add(new InferredSubObjectPropertyAxiomGenerator());
                    types.add(new InferredInverseObjectPropertiesAxiomGenerator());
                    types.add(new InferredObjectPropertyCharacteristicAxiomGenerator());
                    types.add(new InferredEquivalentObjectPropertyAxiomGenerator());
                    break;
                case DATA_PROPERTY_HIERARCHY:
                    types.add(new InferredDataPropertyCharacteristicAxiomGenerator());
                    types.add(new InferredEquivalentDataPropertiesAxiomGenerator());
                    types.add(new InferredSubDataPropertyAxiomGenerator());
                    break;
                case CLASS_ASSERTIONS:
                    types.add(new InferredClassAssertionAxiomGenerator());
                    break;
                case OBJECT_PROPERTY_ASSERTIONS:
                    types.add(new InferredPropertyAssertionGenerator());
                    break;
                case DATA_PROPERTY_ASSERTIONS:
                    types.add(new InferredPropertyAssertionGenerator());
                    break;
                case SAME_INDIVIDUAL: // same case as different individual
                case DIFFERENT_INDIVIDUALS:
                    types.add(new InferredPropertyAssertionGenerator());
                    types.add(new InferredClassAssertionAxiomGenerator());
                    break;
                default:
                    throw new RuntimeException("Unknown inference type!");
            }
        }
        this.axiomGenerators = types;
    }


    /**
     * Uses underlying OWL reasoner to compute a set of entailments of formulas stored in the reasoner. The types of
     * computed entailments must be defined prior to calling this method using {@link #setEntailmentTypes(InferenceType...)}
     */
    @Override
    protected Set<OWLLogicalAxiom> calculateEntailments() {
        start(TIMER_SOLVER_CALCULATE_ENTAILMENTS);
        incrementCounter(COUNTER_SOLVER_CALCULATE_ENTAILMENTS);
        try {
            this.reasoner.precomputeInferences(this.inferenceTypes);
            OWLOntology ontology = this.reasoner.getRootOntology();

            return this.axiomGenerators.stream().flatMap(inferredAxiomGenerator ->
                    inferredAxiomGenerator.createAxioms(ontology.getOWLOntologyManager().getOWLDataFactory(), reasoner).stream()).
                    map(o -> (OWLLogicalAxiom) o).collect(Collectors.toSet());
        } finally {
            stop(TIMER_SOLVER_CALCULATE_ENTAILMENTS);
        }
    }

    @Override
    protected boolean isEntailed(Collection<OWLLogicalAxiom> entailments) {
        start(TIMER_SOLVER_ISENTAILED);
        incrementCounter(COUNTER_SOLVER_ISENTAILED);
        try {
            return this.reasoner.isEntailed(new HashSet<OWLAxiom>(entailments));
        } finally {
            stop(TIMER_SOLVER_ISENTAILED);
        }
    }

    @Override
    protected OWLLogicalAxiom negate(OWLLogicalAxiom example) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected boolean supportsNegation() {
        return false;
    }

    @Override
    protected void sync(Set<OWLLogicalAxiom> addFormulas, Set<OWLLogicalAxiom> removeFormulas) {
        OWLOntology ontology = this.reasoner.getRootOntology();
        OWLOntologyManager manager = ontology.getOWLOntologyManager();
        manager.removeAxioms(ontology, removeFormulas);
        manager.addAxioms(ontology, addFormulas);
        this.reasoner.flush();
    }

    @Override
    protected boolean isConsistent() {
        start(TIMER_SOLVER_ISCONSISTENT);
        incrementCounter(COUNTER_SOLVER_ISCONSISTENT);
        try {
            return this.reasoner.isConsistent();
        } finally {
            stop(TIMER_SOLVER_ISCONSISTENT);
        }
    }

    @Override
    public void dispose() {
        super.dispose();
        final OWLOntology ontology = this.reasoner.getRootOntology();
        if (!ontology.equals(this.debuggingOntology))
            throw new UnsupportedOperationException("reasoners root ontology does not equal the debugging ontology");
        OWLOntologyManager ontologyManager = ontology.getOWLOntologyManager();
        ontologyManager.removeOntology(ontology);
        this.axiomGenerators.clear();
        try {
            this.reasoner.dispose();
        } catch (RuntimeException rex) {
            // we can just ignore any runtime exception since there are reasoners like HermitT, that always
            // throw an RuntimeException (in the case of HermiT: NullPointerException)
        }
    }

    public OWLOntology getDebuggingOntology() {
        return debuggingOntology;
    }

    private static String getReasonerName(OWLReasoner reasoner) {
        if (reasoner.getReasonerName() !=null)
            return reasoner.getReasonerName();
        else
            return reasoner.getClass().getName();
    }

    @Override
    public String toString() {
        return "ExquisiteOWLReasoner(" + getReasonerName(reasoner) + ")";
    }
}
