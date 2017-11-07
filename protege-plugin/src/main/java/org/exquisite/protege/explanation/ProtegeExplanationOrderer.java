package org.exquisite.protege.explanation;

import org.semanticweb.owlapi.model.*;
import uk.ac.manchester.cs.owl.explanation.ordering.EntailedAxiomTree;
import uk.ac.manchester.cs.owl.explanation.ordering.ExplanationOrderer;
import uk.ac.manchester.cs.owl.explanation.ordering.ExplanationTree;
import uk.ac.manchester.cs.owl.explanation.ordering.Tree;

import java.io.Serializable;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;

import static java.util.stream.Collectors.toList;


/**
 * Provides ordering and indenting of explanations based on various ordering
 * heuristics.
 *
 * @author Matthew Horridge, The University Of Manchester, Bio-Health
 *         Informatics Group, Date: 11-Jan-2008
 */
public class ProtegeExplanationOrderer implements ExplanationOrderer {

    private Set<OWLAxiom> currentExplanation;
    private final Map<OWLEntity, Set<OWLAxiom>> lhs2AxiomMap;
    private final Map<OWLAxiom, Set<OWLEntity>> entitiesByAxiomRHS;
    private final SeedExtractor seedExtractor;
    private final OWLOntologyManager man;
    private OWLOntology ont;
    private final Map<OWLObject, Set<OWLAxiom>> mappedAxioms;
    private final Set<OWLAxiom> consumedAxioms;
    private final Set<AxiomType<?>> passTypes;
    private OWLEntity currentSource;
    private OWLEntity currentTarget;

    /**
     * @param m
     *        the manager to use
     */
    public ProtegeExplanationOrderer(OWLOntologyManager m) {
        currentExplanation = Collections.emptySet();
        lhs2AxiomMap = new HashMap<>();
        entitiesByAxiomRHS = new HashMap<>();
        seedExtractor = new SeedExtractor();
        man = m;
        mappedAxioms = new HashMap<>();
        passTypes = new HashSet<>();
        // I'm not sure what to do with disjoint classes yet. At the
        // moment, we just shove them at the end at the top level.
        passTypes.add(AxiomType.DISJOINT_CLASSES);
        consumedAxioms = new HashSet<>();
    }

    private void reset() {
        lhs2AxiomMap.clear();
        entitiesByAxiomRHS.clear();
        consumedAxioms.clear();
    }

    @Override
    public ExplanationTree getOrderedExplanation(OWLAxiom entailment,
                                                 Set<OWLAxiom> axioms) {
        currentExplanation = new HashSet<>(axioms);
        buildIndices();
        ExplanationTree root = new EntailedAxiomTree(entailment);
        currentSource = seedExtractor.getSource(entailment);
        insertChildren(currentSource, root);
        currentTarget = seedExtractor.getTarget(entailment);
        Set<OWLAxiom> axs = root.getUserObjectClosure();
        final Set<OWLAxiom> targetAxioms = new HashSet<>();
        if (currentTarget != null) {
            if (currentTarget.isOWLClass()) {
                targetAxioms.addAll(ont.getAxioms(currentTarget.asOWLClass()));
            }
            if (currentTarget.isOWLObjectProperty()) {
                targetAxioms.addAll(ont.getAxioms(currentTarget
                        .asOWLObjectProperty()));
            }
            if (currentTarget.isOWLDataProperty()) {
                targetAxioms.addAll(ont.getAxioms(currentTarget
                        .asOWLDataProperty()));
            }
            if (currentTarget.isOWLNamedIndividual()) {
                targetAxioms.addAll(ont.getAxioms(currentTarget
                        .asOWLNamedIndividual()));
            }
        }
        List<OWLAxiom> rootAxioms = axioms.stream()
                .filter(ax -> !axs.contains(ax))
                .collect(toList());
        Collections.sort(rootAxioms, new Comparator<OWLAxiom>() {

            @Override
            public int compare(OWLAxiom o1, OWLAxiom o2) {
                if (targetAxioms.contains(o1)) {
                    return 1;
                }
                if (targetAxioms.contains(o2)) {
                    return -1;
                }
                return 0;
            }
        });
        for (OWLAxiom ax : rootAxioms) {
            root.addChild(new ExplanationTree(ax));
        }
        return root;
    }

