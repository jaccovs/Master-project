package org.exquisite.core.parser;

import org.exquisite.core.costestimators.OWLAxiomKeywordCostsEstimator;
import org.exquisite.core.utils.OWLUtils;
import org.semanticweb.owlapi.manchestersyntax.parser.ManchesterOWLSyntax;
import org.semanticweb.owlapi.model.*;

import javax.annotation.Nonnull;
import java.util.*;

/**
 * <p>
 *     A visitor that parses an instance of type OWLAxiom (by calling OWLAxiom.accept(OWLAxiomKeywordCounter)) and counts
 *     the occurrences of keywords from the Manchester Syntax (MS).
 * </p>
 * <p>
 *     Since the instances of OWLAxiom are expressed in a Functional-Style Syntax (FSS) this class is responsible to find
 *     the appropriate FSS style objects and count the occurrence(s) of the appropriate MS keywords.
 * </p>
 * <p>
 *     A mapping between Functional-Style owl-api and Manchester Syntax keywords is implemented in this visitor pattern
 *     according to the
 *     <a href="https://www.w3.org/TR/2012/NOTE-owl2-manchester-syntax-20121211/">OWL 2 Web Ontology Language Manchester Syntax (Second Edition) Specification</a>
 * </p>
 * <p>
 *     The OWLAxiom traversal implementation is a copy of {{@link uk.ac.manchester.cs.owl.owlapi.AbstractEntityRegistrationManager}}
 * </p>
 * <p>
 *     The keywords are only counted when they do occur in the keywords list of class {@link OWLAxiomKeywordCostsEstimator}
 * </p>
 * <p>
 *     An overview of the mapping from Manchester Syntax keywords to Functional-Style OWL-API axiom classes.
 *     <table summary="Mapping between Manchester Syntax and Funtional-Style OWL-Api Classes" border="1">
 *         <tr><th>Manchester-Syntax</th><th>Functional-Style Syntax</th></tr>
 *         <tr><td>ManchesterOWLSyntax.TYPE</td><td>OWLClassAssertionAxiom</td></tr>
 *         <tr><td>ManchesterOWLSyntax.SAME_AS</td><td>OWLSameIndividualAxiom</td></tr>
 *         <tr><td>ManchesterOWLSyntax.SAME_INDIVIDUAL</td><td>OWLSameIndividualAxiom</td></tr>
 *         <tr><td>ManchesterOWLSyntax.DIFFERENT_FROM</td><td>OWLDifferentIndividualsAxiom</td></tr>
 *         <tr><td>ManchesterOWLSyntax.DIFFERENT_INDIVIDUALS</td><td>OWLDifferentIndividualsAxiom</td></tr>
 *         <tr><td>ManchesterOWLSyntax.SUBCLASS_OF</td><td>OWLSubClassOfAxiom</td></tr>
 *         <tr><td>ManchesterOWLSyntax.DISJOINT_WITH</td><td>OWLDisjointClassesAxiom, OWLDisjointObjectPropertiesAxiom, OWLDisjointDataPropertiesAxiom</td></tr>
 *         <tr><td>ManchesterOWLSyntax.DISJOINT_CLASSES</td><td>OWLDisjointClassesAxiom</td></tr>
 *         <tr><td>ManchesterOWLSyntax.DISJOINT_PROPERTIES</td><td>OWLDisjointObjectPropertiesAxiom, OWLDisjointDataPropertiesAxiom</td></tr>
 *         <tr><td>ManchesterOWLSyntax.DISJOINT_UNION_OF</td><td>OWLDisjointUnionAxiom</td></tr>
 *         <tr><td>ManchesterOWLSyntax.EQUIVALENT_TO</td><td>OWLEquivalentClassesAxiom, OWLEquivalentObjectPropertiesAxiom, OWLEquivalentDataPropertiesAxiom, OWLDatatypeDefinitionAxiom</td></tr>
 *         <tr><td>ManchesterOWLSyntax.EQUIVALENT_CLASSES</td><td>OWLEquivalentClassesAxiom</td></tr>
 *         <tr><td>ManchesterOWLSyntax.EQUIVALENT_PROPERTIES</td><td>OWLEquivalentObjectPropertiesAxiom</td></tr>
 *         <tr><td>ManchesterOWLSyntax.AND</td><td>OWLObjectIntersectionOf, OWLDataIntersectionOf</td></tr>
 *         <tr><td>ManchesterOWLSyntax.OR</td><td>OWLObjectUnionOf, OWLDataUnionOf</td></tr>
 *         <tr><td>ManchesterOWLSyntax.NOT</td><td>OWLObjectComplementOf, OWLDataComplementOf, OWLNegativeObjectPropertyAssertionAxiom, OWLNegativeDataPropertyAssertionAxiom</td></tr>
 *         <tr><td>ManchesterOWLSyntax.SOME</td><td>OWLObjectSomeValuesFrom, OWLDataSomeValuesFrom</td></tr>
 *         <tr><td>ManchesterOWLSyntax.ONLY</td><td>OWLObjectAllValuesFrom, OWLDataAllValuesFrom</td></tr>
 *         <tr><td>ManchesterOWLSyntax.MIN</td><td>OWLObjectMinCardinality, OWLDataMinCardinality</td></tr>
 *         <tr><td>ManchesterOWLSyntax.MAX</td><td>OWLObjectMaxCardinality, OWLDataMaxCardinality</td></tr>
 *         <tr><td>ManchesterOWLSyntax.SELF</td><td>OWLObjectHasSelf</td></tr>
 *         <tr><td>ManchesterOWLSyntax.EXACTLY</td><td>OWLObjectExactCardinality, OWLDataExactCardinality</td></tr>
 *         <tr><td>ManchesterOWLSyntax.VALUE</td><td>OWLObjectHasValue, OWLDataHasValue</td></tr>
 *         <tr><td>ManchesterOWLSyntax.INVERSE</td><td>OWLInverseObjectPropertiesAxiom</td></tr>
 *         <tr><td>ManchesterOWLSyntax.INVERSE_OF</td><td>OWLInverseObjectPropertiesAxiom</td></tr>
 *         <tr style="text-decoration: line-through"><td>ManchesterOWLSyntax.ONE_OF_DELIMETER</td><td>OWLObjectOneOf, OWLDataOneOf</td></tr>
 *         <tr><td>ManchesterOWLSyntax.THAT</td><td>OWLObjectIntersectionOf</td></tr>
 *         <tr><td>ManchesterOWLSyntax.HAS_KEY</td><td>OWLHasKeyAxiom</td></tr>
 *         <tr><td>ManchesterOWLSyntax.DOMAIN</td><td>OWLObjectPropertyDomainAxiom, OWLDataPropertyDomainAxiom</td></tr>
 *         <tr><td>ManchesterOWLSyntax.RANGE</td><td>OWLObjectPropertyRangeAxiom, OWLDataPropertyRangeAxiom</td></tr>
 *         <tr><td>ManchesterOWLSyntax.FUNCTIONAL</td><td>OWLFunctionalObjectPropertyAxiom, OWLFunctionalDataPropertyAxiom</td></tr>
 *         <tr><td>ManchesterOWLSyntax.INVERSE_FUNCTIONAL</td><td>OWLInverseFunctionalObjectPropertyAxiom</td></tr>
 *         <tr><td>ManchesterOWLSyntax.REFLEXIVE</td><td>OWLReflexiveObjectPropertyAxiom</td></tr>
 *         <tr><td>ManchesterOWLSyntax.IRREFLEXIVE</td><td>OWLIrreflexiveObjectPropertyAxiom</td></tr>
 *         <tr><td>ManchesterOWLSyntax.SYMMETRIC</td><td>OWLSymmetricObjectPropertyAxiom</td></tr>
 *         <tr><td>ManchesterOWLSyntax.ASYMMETRIC</td><td>OWLAsymmetricObjectPropertyAxiom</td></tr>
 *         <tr><td>ManchesterOWLSyntax.TRANSITIVE</td><td>OWLTransitiveObjectPropertyAxiom</td></tr>
 *         <tr><td>ManchesterOWLSyntax.SUB_PROPERTY_OF</td><td>OWLSubObjectPropertyOfAxiom, OWLSubDataPropertyOfAxiom</td></tr>
 *         <tr style="text-decoration: line-through"><td>ManchesterOWLSyntax.SUB_PROPERTY_CHAIN</td><td>OWLSubPropertyChainOfAxiom</td></tr>
 *     </table>
 * </p>
 * @author wolfi
 */
