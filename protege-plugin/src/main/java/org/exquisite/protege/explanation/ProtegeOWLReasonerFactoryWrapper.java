package org.exquisite.protege.explanation;

import org.protege.editor.owl.model.inference.ProtegeOWLReasonerInfo;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.reasoner.IllegalConfigurationException;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import org.semanticweb.owlapi.reasoner.OWLReasonerConfiguration;
import org.semanticweb.owlapi.reasoner.OWLReasonerFactory;

import javax.annotation.Nonnull;
/*
 * Copyright (C) 2010, University of Manchester
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
 * Author: Matthew Horridge
 * The University of Manchester
 * Information Management Group
 * Date: 06-Apr-2010
 *
 * @apiNote This is a <i>modified</i> copy from the explanation-workbench 5.0.0-beta-19
 * (Revision Number 3c2a4fa7f0591c18693d2b8a6bd0a9739dde2340) at https://github.com/protegeproject/explanation-workbench.git
 * <br>modifications: visibility changes by @author wolfi, annotation changes by @author wolfi
 */
public class ProtegeOWLReasonerFactoryWrapper implements OWLReasonerFactory {

    private OWLReasonerFactory reasonerFactory;

    ProtegeOWLReasonerFactoryWrapper(ProtegeOWLReasonerInfo info) {
        this.reasonerFactory = info.getReasonerFactory();
    }

    @Nonnull
    public String getReasonerName() {
        return reasonerFactory.getReasonerName();
    }

    @Nonnull
    public OWLReasoner createNonBufferingReasoner(@Nonnull OWLOntology ontology) {
        return reasonerFactory.createReasoner(ontology);
    }

    @Nonnull
    public OWLReasoner createReasoner(@Nonnull OWLOntology ontology) {
        return reasonerFactory.createReasoner(ontology);
    }

    @Nonnull
    public OWLReasoner createNonBufferingReasoner(@Nonnull OWLOntology ontology, @Nonnull OWLReasonerConfiguration owlReasonerConfiguration) throws IllegalConfigurationException {
        return reasonerFactory.createReasoner(ontology);
    }

    @Nonnull
    public OWLReasoner createReasoner(@Nonnull OWLOntology ontology, @Nonnull OWLReasonerConfiguration owlReasonerConfiguration) throws IllegalConfigurationException {
        return reasonerFactory.createReasoner(ontology);
    }
}
