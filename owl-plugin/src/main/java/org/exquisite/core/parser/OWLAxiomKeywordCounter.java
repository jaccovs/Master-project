package org.exquisite.core.parser;

import org.exquisite.core.costestimators.OWLAxiomKeywordCostsEstimator;
import org.semanticweb.owlapi.manchestersyntax.parser.ManchesterOWLSyntax;
import org.semanticweb.owlapi.model.*;

import javax.annotation.Nonnull;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * A visitor that parses instances of type OWLAxiom and counts the occurrences of ManchesterOWLSyntax-keywords.
 * A mapping between Functional-Style owl-api and Manchester Syntax keywords is implemented in this visitor pattern.
 * <p>
 * The keywords that are been counted are listed in the class member named <i>keywords</i> in {@link OWLAxiomKeywordCounter}
 * and comprise those keywords:
 * <ul>
 *     <li>ManchesterOWLSyntax.TYPE,</li>
 *     <li>ManchesterOWLSyntax.SAME_AS,</li>
 *     <li>ManchesterOWLSyntax.SAME_INDIVIDUAL,</li>
 *     <li>ManchesterOWLSyntax.DIFFERENT_FROM,</li>
 *     <li>ManchesterOWLSyntax.DIFFERENT_INDIVIDUALS,</li>
 *     <li>ManchesterOWLSyntax.SUBCLASS_OF,</li>
 *     <li>ManchesterOWLSyntax.DISJOINT_WITH,</li>
 *     <li>ManchesterOWLSyntax.DISJOINT_CLASSES,</li>
 *     <li>ManchesterOWLSyntax.DISJOINT_PROPERTIES</li>
 *     <li>ManchesterOWLSyntax.DISJOINT_UNION_OF</li>
 *     <li>ManchesterOWLSyntax.EQUIVALENT_TO,</li>
 *     <li>ManchesterOWLSyntax.EQUIVALENT_CLASSES,</li>
 *     <li>ManchesterOWLSyntax.EQUIVALENT_PROPERTIES,</li>
 *     <li>ManchesterOWLSyntax.AND,</li>
 *     <li>ManchesterOWLSyntax.OR,</li>
 *     <li>ManchesterOWLSyntax.NOT,</li>
 *     <li>ManchesterOWLSyntax.SOME,</li>
 *     <li>ManchesterOWLSyntax.ONLY,</li>
 *     <li>ManchesterOWLSyntax.MIN,</li>
 *     <li>ManchesterOWLSyntax.MAX,</li>
 *     <li>ManchesterOWLSyntax.EXACTLY,</li>
 *     <li>ManchesterOWLSyntax.VALUE,</li>
 *     <li>ManchesterOWLSyntax.INVERSE,</li>
 *     <li>ManchesterOWLSyntax.INVERSE_OF,</li>
 *     <li>ManchesterOWLSyntax.ONE_OF_DELIMETER,</li>
 *     <li>ManchesterOWLSyntax.THAT,</li>
 *     <li>ManchesterOWLSyntax.HAS_KEY</li>
 *     <li>ManchesterOWLSyntax.DOMAIN,</li>
 *     <li>ManchesterOWLSyntax.RANGE,</li>
 *     <li>ManchesterOWLSyntax.FUNCTIONAL,</li>
 *     <li>ManchesterOWLSyntax.INVERSE_FUNCTIONAL</li>
 *     <li>ManchesterOWLSyntax.REFLEXIVE</li>
 *     <li>ManchesterOWLSyntax.IRREFLEXIVE</li>
 *     <li>ManchesterOWLSyntax.SYMMETRIC</li>
 *     <li>ManchesterOWLSyntax.ASYMMETRIC</li>
 *     <li>ManchesterOWLSyntax.TRANSITIVE,</li>
 *     <li>ManchesterOWLSyntax.SUB_PROPERTY_OF,</li>
 *     <li>ManchesterOWLSyntax.SUB_PROPERTY_CHAIN</li>
 * </ul>
 *
 * An overview of the mapping from Manchester Syntax keywords to Functional-Style OWL-API axiom classes.
 *
 * <table summary="Mapping between Manchester Syntax and Funtional-Style OWL-Api Classes" border="1">
 *     <tr><th>Manchester-Syntax</th><th>Functional-Style Syntax</th></tr>
 *     <tr><td>ManchesterOWLSyntax.TYPE</td><td>OWLClassAssertionAxiom</td></tr>
 *     <tr><td>ManchesterOWLSyntax.SAME_AS</td><td>OWLSameIndividualAxiom</td></tr>
 *     <tr><td>ManchesterOWLSyntax.SAME_INDIVIDUAL</td><td>OWLSameIndividualAxiom</td></tr>
 *     <tr><td>ManchesterOWLSyntax.DIFFERENT_FROM</td><td>OWLDifferentIndividualsAxiom</td></tr>
 *     <tr><td>ManchesterOWLSyntax.DIFFERENT_INDIVIDUALS</td><td>OWLDifferentIndividualsAxiom</td></tr>
 *     <tr><td>ManchesterOWLSyntax.SUBCLASS_OF</td><td>OWLSubClassOfAxiom</td></tr>
 *     <tr><td>ManchesterOWLSyntax.DISJOINT_WITH</td><td>OWLDisjointClassesAxiom, OWLDisjointObjectPropertiesAxiom, OWLDisjointDataPropertiesAxiom</td></tr>
 *     <tr><td>ManchesterOWLSyntax.DISJOINT_CLASSES</td><td>OWLDisjointClassesAxiom</td></tr>
 *     <tr><td>ManchesterOWLSyntax.DISJOINT_PROPERTIES</td><td>OWLDisjointObjectPropertiesAxiom, OWLDisjointDataPropertiesAxiom</td></tr>
 *     <tr><td>ManchesterOWLSyntax.DISJOINT_UNION_OF</td><td>OWLDisjointUnionAxiom</td></tr>
 *     <tr><td>ManchesterOWLSyntax.EQUIVALENT_TO</td><td>OWLEquivalentClassesAxiom, OWLEquivalentObjectPropertiesAxiom, OWLEquivalentDataPropertiesAxiom, OWLDatatypeDefinitionAxiom</td></tr>
 *     <tr><td>ManchesterOWLSyntax.EQUIVALENT_CLASSES</td><td>OWLEquivalentClassesAxiom</td></tr>
 *     <tr><td>ManchesterOWLSyntax.EQUIVALENT_PROPERTIES</td><td>OWLEquivalentObjectPropertiesAxiom</td></tr>
 *     <tr><td>ManchesterOWLSyntax.AND</td><td>OWLObjectIntersectionOf, OWLDataIntersectionOf</td></tr>
 *     <tr><td>ManchesterOWLSyntax.OR</td><td>OWLObjectUnionOf, OWLDataUnionOf</td></tr>
 *     <tr><td>ManchesterOWLSyntax.NOT</td><td>OWLObjectComplementOf, OWLDataComplementOf, OWLNegativeObjectPropertyAssertionAxiom, OWLNegativeDataPropertyAssertionAxiom</td></tr>
 *     <tr><td>ManchesterOWLSyntax.SOME</td><td>OWLObjectSomeValuesFrom, OWLDataSomeValuesFrom</td></tr>
 *     <tr><td>ManchesterOWLSyntax.ONLY</td><td>OWLObjectAllValuesFrom, OWLDataAllValuesFrom</td></tr>
 *     <tr><td>ManchesterOWLSyntax.MIN</td><td>OWLObjectMinCardinality, OWLDataMinCardinality</td></tr>
 *     <tr><td>ManchesterOWLSyntax.MAX</td><td>OWLObjectMaxCardinality, OWLDataMaxCardinality</td></tr>
 *     <tr><td>ManchesterOWLSyntax.EXACTLY</td><td>OWLObjectExactCardinality, OWLDataExactCardinality</td></tr>
 *     <tr><td>ManchesterOWLSyntax.VALUE</td><td>OWLObjectHasValue, OWLDataHasValue</td></tr>
 *     <tr><td>ManchesterOWLSyntax.INVERSE</td><td>OWLInverseObjectPropertiesAxiom</td></tr>
 *     <tr><td>ManchesterOWLSyntax.INVERSE_OF</td><td>OWLInverseObjectPropertiesAxiom</td></tr>
 *     <tr><td>ManchesterOWLSyntax.ONE_OF_DELIMETER</td><td>OWLObjectOneOf, OWLDataOneOf</td></tr>
 *     <tr><td>ManchesterOWLSyntax.THAT</td><td>OWLObjectIntersectionOf</td></tr>
 *     <tr><td>ManchesterOWLSyntax.HAS_KEY</td><td>OWLHasKeyAxiom</td></tr>
 *     <tr><td>ManchesterOWLSyntax.DOMAIN</td><td>OWLObjectPropertyDomainAxiom, OWLDataPropertyDomainAxiom</td></tr>
 *     <tr><td>ManchesterOWLSyntax.RANGE</td><td>OWLObjectPropertyRangeAxiom, OWLDataPropertyRangeAxiom</td></tr>
 *     <tr><td>ManchesterOWLSyntax.FUNCTIONAL</td><td>OWLFunctionalObjectPropertyAxiom, OWLFunctionalDataPropertyAxiom</td></tr>
 *     <tr><td>ManchesterOWLSyntax.INVERSE_FUNCTIONAL</td><td>OWLInverseFunctionalObjectPropertyAxiom</td></tr>
 *     <tr><td>ManchesterOWLSyntax.REFLEXIVE</td><td>OWLReflexiveObjectPropertyAxiom</td></tr>
 *     <tr><td>ManchesterOWLSyntax.IRREFLEXIVE</td><td>OWLIrreflexiveObjectPropertyAxiom</td></tr>
 *     <tr><td>ManchesterOWLSyntax.SYMMETRIC</td><td>OWLSymmetricObjectPropertyAxiom</td></tr>
 *     <tr><td>ManchesterOWLSyntax.ASYMMETRIC</td><td>OWLAsymmetricObjectPropertyAxiom</td></tr>
 *     <tr><td>ManchesterOWLSyntax.TRANSITIVE</td><td>OWLTransitiveObjectPropertyAxiom</td></tr>
 *     <tr><td>ManchesterOWLSyntax.SUB_PROPERTY_OF</td><td>OWLSubObjectPropertyOfAxiom, OWLSubDataPropertyOfAxiom</td></tr>
 *     <tr><td>ManchesterOWLSyntax.SUB_PROPERTY_CHAIN</td><td>OWLSubPropertyChainOfAxiom</td></tr>
 * </table>
 *
 * </p>
 * @author wolfi
 * @see OWLAxiomKeywordCounter
 */