public class OWLAxiomKeywordCounter implements Iterable<ManchesterOWLSyntax>, OWLObjectVisitor {

    /**
     * Mapping between axiom and Manchester Syntax keyword occurrences.
     */
    private Map<ManchesterOWLSyntax,Integer> _map;

    /**
     * A copied list of OWLAxiomKeywordCostsEstimator.keywords to check the relevance of a keyword occurrence.
     */
    private List<ManchesterOWLSyntax> _keywords;

    /**
     * Constructor.
     */
    public OWLAxiomKeywordCounter() {
        _map = new HashMap<>();
        _keywords = Arrays.asList(OWLAxiomKeywordCostsEstimator.keywords);
    }

    /**
     * Returns the number of occurrences of the Manchester Syntax keyword within the parsed axiom
     * (to be parsed with OWLAxiom.accept(OWLAxiomKeywordCounter).
     * @param keyword The Manchester Syntax keyword.
     * @return the number of occurrences of the keyword within an OWLAxiom.
     */
    public Integer getOccurrences(ManchesterOWLSyntax keyword) {
        Integer count = _map.get(keyword);
        if (count == null) return 0;
        return count;
    }

    @Override
    public Iterator<ManchesterOWLSyntax> iterator() {
        return getKeywords();
    }

    /**
     * Returns the list of matched keywords found in the parsed axiom.
     *
     * @return The list of matched keywords found in the parsed axiom.
     */
    public Iterator<ManchesterOWLSyntax> getKeywords() {
        return _map.keySet().iterator();
    }

