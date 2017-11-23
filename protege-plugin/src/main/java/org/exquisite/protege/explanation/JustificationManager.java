package org.exquisite.protege.explanation;

import org.protege.editor.core.Disposable;
import org.protege.editor.core.log.LogBanner;
import org.protege.editor.owl.model.OWLModelManager;
import org.semanticweb.owl.explanation.api.Explanation;
import org.semanticweb.owl.explanation.api.*;
import org.semanticweb.owl.explanation.impl.blackbox.checker.InconsistentOntologyExplanationGeneratorFactory;
import org.semanticweb.owl.explanation.impl.laconic.LaconicExplanationGeneratorFactory;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyChange;
import org.semanticweb.owlapi.model.OWLOntologyChangeListener;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import org.semanticweb.owlapi.reasoner.OWLReasonerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.exquisite.protege.explanation.ExplanationLogging.MARKER;
/*
 * Copyright (C) 2008, University of Manchester
 *
 * Modifications to the initial code base are copyright of their
 * respective authors, or their employers as appropriate.  Authorship
 * of the modifications may be determined from the ChangeLog placed at
 * the end of this file.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.

 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.

 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 */


/**
 * Author: Matthew Horridge The University Of Manchester Information Management Group Date:
 * 03-Oct-2008
 * Manages aspects of explanation in Protege 4.
 *
 * @apiNote This is a <i>modified</i> copy from the explanation-workbench 5.0.0-beta-19
 * (Revision Number 3c2a4fa7f0591c18693d2b8a6bd0a9739dde2340) at https://github.com/protegeproject/explanation-workbench.git
 * <br>modifications: visibility changes by @author wolfi, code changes by @author wolfi
 */
public class JustificationManager implements Disposable {

    private ExecutorService executorService;

    private final OWLOntologyChangeListener ontologyChangeListener;

    private static final String KEY = "org.exquisite.protege.explanation";

    private static final Logger logger = LoggerFactory.getLogger(JustificationManager.class);

    private OWLModelManager modelManager;

    private OWLOntology ontology;

    private CachingRootDerivedGenerator rootDerivedGenerator;

    private List<ExplanationManagerListener> listeners;

    private boolean findAllExplanations;

    private JustificationCacheManager justificationCacheManager = new JustificationCacheManager();
    
    private JustificationGeneratorProgressDialog progressDialog;

    private JustificationManager(JFrame parentWindow, OWLModelManager modelManager, OWLOntology ontology) {
        this.modelManager = modelManager;
        this.ontology = ontology;
        rootDerivedGenerator = new CachingRootDerivedGenerator(modelManager);
        listeners = new ArrayList<>();
        findAllExplanations = true;
        progressDialog = new JustificationGeneratorProgressDialog(parentWindow);
        executorService = Executors.newSingleThreadExecutor();
        ontologyChangeListener = changes -> {
            if (!changes.isEmpty()) {
                if (changes.get(0).getOntology().equals(ontology)) {
                    // the change involves the ontology this cache manager is responsible for

                    // now check if one of the changes is an addition, if so clear the cache and stop search
                    for (OWLOntologyChange change : changes) {
                        assert change.getOntology().equals(JustificationManager.this.ontology);

                        if (change.isAddAxiom()) {
                            JustificationManager.this.justificationCacheManager.clear();
                            return;
                        }
                    }

                }
            }
        };
        modelManager.addOntologyChangeListener(ontologyChangeListener);
    }

    private OWLReasonerFactory getReasonerFactory() {
        return new ProtegeOWLReasonerFactoryWrapper(modelManager.getOWLReasonerManager().getCurrentReasonerFactory());
    }

    /**
     * Gets the number of explanations that have actually been computed for an entailment
     * @param entailment The entailment
     * @param type The type of justification to be counted.
     * @return The number of computed explanations.  If no explanations have been computed this value
     *         will be -1.
     */
    int getComputedExplanationCount(OWLAxiom entailment, JustificationType type) {
        JustificationCache cache = justificationCacheManager.getJustificationCache(type);
        if(cache.contains(entailment)) {
            return cache.get(entailment).size();
        }
        else {
            return  -1;
        }
    }

