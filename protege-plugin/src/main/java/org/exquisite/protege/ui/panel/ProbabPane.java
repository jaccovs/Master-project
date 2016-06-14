package org.exquisite.protege.ui.panel;

import org.exquisite.core.costestimators.OWLAxiomKeywordCostsEstimator;
import org.exquisite.protege.model.EditorKitHook;
import org.protege.editor.owl.model.OWLWorkspace;
import org.semanticweb.owlapi.manchestersyntax.parser.ManchesterOWLSyntax;
import org.semanticweb.owlapi.manchestersyntax.renderer.ManchesterOWLSyntaxOWLObjectRendererImpl;
import org.semanticweb.owlapi.model.OWLLogicalAxiom;
import org.semanticweb.owlapi.model.OWLOntology;

import javax.swing.*;
import java.awt.*;
import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

public class ProbabPane extends JPanel {

    private Set<KeywordSliderPanel> sliders;

    private Map<ManchesterOWLSyntax, BigDecimal> probabMap = new LinkedHashMap<ManchesterOWLSyntax, BigDecimal>();

    protected void copyProbabMap(Map<ManchesterOWLSyntax, BigDecimal> from, Map<ManchesterOWLSyntax, BigDecimal> to) {
        for (ManchesterOWLSyntax keyword : from.keySet())
            to.put(keyword,from.get(keyword));
    }

    protected Set<ManchesterOWLSyntax> getUsedKeywords(OWLOntology ontology) {
        ManchesterOWLSyntaxOWLObjectRendererImpl renderer = new ManchesterOWLSyntaxOWLObjectRendererImpl();
        Set<OWLLogicalAxiom> axioms = ontology.getLogicalAxioms();

        String axiomStr = "";
        for (OWLLogicalAxiom axiom : axioms)
            axiomStr += renderer.render(axiom) + "\t";

        Set<ManchesterOWLSyntax> used = new LinkedHashSet<ManchesterOWLSyntax>();
        for (ManchesterOWLSyntax keyword : OWLAxiomKeywordCostsEstimator.keywords) {
            if (axiomStr.contains(keyword.toString()))
                used.add(keyword);
        }
        return used;
    }

    public Map<ManchesterOWLSyntax,BigDecimal> getProbabMap() {
        for (KeywordSliderPanel sliderPanel : sliders) {
            probabMap.put(sliderPanel.getKeyword(),sliderPanel.getProbab());
        }
        return probabMap;
    }

    protected Set<KeywordSliderPanel> createSliders(Set<ManchesterOWLSyntax> keywords, OWLWorkspace owlWorkspace) {
        Set<KeywordSliderPanel> sliders = new LinkedHashSet<KeywordSliderPanel>();

        for (ManchesterOWLSyntax keyword : keywords)
            sliders.add(new KeywordSliderPanel(keyword,probabMap.get(keyword),owlWorkspace));

        return sliders;
    }

    public ProbabPane(EditorKitHook editorKitHook) { // TODO
        /*
        DiagnosisEngineFactory creator = editorKitHook.getActiveOntologyDiagnosisSearcher().getDiagnosisEngineFactory();
        OWLOntology ontology =  ((OWLTheory) creator.getSearch().getSearchable()).getOriginalOntology();

        OWLAxiomKeywordCostsEstimator est = (OWLAxiomKeywordCostsEstimator) creator.getSearch().getCostsEstimator();

        copyProbabMap(est.getKeywordProbabilities(),probabMap);
        Set<ManchesterOWLSyntax> usedKeywords = getUsedKeywords(ontology);
        sliders = createSliders(usedKeywords,editorKitHook.getOWLEditorKit().getOWLWorkspace());

        setLayout(new BoxLayout(this,BoxLayout.Y_AXIS));
        setBackground(Color.WHITE);

        for (KeywordSliderPanel slider : sliders) {
            add(slider);
            add(Box.createVerticalStrut(10));

        }
        */

    }

}