    private void increment(final OWLObject axiom, Collection<? extends OWLObject> owlObjects, final ManchesterOWLSyntax... keywordCandidates) {
        increment(axiom, owlObjects, 1, keywordCandidates);
    }

    /**
     * Increments the occurrence of one of the (Manchester Syntax) keyword candidates by inc (if keyword is registered).
     *
     * @param object An owl object to be parsed.
     * @param owlObjects The objects the keyword candidates is connecting.
     * @param inc The increment.
     * @param keywordCandidates A list of keyword candidates for the current owl object.
     */
    private void increment(final OWLObject object, Collection<? extends OWLObject> owlObjects, final int inc, final ManchesterOWLSyntax... keywordCandidates) {
        if (inc < 1) throw new IllegalArgumentException("Parameter inc must be a positive number");

        // We MUST NOT use object.toString() since this method might generate different syntaxes as the expected
        // Manchester Syntax (e.g. a Functional Style syntax)+
        // In the second half of this method we heavily depend on a Manchester Syntax string representation of the OWLObject.
        String axiomAsManchesterSyntaxString = OWLUtils.getManchesterSyntaxString(object);

        for (OWLObject owlObject : owlObjects) {
            final String owlObjectString = OWLUtils.getManchesterSyntaxString(owlObject);
            int idx = axiomAsManchesterSyntaxString.indexOf(owlObjectString);
            if (idx != -1) // replaces the first occurrence of a matching owlObjectString with ""
                axiomAsManchesterSyntaxString = axiomAsManchesterSyntaxString.substring(0, idx) + axiomAsManchesterSyntaxString.substring(idx+owlObjectString.length());
        }

        axiomAsManchesterSyntaxString = axiomAsManchesterSyntaxString.trim();

        boolean found = false;
        // first check for equality
        for (int i = 0; i < keywordCandidates.length && !found; i++) {
            ManchesterOWLSyntax keyword = keywordCandidates[i];
            final StringTokenizer st = new StringTokenizer(axiomAsManchesterSyntaxString);
            final String keywordString = keyword.toString();
            while (st.hasMoreTokens() && !found) {
                if (found = st.nextToken().equals(keywordString)) {
                    increment(keyword, inc);
                }
            }
        }
        if (!found) { // if yet no matching keyword is found, search for starting string matches

            // sort the list by descending length to guarantee to increment the longest matching keyword (e.g. inverseOf before inverse)
            Arrays.sort(keywordCandidates, (o1, o2) -> o2.keyword().length() - o1.keyword().length());

            for (int i = 0; i < keywordCandidates.length && !found; i++) {
                ManchesterOWLSyntax keyword = keywordCandidates[i];
                final StringTokenizer st = new StringTokenizer(axiomAsManchesterSyntaxString);
                final String keywordString = keyword.toString();
                while (st.hasMoreTokens() && !found) {
                    if (found = st.nextToken().startsWith(keywordString)) {
                        increment(keyword, inc);
                    }
                }
            }
        }
    }