public class OWLAxiomKeywordCounter implements OWLAxiomVisitor, OWLClassExpressionVisitor, OWLPropertyExpressionVisitor, OWLDataRangeVisitor  {

    private Map<ManchesterOWLSyntax,Integer> map = new HashMap<>();

    public Integer get(ManchesterOWLSyntax keyword) {
        Integer count = map.get(keyword);
        if (count == null) return 0;
        return count;
    }

    /**
     * Increments the occurrence of a keyword by one.
     * @param keyword
     */
    private void increment(ManchesterOWLSyntax keyword) {
        // assert that the keyword is in the list of the keywords
        if (Arrays.asList(OWLAxiomKeywordCostsEstimator.keywords).contains(keyword)) {
            Integer i = map.get(keyword);
            if (i == null)
                map.put(keyword, 1);
            else
                map.put(keyword, i + 1);
        }
    }

    @Override
    public void visit(@Nonnull OWLDeclarationAxiom owlDeclarationAxiom) {

    }

    @Override
    public void visit(@Nonnull OWLDatatypeDefinitionAxiom owlDatatypeDefinitionAxiom) {
        increment(ManchesterOWLSyntax.EQUIVALENT_TO);
    }

    @Override
    public void visit(@Nonnull OWLAnnotationAssertionAxiom owlAnnotationAssertionAxiom) {

    }