    private List<OWLEntity> getRHSEntitiesSorted(OWLAxiom ax) {
        Collection<OWLEntity> entities = getRHSEntities(ax);
        List<OWLEntity> sortedEntities = new ArrayList<>(entities);
        Collections.sort(sortedEntities, propertiesFirstComparator);
        return sortedEntities;
    }

    private void insertChildren(OWLEntity entity, ExplanationTree tree) {
        Set<OWLAxiom> currentPath = new HashSet<>(
                tree.getUserObjectPathToRoot());
        Set<? extends OWLAxiom> axioms = Collections.emptySet();
        if (entity != null) {
            if (entity.isOWLClass()) {
                axioms = ont.getAxioms(entity.asOWLClass());
            } else if (entity.isOWLObjectProperty()) {
                axioms = ont.getAxioms(entity.asOWLObjectProperty());
            } else if (entity.isOWLDataProperty()) {
                axioms = ont.getAxioms(entity.asOWLDataProperty());
            } else if (entity.isOWLNamedIndividual()) {
                axioms = ont.getAxioms(entity.asOWLNamedIndividual());
            }
            for (OWLAxiom ax : axioms) {
                if (passTypes.contains(ax.getAxiomType())) {
                    continue;
                }
                Set<OWLAxiom> mapped = getIndexedSet(entity, mappedAxioms, true);
                if (consumedAxioms.contains(ax) || mapped.contains(ax)
                        || currentPath.contains(ax)) {
                    continue;
                }
                mapped.add(ax);
                consumedAxioms.add(ax);
                ExplanationTree child = new ExplanationTree(ax);
                tree.addChild(child);
                for (OWLEntity ent : getRHSEntitiesSorted(ax)) {
                    insertChildren(ent, child);
                }
            }
        }
        sortChildrenAxioms(tree);
    }

    private static Comparator<Tree<OWLAxiom>> comparator = new OWLAxiomTreeComparator();

    private void sortChildrenAxioms(ExplanationTree tree) {
        tree.sortChildren(comparator);
    }

    private static AtomicLong randomstart = new AtomicLong(
            System.currentTimeMillis());

    private void buildIndices() {
        reset();
        AxiomMapBuilder builder = new AxiomMapBuilder();
        for (OWLAxiom ax : currentExplanation) {
            ax.accept(builder);
        }
        try {
            if (ont != null) {
                man.removeOntology(ont);
            }
            ont = man.createOntology(IRI.create("http://www.semanticweb.org/",
                    "ontology" + randomstart.incrementAndGet()));
            List<OWLOntologyChange> changes = new ArrayList<>();
            for (OWLAxiom ax : currentExplanation) {
                changes.add(new AddAxiom(ont, ax));
                ax.accept(builder);
            }
            man.applyChanges(changes);
        } catch (OWLOntologyCreationException e) {
            throw new OWLRuntimeException(e);
        }
    }

    /**
     * A utility method that obtains a set of axioms that are indexed by some
     * object.
     *
     * @param obj
     *        The object that indexed the axioms
     * @param map
     *        The map that provides the index structure
     * @param addIfEmpty
     *        A flag that indicates whether an empty set of axiom should be
     *        added to the index if there is not value present for the indexing
     *        object.
     * @return A set of axioms (may be empty)
     */
    private static <K, E> Set<E> getIndexedSet(K obj, Map<K, Set<E>> map,
                                               boolean addIfEmpty) {
        Set<E> values = map.get(obj);
        if (values == null) {
            values = new HashSet<>();
            if (addIfEmpty) {
                map.put(obj, values);
            }
        }
        return values;
    }

    /**
     * Gets axioms that have a LHS corresponding to the specified entity.
     *
     * @param lhs
     *        The entity that occurs on the left hand side of the axiom.
     * @return A set of axioms that have the specified entity as their left hand
     *         side.
     */
    protected Set<OWLAxiom> getAxiomsForLHS(OWLEntity lhs) {
        return getIndexedSet(lhs, lhs2AxiomMap, true);
    }

    private Collection<OWLEntity> getRHSEntities(OWLAxiom axiom) {
        return getIndexedSet(axiom, entitiesByAxiomRHS, true);
    }

    protected void indexAxiomsByRHSEntities(OWLObject rhs, OWLAxiom axiom) {
        getIndexedSet(axiom, entitiesByAxiomRHS, true).addAll(
                rhs.getSignature());
    }