    /**
     * Increments the occurrence of a (Manchester Syntax) keyword by one (if keyword is registered).
     *
     * @param keyword The Manchester Syntax keyword.
     */
    private void increment(ManchesterOWLSyntax keyword) {
        increment(keyword, 1);
    }

    /**
     * Increments the occurrence of a (Manchester Syntax) keyword by inc (if keyword is registered).
     *
     * @param keyword The Manchester Syntax keyword.
     */
    private void increment(final ManchesterOWLSyntax keyword, final int inc) {
        if (inc < 1) throw new IllegalArgumentException("Parameter inc must be a positive number");

        // assert that the keyword is in the list of the keywords in OWLAxiomKeywordCostsEstimator
        if (_keywords.contains(keyword)) {
            Integer i = _map.get(keyword);
            if (i == null) _map.put(keyword, inc);
            else _map.put(keyword, i + inc);
        }
    }

    // OWLAxiomVisitor

    @Override
    public void visit(@Nonnull OWLDeclarationAxiom axiom) {
        axiom.getEntity().accept(this);
    }

    @Override
    public void visit(@Nonnull OWLDatatypeDefinitionAxiom axiom) {
        increment(ManchesterOWLSyntax.EQUIVALENT_TO);
        axiom.getDatatype().accept(this);
        axiom.getDataRange().accept(this);
    }

    @Override
    public void visit(@Nonnull OWLSubClassOfAxiom axiom) {
        increment(ManchesterOWLSyntax.SUBCLASS_OF);
        axiom.getSubClass().accept(this);
        axiom.getSuperClass().accept(this);
    }

    @Override
    public void visit(@Nonnull OWLNegativeObjectPropertyAssertionAxiom axiom) {
        increment(ManchesterOWLSyntax.NOT);
        axiom.getSubject().accept(this);
        axiom.getProperty().accept(this);
        axiom.getObject().accept(this);
    }

    @Override
    public void visit(@Nonnull OWLAsymmetricObjectPropertyAxiom axiom) {
        increment(ManchesterOWLSyntax.ASYMMETRIC);
        axiom.getProperty().accept(this);
    }

    @Override
    public void visit(@Nonnull OWLReflexiveObjectPropertyAxiom axiom) {
        increment(ManchesterOWLSyntax.REFLEXIVE);
        axiom.getProperty().accept(this);
    }

    @Override
    public void visit(@Nonnull OWLDisjointClassesAxiom axiom) {
        increment(axiom, axiom.getClassExpressions(), ManchesterOWLSyntax.DISJOINT_CLASSES, ManchesterOWLSyntax.DISJOINT_WITH);
        for (OWLClassExpression desc : axiom.getClassExpressions()) {
            desc.accept(this);
        }
    }

    @Override
    public void visit(@Nonnull OWLDataPropertyDomainAxiom axiom) {
        increment(ManchesterOWLSyntax.DOMAIN);
        axiom.getDomain().accept(this);
        axiom.getProperty().accept(this);
    }

    @Override
    public void visit(@Nonnull OWLObjectPropertyDomainAxiom axiom) {
        increment(ManchesterOWLSyntax.DOMAIN);
        axiom.getDomain().accept(this);
        axiom.getProperty().accept(this);
    }

    @Override
    public void visit(@Nonnull OWLEquivalentObjectPropertiesAxiom axiom) {
        increment(axiom, axiom.getProperties(), ManchesterOWLSyntax.EQUIVALENT_TO, ManchesterOWLSyntax.EQUIVALENT_PROPERTIES);
        for (OWLObjectPropertyExpression prop : axiom.getProperties()) {
            prop.accept(this);
        }
    }