    @Override
    public void visit(@Nonnull OWLSubAnnotationPropertyOfAxiom owlSubAnnotationPropertyOfAxiom) {

    }

    @Override
    public void visit(@Nonnull OWLAnnotationPropertyDomainAxiom owlAnnotationPropertyDomainAxiom) {

    }

    @Override
    public void visit(@Nonnull OWLAnnotationPropertyRangeAxiom owlAnnotationPropertyRangeAxiom) {

    }

    @Override
    public void visit(@Nonnull OWLSubClassOfAxiom owlSubClassOfAxiom) {
        increment(ManchesterOWLSyntax.SUBCLASS_OF);
    }

    @Override
    public void visit(@Nonnull OWLNegativeObjectPropertyAssertionAxiom owlNegativeObjectPropertyAssertionAxiom) {
        increment(ManchesterOWLSyntax.NOT);
    }

    @Override
    public void visit(@Nonnull OWLAsymmetricObjectPropertyAxiom owlAsymmetricObjectPropertyAxiom) {
        increment(ManchesterOWLSyntax.ASYMMETRIC);
    }

    @Override
    public void visit(@Nonnull OWLReflexiveObjectPropertyAxiom owlReflexiveObjectPropertyAxiom) {
        increment(ManchesterOWLSyntax.REFLEXIVE);
    }