    private static final class PropertiesFirstComparator implements
            Comparator<OWLObject> {

        public PropertiesFirstComparator() {}

        @Override
        public int compare(OWLObject o1, OWLObject o2) {
            if (o1 instanceof OWLProperty) {
                return -1;
            } else {
                if (o1.equals(o2)) {
                    return 0;
                }
                return 1;
            }
        }
    }

    private static PropertiesFirstComparator propertiesFirstComparator = new PropertiesFirstComparator();

    // ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    // ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    /** tree comparator. */
    private static final class OWLAxiomTreeComparator implements
            Comparator<Tree<OWLAxiom>>, Serializable {

        private static final long serialVersionUID = 30406L;

        public OWLAxiomTreeComparator() {}

        @Override
        public int compare(Tree<OWLAxiom> o1, Tree<OWLAxiom> o2) {
            OWLAxiom ax1 = o1.getUserObject();
            OWLAxiom ax2 = o2.getUserObject();
            // Equivalent classes axioms always come last
            if (ax1 instanceof OWLEquivalentClassesAxiom) {
                return 1;
            }
            if (ax2 instanceof OWLEquivalentClassesAxiom) {
                return -1;
            }
            if (ax1 instanceof OWLPropertyAxiom) {
                return -1;
            }
            int childCount1 = o1.getChildCount();
            childCount1 = childCount1 > 0 ? 0 : 1;
            int childCount2 = o2.getChildCount();
            childCount2 = childCount2 > 0 ? 0 : 1;
            int diff = childCount1 - childCount2;
            if (diff != 0) {
                return diff;
            }
            if (ax1 instanceof OWLSubClassOfAxiom
                    && ax2 instanceof OWLSubClassOfAxiom) {
                OWLSubClassOfAxiom sc1 = (OWLSubClassOfAxiom) ax1;
                OWLSubClassOfAxiom sc2 = (OWLSubClassOfAxiom) ax2;
                return sc1.getSuperClass().compareTo(sc2.getSuperClass());
            }
            return 1;
        }
    }

    @SuppressWarnings("unused")
    private static class SeedExtractor implements OWLAxiomVisitor {

        private OWLEntity source;
        private OWLEntity target;

        public SeedExtractor() {}

        public OWLEntity getSource(OWLAxiom axiom) {
            axiom.accept(this);
            return source;
        }

        public OWLEntity getTarget(OWLAxiom axiom) {
            axiom.accept(this);
            return target;
        }

        @Override
        public void visit(OWLSubClassOfAxiom axiom) {
            if (!axiom.getSubClass().isAnonymous()) {
                source = axiom.getSubClass().asOWLClass();
            }
            if (!axiom.getSuperClass().isOWLNothing()) {
                OWLClassExpression classExpression = axiom.getSuperClass();
                if (!classExpression.isAnonymous()) {
                    target = classExpression.asOWLClass();
                }
            }
        }

        @Override
        public void visit(OWLNegativeObjectPropertyAssertionAxiom axiom) {}

        @Override
        public void visit(OWLAsymmetricObjectPropertyAxiom axiom) {}

        @Override
        public void visit(OWLReflexiveObjectPropertyAxiom axiom) {}

        @Override
        public void visit(OWLDisjointClassesAxiom axiom) {
            for (OWLClassExpression ce : axiom.getClassExpressions()) {
                if (!ce.isAnonymous()) {
                    if (source == null) {
                        source = ce.asOWLClass();
                    } else if (target == null) {
                        target = ce.asOWLClass();
                    } else {
                        break;
                    }
                }
            }
        }

        @Override
        public void visit(OWLDataPropertyDomainAxiom axiom) {}

        public void visit(OWLImportsDeclaration axiom) {}

        @Override
        public void visit(OWLObjectPropertyDomainAxiom axiom) {}

        @Override
        public void visit(OWLEquivalentObjectPropertiesAxiom axiom) {}

        @Override
        public void visit(OWLNegativeDataPropertyAssertionAxiom axiom) {}

        @Override
        public void visit(OWLDifferentIndividualsAxiom axiom) {}

        @Override
        public void visit(OWLDisjointDataPropertiesAxiom axiom) {}

        @Override
        public void visit(OWLDisjointObjectPropertiesAxiom axiom) {}