    @Override
    public void visit(@Nonnull OWLNegativeDataPropertyAssertionAxiom axiom) {
        increment(ManchesterOWLSyntax.NOT);
        axiom.getSubject().accept(this);
        axiom.getProperty().accept(this);
        axiom.getObject().accept(this);
    }

    @Override
    public void visit(@Nonnull OWLDifferentIndividualsAxiom axiom) {
        increment(axiom, axiom.getIndividuals(), ManchesterOWLSyntax.DIFFERENT_FROM, ManchesterOWLSyntax.DIFFERENT_INDIVIDUALS);
        for (OWLIndividual ind : axiom.getIndividuals()) {
            ind.accept(this);
        }
    }

    @Override
    public void visit(@Nonnull OWLDisjointDataPropertiesAxiom axiom) {
        increment(axiom, axiom.getProperties(), ManchesterOWLSyntax.DISJOINT_WITH, ManchesterOWLSyntax.DISJOINT_PROPERTIES);
        for (OWLDataPropertyExpression prop : axiom.getProperties()) {
            prop.accept(this);
        }
    }

    @Override
    public void visit(@Nonnull OWLDisjointObjectPropertiesAxiom axiom) {
        increment(axiom, axiom.getProperties(), ManchesterOWLSyntax.DISJOINT_WITH, ManchesterOWLSyntax.DISJOINT_PROPERTIES);
        for (OWLObjectPropertyExpression prop : axiom.getProperties()) {
            prop.accept(this);
        }
    }

    @Override
    public void visit(@Nonnull OWLObjectPropertyRangeAxiom axiom) {
        increment(ManchesterOWLSyntax.RANGE);
        axiom.getRange().accept(this);
        axiom.getProperty().accept(this);
    }

    @Override
    public void visit(@Nonnull OWLObjectPropertyAssertionAxiom axiom) {
        axiom.getSubject().accept(this);
        axiom.getProperty().accept(this);
        axiom.getObject().accept(this);
    }

    @Override
    public void visit(@Nonnull OWLFunctionalObjectPropertyAxiom axiom) {
        increment(ManchesterOWLSyntax.FUNCTIONAL);
        axiom.getProperty().accept(this);
    }

    @Override
    public void visit(@Nonnull OWLSubObjectPropertyOfAxiom axiom) {
        increment(ManchesterOWLSyntax.SUB_PROPERTY_OF);
        axiom.getSubProperty().accept(this);
        axiom.getSuperProperty().accept(this);
    }

    @Override
    public void visit(@Nonnull OWLDisjointUnionAxiom axiom) {
        increment(ManchesterOWLSyntax.DISJOINT_UNION_OF);
        axiom.getOWLClass().accept((OWLEntityVisitor) this);
        for (OWLClassExpression desc : axiom.getClassExpressions()) {
            desc.accept(this);
        }
    }

    @Override
    public void visit(@Nonnull OWLSymmetricObjectPropertyAxiom axiom) {
        increment(ManchesterOWLSyntax.SYMMETRIC);
        axiom.getProperty().accept(this);
    }

    @Override
    public void visit(@Nonnull OWLDataPropertyRangeAxiom axiom) {
        increment(ManchesterOWLSyntax.RANGE);
        axiom.getProperty().accept(this);
        axiom.getRange().accept(this);
    }

    @Override
    public void visit(@Nonnull OWLFunctionalDataPropertyAxiom axiom) {
        increment(ManchesterOWLSyntax.FUNCTIONAL);
        axiom.getProperty().accept(this);
    }

    @Override
    public void visit(@Nonnull OWLEquivalentDataPropertiesAxiom axiom) {
        increment(axiom, axiom.getProperties(), ManchesterOWLSyntax.EQUIVALENT_TO, ManchesterOWLSyntax.EQUIVALENT_PROPERTIES);
        for (OWLDataPropertyExpression prop : axiom.getProperties()) {
            prop.accept(this);
        }
    }

    @Override
    public void visit(@Nonnull OWLClassAssertionAxiom axiom) {
        increment(ManchesterOWLSyntax.TYPE);
        axiom.getClassExpression().accept(this);
        axiom.getIndividual().accept(this);
    }