    @Override
    public void visit(@Nonnull OWLDisjointClassesAxiom owlDisjointClassesAxiom) {
        increment(ManchesterOWLSyntax.DISJOINT_CLASSES);
        increment(ManchesterOWLSyntax.DISJOINT_WITH);
    }

    @Override
    public void visit(@Nonnull OWLDataPropertyDomainAxiom owlDataPropertyDomainAxiom) {
        increment(ManchesterOWLSyntax.DOMAIN);
    }

    @Override
    public void visit(@Nonnull OWLObjectPropertyDomainAxiom owlObjectPropertyDomainAxiom) {
        increment(ManchesterOWLSyntax.DOMAIN);
    }

    @Override
    public void visit(@Nonnull OWLEquivalentObjectPropertiesAxiom owlEquivalentObjectPropertiesAxiom) {
        increment(ManchesterOWLSyntax.EQUIVALENT_TO);
        increment(ManchesterOWLSyntax.EQUIVALENT_PROPERTIES);
    }

    @Override
    public void visit(@Nonnull OWLNegativeDataPropertyAssertionAxiom owlNegativeDataPropertyAssertionAxiom) {
        increment(ManchesterOWLSyntax.NOT);
    }

    @Override
    public void visit(@Nonnull OWLDifferentIndividualsAxiom owlDifferentIndividualsAxiom) {
        increment(ManchesterOWLSyntax.DIFFERENT_FROM);
        increment(ManchesterOWLSyntax.DIFFERENT_INDIVIDUALS);
    }

    @Override
    public void visit(@Nonnull OWLDisjointDataPropertiesAxiom owlDisjointDataPropertiesAxiom) {
        increment(ManchesterOWLSyntax.DISJOINT_WITH);
        increment(ManchesterOWLSyntax.DISJOINT_PROPERTIES);
    }

    @Override
    public void visit(@Nonnull OWLDisjointObjectPropertiesAxiom owlDisjointObjectPropertiesAxiom) {
        increment(ManchesterOWLSyntax.DISJOINT_WITH);
        increment(ManchesterOWLSyntax.DISJOINT_PROPERTIES);
    }

    @Override
    public void visit(@Nonnull OWLObjectPropertyRangeAxiom owlObjectPropertyRangeAxiom) {
        increment(ManchesterOWLSyntax.RANGE);
    }

    @Override
    public void visit(@Nonnull OWLObjectPropertyAssertionAxiom owlObjectPropertyAssertionAxiom) {

    }

    @Override
    public void visit(@Nonnull OWLFunctionalObjectPropertyAxiom owlFunctionalObjectPropertyAxiom) {
        increment(ManchesterOWLSyntax.FUNCTIONAL);
    }

    @Override
    public void visit(@Nonnull OWLSubObjectPropertyOfAxiom owlSubObjectPropertyOfAxiom) {
        increment(ManchesterOWLSyntax.SUB_PROPERTY_OF);
    }