        @Override
        public void visit(OWLObjectPropertyRangeAxiom axiom) {}

        @Override
        public void visit(OWLObjectPropertyAssertionAxiom axiom) {}

        @Override
        public void visit(OWLFunctionalObjectPropertyAxiom axiom) {}

        @Override
        public void visit(OWLSubObjectPropertyOfAxiom axiom) {
            if (!axiom.getSubProperty().isAnonymous()) {
                source = axiom.getSubProperty().asOWLObjectProperty();
            }
            if (!axiom.getSuperProperty().isAnonymous()) {
                target = axiom.getSuperProperty().asOWLObjectProperty();
            }
        }

        @Override
        public void visit(OWLDisjointUnionAxiom axiom) {}

        @Override
        public void visit(OWLDeclarationAxiom axiom) {}

        @Override
        public void visit(OWLAnnotationAssertionAxiom axiom) {}

        @Override
        public void visit(OWLSymmetricObjectPropertyAxiom axiom) {}

        @Override
        public void visit(OWLDataPropertyRangeAxiom axiom) {}

        @Override
        public void visit(OWLFunctionalDataPropertyAxiom axiom) {}

        @Override
        public void visit(OWLEquivalentDataPropertiesAxiom axiom) {}

        @Override
        public void visit(OWLClassAssertionAxiom axiom) {
            if (!axiom.getClassExpression().isAnonymous()) {
                source = axiom.getIndividual().asOWLNamedIndividual();
                target = axiom.getClassExpression().asOWLClass();
            }
        }

        @Override
        public void visit(OWLEquivalentClassesAxiom axiom) {
            for (OWLClass cls : axiom.getNamedClasses()) {
                if (source == null) {
                    source = cls;
                } else if (target == null) {
                    target = cls;
                } else {
                    break;
                }
            }
        }

        @Override
        public void visit(OWLDataPropertyAssertionAxiom axiom) {}

        @Override
        public void visit(OWLTransitiveObjectPropertyAxiom axiom) {}

        @Override
        public void visit(OWLIrreflexiveObjectPropertyAxiom axiom) {}

        @Override
        public void visit(OWLSubDataPropertyOfAxiom axiom) {}

        @Override
        public void visit(OWLInverseFunctionalObjectPropertyAxiom axiom) {}

        @Override
        public void visit(OWLSameIndividualAxiom axiom) {}

        @Override
        public void visit(OWLSubPropertyChainOfAxiom axiom) {}

        @Override
        public void visit(OWLInverseObjectPropertiesAxiom axiom) {}

        @Override
        public void visit(SWRLRule rule) {}

        @Override
        public void visit(OWLHasKeyAxiom axiom) {}

        @Override
        public void visit(OWLAnnotationPropertyDomainAxiom axiom) {}

        @Override
        public void visit(OWLAnnotationPropertyRangeAxiom axiom) {}

        @Override
        public void visit(OWLSubAnnotationPropertyOfAxiom axiom) {}

        @Override
        public void visit(OWLDatatypeDefinitionAxiom axiom) {}
    }

    // ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    // ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    /** A visitor that indexes axioms by their left and right hand sides. */
    @SuppressWarnings("unused")
    private class AxiomMapBuilder implements OWLAxiomVisitor {

        public AxiomMapBuilder() {}

        @Override
        public void visit(OWLSubClassOfAxiom axiom) {
            if (!axiom.getSubClass().isAnonymous()) {
                getAxiomsForLHS(axiom.getSubClass().asOWLClass()).add(axiom);
                indexAxiomsByRHSEntities(axiom.getSuperClass(), axiom);
            }
        }

        @Override
        public void visit(OWLNegativeObjectPropertyAssertionAxiom axiom) {}

        @Override
        public void visit(OWLAsymmetricObjectPropertyAxiom axiom) {
            if (!axiom.getProperty().isAnonymous()) {
                getAxiomsForLHS(axiom.getProperty().asOWLObjectProperty()).add(
                        axiom);
            }
        }

        @Override
        public void visit(OWLReflexiveObjectPropertyAxiom axiom) {
            if (!axiom.getProperty().isAnonymous()) {
                getAxiomsForLHS(axiom.getProperty().asOWLObjectProperty()).add(
                        axiom);
            }
        }