    @Override
    public void visit(@Nonnull OWLEquivalentClassesAxiom axiom) {
        increment(axiom, axiom.getClassExpressions(), ManchesterOWLSyntax.EQUIVALENT_TO, ManchesterOWLSyntax.EQUIVALENT_CLASSES);
        for (OWLClassExpression desc : axiom.getClassExpressions()) {
            desc.accept(this);
        }
    }

    @Override
    public void visit(@Nonnull OWLDataPropertyAssertionAxiom axiom) {
        axiom.getSubject().accept(this);
        axiom.getProperty().accept(this);
        axiom.getObject().accept(this);
    }

    @Override
    public void visit(@Nonnull OWLTransitiveObjectPropertyAxiom axiom) {
        increment(ManchesterOWLSyntax.TRANSITIVE);
        axiom.getProperty().accept(this);
    }

    @Override
    public void visit(@Nonnull OWLIrreflexiveObjectPropertyAxiom axiom) {
        increment(ManchesterOWLSyntax.IRREFLEXIVE);
        axiom.getProperty().accept(this);
    }

    @Override
    public void visit(@Nonnull OWLSubDataPropertyOfAxiom axiom) {
        increment(ManchesterOWLSyntax.SUB_PROPERTY_OF);
        axiom.getSubProperty().accept(this);
        axiom.getSuperProperty().accept(this);
    }

    @Override
    public void visit(@Nonnull OWLInverseFunctionalObjectPropertyAxiom axiom) {
        increment(ManchesterOWLSyntax.INVERSE_FUNCTIONAL);
        axiom.getProperty().accept(this);
    }

    @Override
    public void visit(@Nonnull OWLSameIndividualAxiom axiom) {
        increment(axiom, axiom.getIndividuals(), ManchesterOWLSyntax.SAME_AS, ManchesterOWLSyntax.SAME_INDIVIDUAL);
        for (OWLIndividual ind : axiom.getIndividuals()) {
            ind.accept(this);
        }
    }

    @Override
    public void visit(@Nonnull OWLSubPropertyChainOfAxiom axiom) {
        increment(ManchesterOWLSyntax.SUB_PROPERTY_CHAIN);
        for (OWLObjectPropertyExpression prop : axiom.getPropertyChain()) {
            prop.accept(this);
        }
        axiom.getSuperProperty().accept(this);
    }

    @Override
    public void visit(@Nonnull OWLInverseObjectPropertiesAxiom axiom) {
        final Set<OWLObjectPropertyExpression> owlObjects = new HashSet<>();
        owlObjects.add(axiom.getFirstProperty());
        owlObjects.add(axiom.getSecondProperty());

        increment(axiom, owlObjects, ManchesterOWLSyntax.INVERSE, ManchesterOWLSyntax.INVERSE_OF);

        axiom.getFirstProperty().accept(this);
        axiom.getSecondProperty().accept(this);
    }

    @Override
    public void visit(@Nonnull OWLHasKeyAxiom axiom) {
        increment(ManchesterOWLSyntax.HAS_KEY);
        axiom.getClassExpression().accept(this);
        for (OWLPropertyExpression prop : axiom.getPropertyExpressions()) {
            prop.accept(this);
        }
    }

    @Override
    public void visit(@Nonnull SWRLRule swrlRule) {

    }

    @Override
    public void visit(@Nonnull SWRLClassAtom swrlClassAtom) {

    }

    @Override
    public void visit(@Nonnull SWRLDataRangeAtom swrlDataRangeAtom) {

    }

    @Override
    public void visit(@Nonnull SWRLObjectPropertyAtom swrlObjectPropertyAtom) {

    }

    @Override
    public void visit(@Nonnull SWRLDataPropertyAtom swrlDataPropertyAtom) {

    }

    @Override
    public void visit(@Nonnull SWRLBuiltInAtom swrlBuiltInAtom) {

    }

    @Override
    public void visit(@Nonnull SWRLVariable swrlVariable) {

    }

    @Override
    public void visit(@Nonnull SWRLIndividualArgument swrlIndividualArgument) {

    }

    @Override
    public void visit(@Nonnull SWRLLiteralArgument swrlLiteralArgument) {

    }