    @Override
    public void visit(@Nonnull OWLDisjointUnionAxiom owlDisjointUnionAxiom) {
        increment(ManchesterOWLSyntax.DISJOINT_UNION_OF);
    }

    @Override
    public void visit(@Nonnull OWLSymmetricObjectPropertyAxiom owlSymmetricObjectPropertyAxiom) {
        increment(ManchesterOWLSyntax.SYMMETRIC);
    }

    @Override
    public void visit(@Nonnull OWLDataPropertyRangeAxiom owlDataPropertyRangeAxiom) {
        increment(ManchesterOWLSyntax.RANGE);
    }

    @Override
    public void visit(@Nonnull OWLFunctionalDataPropertyAxiom owlFunctionalDataPropertyAxiom) {
        increment(ManchesterOWLSyntax.FUNCTIONAL);
    }

    @Override
    public void visit(@Nonnull OWLEquivalentDataPropertiesAxiom owlEquivalentDataPropertiesAxiom) {
        increment(ManchesterOWLSyntax.EQUIVALENT_TO);
        increment(ManchesterOWLSyntax.EQUIVALENT_PROPERTIES);
    }

    @Override
    public void visit(@Nonnull OWLClassAssertionAxiom owlClassAssertionAxiom) {
        increment(ManchesterOWLSyntax.TYPE);
    }

    @Override
    public void visit(@Nonnull OWLEquivalentClassesAxiom owlEquivalentClassesAxiom) {
        increment(ManchesterOWLSyntax.EQUIVALENT_TO);
        increment(ManchesterOWLSyntax.EQUIVALENT_CLASSES);
    }

    @Override
    public void visit(@Nonnull OWLDataPropertyAssertionAxiom owlDataPropertyAssertionAxiom) {

    }

    @Override
    public void visit(@Nonnull OWLTransitiveObjectPropertyAxiom owlTransitiveObjectPropertyAxiom) {
        increment(ManchesterOWLSyntax.TRANSITIVE);
    }

    @Override
    public void visit(@Nonnull OWLIrreflexiveObjectPropertyAxiom owlIrreflexiveObjectPropertyAxiom) {
        increment(ManchesterOWLSyntax.IRREFLEXIVE);
    }

    @Override
    public void visit(@Nonnull OWLSubDataPropertyOfAxiom owlSubDataPropertyOfAxiom) {
        increment(ManchesterOWLSyntax.SUB_PROPERTY_OF);
    }

    @Override
    public void visit(@Nonnull OWLInverseFunctionalObjectPropertyAxiom owlInverseFunctionalObjectPropertyAxiom) {
        increment(ManchesterOWLSyntax.INVERSE_FUNCTIONAL);
    }

    @Override
    public void visit(@Nonnull OWLSameIndividualAxiom owlSameIndividualAxiom) {
        increment(ManchesterOWLSyntax.SAME_AS);
        increment(ManchesterOWLSyntax.SAME_INDIVIDUAL);
    }

    @Override
    public void visit(@Nonnull OWLSubPropertyChainOfAxiom owlSubPropertyChainOfAxiom) {
        increment(ManchesterOWLSyntax.SUB_PROPERTY_CHAIN);
    }

    @Override
    public void visit(@Nonnull OWLInverseObjectPropertiesAxiom owlInverseObjectPropertiesAxiom) {
        increment(ManchesterOWLSyntax.INVERSE);
        increment(ManchesterOWLSyntax.INVERSE_OF);
    }

    @Override
    public void visit(@Nonnull OWLHasKeyAxiom owlHasKeyAxiom) {
        increment(ManchesterOWLSyntax.HAS_KEY);
    }

    @Override
    public void visit(@Nonnull SWRLRule swrlRule) {

    }

    @Override
    public void visit(@Nonnull OWLClass owlClass) {

    }

    @Override
    public void visit(@Nonnull OWLObjectIntersectionOf owlObjectIntersectionOf) {
        increment(ManchesterOWLSyntax.AND);
        increment(ManchesterOWLSyntax.THAT);
    }