    Set<Explanation<OWLAxiom>> getJustifications(OWLAxiom entailment, JustificationType type) throws ExplanationException {
        JustificationCache cache = justificationCacheManager.getJustificationCache(type);
        if (!cache.contains(entailment)) {
            Set<Explanation<OWLAxiom>> expls = computeJustifications(entailment, type);
            cache.put(expls);
        }
        return cache.get(entailment);
    }

    Explanation<OWLAxiom> getLaconicJustification(Explanation<OWLAxiom> explanation) {
        Set<Explanation<OWLAxiom>> explanations = getLaconicExplanations(explanation, 1);
        if(explanations.isEmpty()) {
            return Explanation.getEmptyExplanation(explanation.getEntailment());
        }
        else {
            return explanations.iterator().next();
        }
    }

    private Set<Explanation<OWLAxiom>> computeJustifications(OWLAxiom entailment, JustificationType justificationType) throws ExplanationException {
        logger.info(LogBanner.start("Computing Justifications"));
        logger.info(MARKER, "Computing justifications for {}", entailment);
        Set<OWLAxiom> axioms = new HashSet<>();
        for (OWLOntology ont : modelManager.getActiveOntologies()) {
            axioms.addAll(ont.getAxioms());
        }
        ExplanationGeneratorCallable callable = new ExplanationGeneratorCallable(
                axioms,
                entailment,
                getCurrentExplanationGeneratorFactory(justificationType),
                findAllExplanations,
                progressDialog);
        try {
            executorService.submit(callable);
        }
        catch (ExplanationGeneratorInterruptedException e) {
            logger.info(MARKER, "Justification computation terminated early by user");
        }
        progressDialog.reset();
        progressDialog.setVisible(true);

        HashSet<Explanation<OWLAxiom>> explanations = new HashSet<>(callable.found);
        logger.info(MARKER, "A total of {} explanations have been computed", explanations.size());
        fireExplanationsComputed(entailment);
        logger.info(LogBanner.end());
        return explanations;
    }


    private ExplanationGeneratorFactory<OWLAxiom> getCurrentExplanationGeneratorFactory(JustificationType type) {
        OWLReasoner reasoner = modelManager.getOWLReasonerManager().getCurrentReasoner();
        if(reasoner.isConsistent()) {
            if (type.equals(JustificationType.LACONIC)) {
                OWLReasonerFactory rf = getReasonerFactory();
                return ExplanationManager.createLaconicExplanationGeneratorFactory(rf, progressDialog.getProgressMonitor());
            }
            else {
                OWLReasonerFactory rf = getReasonerFactory();
                return ExplanationManager.createExplanationGeneratorFactory(rf, progressDialog.getProgressMonitor());
            }    
        }
        else {
            if (type.equals(JustificationType.LACONIC)) {
                OWLReasonerFactory rf = getReasonerFactory();
                InconsistentOntologyExplanationGeneratorFactory fac = new InconsistentOntologyExplanationGeneratorFactory(rf, Long.MAX_VALUE);
                return new LaconicExplanationGeneratorFactory<>(fac);
            }
            else {
                OWLReasonerFactory rf = getReasonerFactory();
                return new InconsistentOntologyExplanationGeneratorFactory(rf, Long.MAX_VALUE);
            }
        }
        
    }


    private Set<Explanation<OWLAxiom>> getLaconicExplanations(Explanation<OWLAxiom> explanation, int limit) throws ExplanationException {
        return computeLaconicExplanations(explanation, limit);
    }