        @Override
        public void visit(OWLDisjointClassesAxiom axiom) {
            for (OWLClassExpression desc : axiom.getClassExpressions()) {
                if (!desc.isAnonymous()) {
                    getAxiomsForLHS(desc.asOWLClass()).add(axiom);
                }
                indexAxiomsByRHSEntities(desc, axiom);
            }
        }

        @Override
        public void visit(OWLDataPropertyDomainAxiom axiom) {
            getAxiomsForLHS(axiom.getProperty().asOWLDataProperty()).add(axiom);
            indexAxiomsByRHSEntities(axiom.getDomain(), axiom);
        }

        public void visit(OWLImportsDeclaration axiom) {}

        @Override
        public void visit(OWLObjectPropertyDomainAxiom axiom) {
            if (!axiom.getProperty().isAnonymous()) {
                getAxiomsForLHS(axiom.getProperty().asOWLObjectProperty()).add(
                        axiom);
            }
            indexAxiomsByRHSEntities(axiom.getDomain(), axiom);
        }

        @Override
        public void visit(OWLEquivalentObjectPropertiesAxiom axiom) {
            for (OWLObjectPropertyExpression prop : axiom.getProperties()) {
                if (!prop.isAnonymous()) {
                    getAxiomsForLHS(prop.asOWLObjectProperty()).add(axiom);
                }
                indexAxiomsByRHSEntities(prop, axiom);
            }
        }

        @Override
        public void visit(OWLNegativeDataPropertyAssertionAxiom axiom) {}

        @Override
        public void visit(OWLDifferentIndividualsAxiom axiom) {
            for (OWLIndividual ind : axiom.getIndividuals()) {
                if (!ind.isAnonymous()) {
                    getAxiomsForLHS(ind.asOWLNamedIndividual()).add(axiom);
                    indexAxiomsByRHSEntities(ind, axiom);
                }
            }
        }

        @Override
        public void visit(OWLDisjointDataPropertiesAxiom axiom) {
            for (OWLDataPropertyExpression prop : axiom.getProperties()) {
                getAxiomsForLHS(prop.asOWLDataProperty()).add(axiom);
                indexAxiomsByRHSEntities(prop, axiom);
            }
        }

        @Override
        public void visit(OWLDisjointObjectPropertiesAxiom axiom) {
            for (OWLObjectPropertyExpression prop : axiom.getProperties()) {
                if (!prop.isAnonymous()) {
                    getAxiomsForLHS(prop.asOWLObjectProperty()).add(axiom);
                }
                indexAxiomsByRHSEntities(prop, axiom);
            }
        }

        @Override
        public void visit(OWLObjectPropertyRangeAxiom axiom) {
            if (!axiom.getProperty().isAnonymous()) {
                getAxiomsForLHS(axiom.getProperty().asOWLObjectProperty()).add(
                        axiom);
            }
            indexAxiomsByRHSEntities(axiom.getRange(), axiom);
        }

        @Override
        public void visit(OWLObjectPropertyAssertionAxiom axiom) {}

        @Override
        public void visit(OWLFunctionalObjectPropertyAxiom axiom) {
            if (!axiom.getProperty().isAnonymous()) {
                getAxiomsForLHS(axiom.getProperty().asOWLObjectProperty()).add(
                        axiom);
            }
        }

        @Override
        public void visit(OWLSubObjectPropertyOfAxiom axiom) {
            if (!axiom.getSubProperty().isAnonymous()) {
                getAxiomsForLHS(axiom.getSubProperty().asOWLObjectProperty())
                        .add(axiom);
            }
            indexAxiomsByRHSEntities(axiom.getSuperProperty(), axiom);
        }

        @Override
        public void visit(OWLDisjointUnionAxiom axiom) {
            getAxiomsForLHS(axiom.getOWLClass()).add(axiom);
        }

        @Override
        public void visit(OWLDeclarationAxiom axiom) {}

        @Override
        public void visit(OWLAnnotationAssertionAxiom axiom) {}

        @Override
        public void visit(OWLSymmetricObjectPropertyAxiom axiom) {
            if (!axiom.getProperty().isAnonymous()) {
                getAxiomsForLHS(axiom.getProperty().asOWLObjectProperty()).add(
                        axiom);
            }
        }

        @Override
        public void visit(OWLDataPropertyRangeAxiom axiom) {
            if (!axiom.getProperty().isAnonymous()) {
                getAxiomsForLHS(axiom.getProperty().asOWLDataProperty()).add(
                        axiom);
            }
            indexAxiomsByRHSEntities(axiom.getRange(), axiom);
        }