    @Override
    public void visit(@Nonnull OWLObjectUnionOf owlObjectUnionOf) {
        increment(ManchesterOWLSyntax.OR);
    }

    @Override
    public void visit(@Nonnull OWLObjectComplementOf owlObjectComplementOf) {
        increment(ManchesterOWLSyntax.NOT);
    }

    @Override
    public void visit(@Nonnull OWLObjectSomeValuesFrom owlObjectSomeValuesFrom) {
        increment(ManchesterOWLSyntax.SOME);
    }

    @Override
    public void visit(@Nonnull OWLObjectAllValuesFrom owlObjectAllValuesFrom) {
        increment(ManchesterOWLSyntax.ONLY);
    }

    @Override
    public void visit(@Nonnull OWLObjectHasValue owlObjectHasValue) {
        increment(ManchesterOWLSyntax.VALUE);
    }

    @Override
    public void visit(@Nonnull OWLObjectMinCardinality owlObjectMinCardinality) {
        increment(ManchesterOWLSyntax.MIN);
    }

    @Override
    public void visit(@Nonnull OWLObjectExactCardinality owlObjectExactCardinality) {
        increment(ManchesterOWLSyntax.EXACTLY);
    }

    @Override
    public void visit(@Nonnull OWLObjectMaxCardinality owlObjectMaxCardinality) {
        increment(ManchesterOWLSyntax.MAX);
    }

    @Override
    public void visit(@Nonnull OWLObjectHasSelf owlObjectHasSelf) {
        increment(ManchesterOWLSyntax.SELF);
    }

    @Override
    public void visit(@Nonnull OWLObjectOneOf owlObjectOneOf) {
        increment(ManchesterOWLSyntax.ONE_OF_DELIMETER);
    }

    @Override
    public void visit(@Nonnull OWLDataSomeValuesFrom owlDataSomeValuesFrom) {
        increment(ManchesterOWLSyntax.SOME);
    }

    @Override
    public void visit(@Nonnull OWLDataAllValuesFrom owlDataAllValuesFrom) {
        increment(ManchesterOWLSyntax.ONLY);
    }

    @Override
    public void visit(@Nonnull OWLDataHasValue owlDataHasValue) {
        increment(ManchesterOWLSyntax.VALUE);
    }

    @Override
    public void visit(@Nonnull OWLDataMinCardinality owlDataMinCardinality) {
        increment(ManchesterOWLSyntax.MIN);
    }

    @Override
    public void visit(@Nonnull OWLDataExactCardinality owlDataExactCardinality) {
        increment(ManchesterOWLSyntax.EXACTLY);
    }

    @Override
    public void visit(@Nonnull OWLDataMaxCardinality owlDataMaxCardinality) {
        increment(ManchesterOWLSyntax.MAX);
    }

    @Override
    public void visit(@Nonnull OWLObjectProperty owlObjectProperty) {

    }

    @Override
    public void visit(@Nonnull OWLObjectInverseOf owlObjectInverseOf) {
        increment(ManchesterOWLSyntax.INVERSE);
    }

    @Override
    public void visit(@Nonnull OWLDataProperty owlDataProperty) {

    }

    @Override
    public void visit(@Nonnull OWLAnnotationProperty owlAnnotationProperty) {

    }

    @Override
    public void visit(@Nonnull OWLDatatype owlDatatype) {

    }

    @Override
    public void visit(@Nonnull OWLDataOneOf owlDataOneOf) {
        increment(ManchesterOWLSyntax.ONE_OF_DELIMETER);
    }

    @Override
    public void visit(@Nonnull OWLDataComplementOf owlDataComplementOf) {
        increment(ManchesterOWLSyntax.NOT);
    }

    @Override
    public void visit(@Nonnull OWLDataIntersectionOf owlDataIntersectionOf) {
        increment(ManchesterOWLSyntax.AND);
    }

    @Override
    public void visit(@Nonnull OWLDataUnionOf owlDataUnionOf) {
        increment(ManchesterOWLSyntax.OR);
    }

    @Override
    public void visit(@Nonnull OWLDatatypeRestriction owlDatatypeRestriction) {

    }
}