    @Override
    public void visit(@Nonnull SWRLSameIndividualAtom swrlSameIndividualAtom) {

    }

    @Override
    public void visit(@Nonnull SWRLDifferentIndividualsAtom swrlDifferentIndividualsAtom) {

    }

    // OWLClassExpressionVisitor

    @Override
    public void visit(@Nonnull OWLObjectIntersectionOf ce) {
        final int size = ce.getOperands().size() >= 2 ? ce.getOperands().size() - 1 : 1;
        increment(ce, ce.getOperands(), size, ManchesterOWLSyntax.AND, ManchesterOWLSyntax.THAT);
        for (OWLClassExpression operand : ce.getOperands()) {
            operand.accept(this);
        }
    }

    @Override
    public void visit(@Nonnull OWLObjectUnionOf ce) {
        final int size = ce.getOperands().size() >= 2 ? ce.getOperands().size() - 1 : 1;
        increment(ManchesterOWLSyntax.OR, size);
        for (OWLClassExpression operand : ce.getOperands()) {
            operand.accept(this);
        }
    }

    @Override
    public void visit(@Nonnull OWLObjectComplementOf ce) {
        increment(ManchesterOWLSyntax.NOT);
        ce.getOperand().accept(this);
    }

    @Override
    public void visit(@Nonnull OWLObjectSomeValuesFrom ce) {
        increment(ManchesterOWLSyntax.SOME);
        ce.getProperty().accept(this);
        ce.getFiller().accept(this);
    }

    @Override
    public void visit(@Nonnull OWLObjectAllValuesFrom ce) {
        increment(ManchesterOWLSyntax.ONLY);
        ce.getProperty().accept(this);
        ce.getFiller().accept(this);
    }

    @Override
    public void visit(@Nonnull OWLObjectHasValue ce) {
        increment(ManchesterOWLSyntax.VALUE);
        ce.getProperty().accept(this);
        ce.getFiller().accept(this);
    }

    @Override
    public void visit(@Nonnull OWLObjectMinCardinality ce) {
        increment(ManchesterOWLSyntax.MIN);
        ce.getProperty().accept(this);
        ce.getFiller().accept(this);
    }

    @Override
    public void visit(@Nonnull OWLObjectExactCardinality ce) {
        increment(ManchesterOWLSyntax.EXACTLY);
        ce.getProperty().accept(this);
        ce.getFiller().accept(this);
    }

    @Override
    public void visit(@Nonnull OWLObjectMaxCardinality ce) {
        increment(ManchesterOWLSyntax.MAX);
        ce.getProperty().accept(this);
        ce.getFiller().accept(this);
    }

    @Override
    public void visit(@Nonnull OWLObjectHasSelf ce) {
        increment(ManchesterOWLSyntax.SELF);
        ce.getProperty().accept(this);
    }

    @Override
    public void visit(@Nonnull OWLObjectOneOf ce) {
        increment(ManchesterOWLSyntax.ONE_OF_DELIMETER);
        for (OWLIndividual ind : ce.getIndividuals()) {
            ind.accept(this);
        }
    }

    @Override
    public void visit(@Nonnull OWLDataSomeValuesFrom ce) {
        increment(ManchesterOWLSyntax.SOME);
        ce.getProperty().accept(this);
        ce.getFiller().accept(this);
    }

    @Override
    public void visit(@Nonnull OWLDataAllValuesFrom ce) {
        increment(ManchesterOWLSyntax.ONLY);
        ce.getProperty().accept(this);
        ce.getFiller().accept(this);
    }

    @Override
    public void visit(@Nonnull OWLDataHasValue ce) {
        increment(ManchesterOWLSyntax.VALUE);
        ce.getProperty().accept(this);
        ce.getFiller().accept(this);
    }

    @Override
    public void visit(@Nonnull OWLDataMinCardinality ce) {
        increment(ManchesterOWLSyntax.MIN);
        ce.getProperty().accept(this);
        ce.getFiller().accept(this);
    }

    @Override
    public void visit(@Nonnull OWLDataExactCardinality ce) {
        increment(ManchesterOWLSyntax.EXACTLY);
        ce.getProperty().accept(this);
        ce.getFiller().accept(this);
    }