    private Set<Explanation<OWLAxiom>> computeLaconicExplanations(Explanation<OWLAxiom> explanation, int limit) throws ExplanationException {
        try {
            if(modelManager.getReasoner().isConsistent()) {
                OWLReasonerFactory rf = getReasonerFactory();
                ExplanationGenerator<OWLAxiom> g = ExplanationManager.createLaconicExplanationGeneratorFactory(rf).createExplanationGenerator(explanation.getAxioms());
                return g.getExplanations(explanation.getEntailment(), limit);
            }
            else {
                OWLReasonerFactory rf = getReasonerFactory();
                InconsistentOntologyExplanationGeneratorFactory fac = new InconsistentOntologyExplanationGeneratorFactory(rf, Long.MAX_VALUE);
                LaconicExplanationGeneratorFactory<OWLAxiom> lacFac = new LaconicExplanationGeneratorFactory<>(fac);
                ExplanationGenerator<OWLAxiom> g = lacFac.createExplanationGenerator(explanation.getAxioms());
                return g.getExplanations(explanation.getEntailment(), limit);
            }
        }
        catch (ExplanationException e) {
            throw new ExplanationException(e);
        }
    }


    public void dispose() {
        rootDerivedGenerator.dispose();
        modelManager.removeOntologyChangeListener(ontologyChangeListener);
        justificationCacheManager.clear();
    }


    private void fireExplanationsComputed(OWLAxiom entailment) {
        for (ExplanationManagerListener lsnr : new ArrayList<>(listeners)) {
            lsnr.explanationsComputed(entailment);
        }
    }

    static synchronized JustificationManager getExplanationManager(JFrame parentWindow, OWLModelManager modelManager, OWLOntology ontology) {
        final String key = KEY + "_" + ontology.getOntologyID();

        JustificationManager m = modelManager.get(key);
        if (m == null) {
            m = new JustificationManager(parentWindow, modelManager, ontology);
            modelManager.put(key, m);
        }
        return m;
    }


    private static class ExplanationGeneratorCallable implements Callable<Set<Explanation<OWLAxiom>>>, ExplanationProgressMonitor<OWLAxiom> {

        private final Set<OWLAxiom> axioms;

        private final OWLAxiom axiom;

        private int limit = Integer.MAX_VALUE;

        private final Set<Explanation<OWLAxiom>> found = new HashSet<>();

        private final JustificationGeneratorProgressDialog progressDialog;

        private final boolean findAllExplanations;

        private final ExplanationGeneratorFactory<OWLAxiom> factory;

        private ExplanationGeneratorCallable(Set<OWLAxiom> axioms, OWLAxiom axiom, ExplanationGeneratorFactory<OWLAxiom> factory, boolean findAllExplanations, JustificationGeneratorProgressDialog progressDialog) {
            this.axioms = axioms;
            this.axiom = axiom;
            this.progressDialog = progressDialog;
            this.findAllExplanations = findAllExplanations;
            this.factory = factory;
        }

        /**
         * Computes a result, or throws an exception if unable to do so.
         * @return computed result
         * @throws Exception if unable to compute a result
         */
        public Set<Explanation<OWLAxiom>> call() throws Exception {
            found.clear();
            ExplanationGenerator<OWLAxiom> delegate = factory.createExplanationGenerator(axioms, this);
            progressDialog.reset();
            try {
                if (findAllExplanations) {
                    delegate.getExplanations(axiom);
                }
                else {
                    delegate.getExplanations(axiom, limit);
                }
            }
            finally {
                SwingUtilities.invokeLater(() -> progressDialog.setVisible(false));
            }

            return found;
        }

        public void foundExplanation(ExplanationGenerator<OWLAxiom> explanationGenerator, Explanation<OWLAxiom> explanation, Set<Explanation<OWLAxiom>> explanations) {
            progressDialog.getProgressMonitor().foundExplanation(explanationGenerator, explanation, explanations);
            found.add(explanation);
            logger.info(MARKER, "Explanation {} found", found.size(), explanation.getEntailment());
        }

        public boolean isCancelled() {
            return progressDialog.getProgressMonitor().isCancelled();
        }
    }

}