        @Override
        public void visit(OWLFunctionalDataPropertyAxiom axiom) {
            if (!axiom.getProperty().isAnonymous()) {
                getAxiomsForLHS(axiom.getProperty().asOWLDataProperty()).add(
                        axiom);
            }
        }

        @Override
        public void visit(OWLEquivalentDataPropertiesAxiom axiom) {
            for (OWLDataPropertyExpression prop : axiom.getProperties()) {
                getAxiomsForLHS(prop.asOWLDataProperty()).add(axiom);
                indexAxiomsByRHSEntities(prop, axiom);
            }
        }

        @Override
        public void visit(OWLClassAssertionAxiom axiom) {
            if (!axiom.getIndividual().isAnonymous()) {
                getAxiomsForLHS(axiom.getIndividual().asOWLNamedIndividual())
                        .add(axiom);
                indexAxiomsByRHSEntities(axiom.getClassExpression(), axiom);
            }
        }

        @Override
        public void visit(OWLEquivalentClassesAxiom axiom) {
            for (OWLClassExpression desc : axiom.getClassExpressions()) {
                if (!desc.isAnonymous()) {
                    getAxiomsForLHS(desc.asOWLClass()).add(axiom);
                }
                indexAxiomsByRHSEntities(desc, axiom);
            }
        }

        @Override
        public void visit(OWLDataPropertyAssertionAxiom axiom) {
            indexAxiomsByRHSEntities(axiom.getSubject(), axiom);
        }

        @Override
        public void visit(OWLTransitiveObjectPropertyAxiom axiom) {
            if (!axiom.getProperty().isAnonymous()) {
                getAxiomsForLHS(axiom.getProperty().asOWLObjectProperty()).add(
                        axiom);
            }
        }

        @Override
        public void visit(OWLIrreflexiveObjectPropertyAxiom axiom) {
            if (!axiom.getProperty().isAnonymous()) {
                getAxiomsForLHS(axiom.getProperty().asOWLObjectProperty()).add(
                        axiom);
            }
        }

        @Override
        public void visit(OWLSubDataPropertyOfAxiom axiom) {
            getAxiomsForLHS(axiom.getSubProperty().asOWLDataProperty()).add(
                    axiom);
            indexAxiomsByRHSEntities(axiom.getSuperProperty(), axiom);
        }

        @Override
        public void visit(OWLInverseFunctionalObjectPropertyAxiom axiom) {
            if (!axiom.getProperty().isAnonymous()) {
                getAxiomsForLHS(axiom.getProperty().asOWLObjectProperty()).add(
                        axiom);
            }
        }

        @Override
        public void visit(OWLSameIndividualAxiom axiom) {
            for (OWLIndividual ind : axiom.getIndividuals()) {
                if (!ind.isAnonymous()) {
                    getAxiomsForLHS(ind.asOWLNamedIndividual()).add(axiom);
                    indexAxiomsByRHSEntities(ind, axiom);
                }
            }
        }

        @Override
        public void visit(OWLSubPropertyChainOfAxiom axiom) {}

        @Override
        public void visit(OWLDatatypeDefinitionAxiom axiom) {}

        @Override
        public void visit(OWLInverseObjectPropertiesAxiom axiom) {
            if (!axiom.getFirstProperty().isAnonymous()) {
                getAxiomsForLHS(axiom.getFirstProperty().asOWLObjectProperty())
                        .add(axiom);
            }
            indexAxiomsByRHSEntities(axiom.getFirstProperty(), axiom);
            indexAxiomsByRHSEntities(axiom.getSecondProperty(), axiom);
        }

        @Override
        public void visit(SWRLRule rule) {}

        @Override
        public void visit(OWLHasKeyAxiom axiom) {
            if (!axiom.getClassExpression().isAnonymous()) {
                indexAxiomsByRHSEntities(axiom.getClassExpression()
                        .asOWLClass(), axiom);
            }
        }

        @Override
        public void visit(OWLAnnotationPropertyDomainAxiom axiom) {}

        @Override
        public void visit(OWLAnnotationPropertyRangeAxiom axiom) {}

        @Override
        public void visit(OWLSubAnnotationPropertyOfAxiom axiom) {}
    }
}