    @Override
    public void visit(@Nonnull OWLDataMaxCardinality ce) {
        increment(ManchesterOWLSyntax.MAX);
        ce.getProperty().accept(this);
        ce.getFiller().accept(this);
    }

    // OWLEntityVisitor

    @Override
    public void visit(@Nonnull OWLClass owlClass) {
        // terminal node
    }

    @Override
    public void visit(@Nonnull OWLObjectProperty owlObjectProperty) {
        // terminal node
    }

    @Override
    public void visit(@Nonnull OWLDataProperty owlDataProperty) {
        // terminal node
    }

    @Override
    public void visit(@Nonnull OWLNamedIndividual owlNamedIndividual) {
        // terminal node
    }

    @Override
    public void visit(@Nonnull OWLAnnotationProperty owlAnnotationProperty) {
        // terminal node
    }

    @Override
    public void visit(@Nonnull OWLObjectInverseOf property) {
        increment(ManchesterOWLSyntax.INVERSE);
        property.getInverse().accept(this);
    }

    @Override
    public void visit(@Nonnull OWLOntology owlOntology) {

    }

    // OWLDataVisitor

    @Override
    public void visit(@Nonnull OWLDatatype node) {
        // terminal node
    }

    @Override
    public void visit(@Nonnull OWLDataOneOf node) {
        final int size = node.getValues().size() >= 2 ? node.getValues().size() - 1 : 1;
        increment(ManchesterOWLSyntax.ONE_OF_DELIMETER, size);
        for (OWLLiteral val : node.getValues()) {
            val.accept(this);
        }
    }

    @Override
    public void visit(@Nonnull OWLDataComplementOf node) {
        increment(ManchesterOWLSyntax.NOT);
        node.getDataRange().accept(this);
    }

    @Override
    public void visit(@Nonnull OWLDataIntersectionOf node) {
        final int size = node.getOperands().size() >= 2 ? node.getOperands().size() - 1 : 1;
        increment(ManchesterOWLSyntax.AND, size);
        for (OWLDataRange dr : node.getOperands()) {
            dr.accept(this);
        }
    }

    @Override
    public void visit(@Nonnull OWLDataUnionOf node) {
        final int size = node.getOperands().size() >= 2 ? node.getOperands().size() - 1: 1;
        increment(ManchesterOWLSyntax.OR, size);
        for (OWLDataRange dr : node.getOperands()) {
            dr.accept(this);
        }
    }

    @Override
    public void visit(@Nonnull OWLDatatypeRestriction node) {
        node.getDatatype().accept(this);
        for (OWLFacetRestriction facetRestriction : node.getFacetRestrictions()) {
            facetRestriction.accept(this);
        }
    }


    @Override
    public void visit(@Nonnull OWLFacetRestriction node) {
        node.getFacetValue().accept(this);
    }

    // OWLAnnotationObjectVisitor

    @Override
    public void visit(@Nonnull OWLAnnotation node) {
        node.getProperty().accept(this);
        node.getValue().accept(this);
        for (OWLAnnotation anno : node.getAnnotations()) {
            anno.accept(this);
        }
    }

    // OWLAnnotationValueVisitor

    @Override
    public void visit(@Nonnull IRI iri) {

    }

    @Override
    public void visit(@Nonnull OWLAnonymousIndividual owlAnonymousIndividual) {

    }

    @Override
    public void visit(@Nonnull OWLLiteral node) {
        node.getDatatype().accept(this);
    }

    // OWLAnnotationAxiomVisitor

    @Override
    public void visit(@Nonnull OWLAnnotationAssertionAxiom axiom) {
        axiom.getSubject().accept(this);
        axiom.getProperty().accept(this);
        axiom.getValue().accept(this);
    }

    @Override
    public void visit(@Nonnull OWLSubAnnotationPropertyOfAxiom axiom) {
        axiom.getSubProperty().accept(this);
        axiom.getSuperProperty().accept(this);
    }

    @Override
    public void visit(@Nonnull OWLAnnotationPropertyDomainAxiom axiom) {
        axiom.getProperty().accept(this);
    }

    @Override
    public void visit(@Nonnull OWLAnnotationPropertyRangeAxiom axiom) {
        axiom.getProperty().accept(this);
    }

}
