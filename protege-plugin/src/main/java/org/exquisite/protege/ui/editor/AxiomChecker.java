package org.exquisite.protege.ui.editor;

import org.exquisite.core.parser.ManchesterOWLSyntaxEditorParser;
import org.protege.editor.owl.model.OWLModelManager;
import org.protege.editor.owl.model.classexpression.OWLExpressionParserException;
import org.protege.editor.owl.model.parser.ParserUtil;
import org.protege.editor.owl.model.parser.ProtegeOWLEntityChecker;
import org.protege.editor.owl.ui.clsdescriptioneditor.OWLExpressionChecker;
import org.semanticweb.owlapi.expression.ParserException;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLLogicalAxiom;

import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Created with IntelliJ IDEA.
 * User: pfleiss
 * Date: 06.09.12
 * Time: 11:17
 * To change this template use File | Settings | File Templates.
 */
public class AxiomChecker implements OWLExpressionChecker<Set<OWLLogicalAxiom>> {

    private OWLModelManager modelManager;

    public AxiomChecker(OWLModelManager modelManager) {
        this.modelManager = modelManager;
    }

    public void check(String text) throws OWLExpressionParserException {
        createObject(text);
    }

    public Set<OWLLogicalAxiom> createObject(String text) throws OWLExpressionParserException {

        Set<OWLLogicalAxiom> axioms = new LinkedHashSet<OWLLogicalAxiom>();

        String[] splitted = text.split(",");
        for (String a : splitted) {
            ManchesterOWLSyntaxEditorParser parser = new ManchesterOWLSyntaxEditorParser(modelManager.getOWLDataFactory(), a);
            parser.setOWLEntityChecker(new ProtegeOWLEntityChecker(modelManager.getOWLEntityFinder()));
            try {
                OWLAxiom ax = parser.parseAxiom();
                axioms.add((OWLLogicalAxiom) ax);
            } catch (ParserException e) {
                throw ParserUtil.convertException(e);
            }
        }
        return axioms;
    }
}
