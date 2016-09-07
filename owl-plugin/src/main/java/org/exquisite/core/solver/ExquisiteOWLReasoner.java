package org.exquisite.core.solver;

import org.exquisite.core.model.DiagnosisModel;
import org.semanticweb.owlapi.model.*;
import org.semanticweb.owlapi.reasoner.InferenceType;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import org.semanticweb.owlapi.reasoner.OWLReasonerFactory;
import org.semanticweb.owlapi.util.*;
import org.slf4j.LoggerFactory;
import uk.ac.manchester.cs.owlapi.modularity.ModuleType;
import uk.ac.manchester.cs.owlapi.modularity.SyntacticLocalityModuleExtractor;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
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
    private InferenceType[] interenceTypes = new InferenceType[]{InferenceType.CLASS_HIERARCHY,
            InferenceType.CLASS_ASSERTIONS, InferenceType.DISJOINT_CLASSES, InferenceType.DIFFERENT_INDIVIDUALS,
            InferenceType.SAME_INDIVIDUAL};
    private HashSet<InferredAxiomGenerator<? extends OWLLogicalAxiom>> axiomGenerators = new HashSet<>();

    /**
     * An anonymous ontology that is used by the internal OWLreasoner for debugging sessions.
     */
    private OWLOntology debuggingOntology = null;

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
     * @param reasonerFactory of a reasoner expressive enough to reason about consistency of the ontology
     * @param extractModule shall the SyntacticLocalityModuleExtractor be used on consistent ontologies?
     * @return a diagnosis model
     * @throws OWLOntologyCreationException´An exception which describes an error during the creation of an ontology.
     */
    public static DiagnosisModel<OWLLogicalAxiom> generateDiagnosisModel(OWLOntology ontology,
                                                                         OWLReasonerFactory reasonerFactory, boolean extractModule, boolean reduceIncoherencyToInconsistency)
            throws OWLOntologyCreationException {

        OWLOntologyManager manager = ontology.getOWLOntologyManager();


        logger.info("------------------------- Diagnosis Model Creation Settings -----------------------------");
        logger.info("Ontology: {}", ontology.getOntologyID());
        logger.info("OWLOntologyManager: {}", manager);
        logger.info("OWLReasonerFactory: {}", reasonerFactory);

        OWLReasoner reasoner = reasonerFactory.createReasoner(ontology);

        logger.info("OWLReasoner: {}", reasoner);
        logger.info("Configuration [extractModules]: {}", extractModule);
        logger.info("Configuration [reduceIncoherency]: {}", reduceIncoherencyToInconsistency);
        logger.info("-----------------------------------------------------------------------------------------");

        Set<OWLLogicalAxiom> possiblyFaulty = new HashSet<>(ontology.getLogicalAxiomCount());
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

        // in case the ontology is consistent we assume that the user wants to debug the incoherency.
        if (reasoner.isConsistent()) {
            reasoner.precomputeInferences(InferenceType.CLASS_HIERARCHY);
            Set<OWLClass> classes = reasoner.getBottomClassNode().getEntities();
            classes.remove(manager.getOWLDataFactory().getOWLNothing());

            if (extractModule && classes.size() > 1) {
                SyntacticLocalityModuleExtractor extractor = new SyntacticLocalityModuleExtractor(manager,
                        ontology, ModuleType.STAR);

                Set<OWLEntity> entities = classes.stream()
                        .map(o -> (OWLEntity) o).collect(Collectors.toSet());

                possiblyFaulty = extractor.extract(entities).stream().filter(OWLLogicalAxiom.class::isInstance).
                        map(o -> (OWLLogicalAxiom) o).collect(Collectors.toSet());

            } else
                possiblyFaulty.addAll(ontology.getLogicalAxioms());

            // instantiate unsat classes thus reducing the incoherency to inconsistency
            if (reduceIncoherencyToInconsistency) {
                for (OWLClass cl : classes) {
                    OWLDataFactory df = manager.getOWLDataFactory();
                    OWLIndividual ind = df.getOWLAnonymousIndividual();
                    dm.getCorrectFormulas().add(df.getOWLClassAssertionAxiom(cl, ind));
                }
            }
        } else
            possiblyFaulty.addAll(ontology.getLogicalAxioms());

        // make sure that all sets are disjoint
        possiblyFaulty.removeAll(dm.getCorrectFormulas());
        possiblyFaulty.removeAll(dm.getEntailedExamples());
        possiblyFaulty.removeAll(dm.getNotEntailedExamples());

        dm.setPossiblyFaultyFormulas(possiblyFaulty);
        reasoner.dispose();

        logger.info("------------------------- Generated Diagnosis Model -------------------------------------");
        logger.info("{} Possibly Faulty Formulas", dm.getPossiblyFaultyFormulas().size());
        logger.info("{} Correct Formulas", dm.getCorrectFormulas().size());
        logger.info("{} Entailed Examples", dm.getEntailedExamples().size());
        logger.info("{} Not-Entailed Examples", dm.getNotEntailedExamples().size());
        logger.info("-----------------------------------------------------------------------------------------");
        return dm;
    }

    /**
     * Sets types of entailements that must be computed by {@link #calculateEntailments()}
     *
     * @param infType entailment type
     */
    public void setEntailmentTypes(InferenceType... infType) {
        this.interenceTypes = infType;
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
            this.reasoner.precomputeInferences(this.interenceTypes);
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
        this.reasoner.dispose();
    }

    public OWLOntology getDebuggingOntology() {
        return debuggingOntology;
    }

    @Override
    public String toString() {
        return "ExquisiteOWLReasoner(" + reasoner + ")";
    }
}
