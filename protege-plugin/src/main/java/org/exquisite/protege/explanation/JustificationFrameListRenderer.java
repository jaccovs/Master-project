package org.exquisite.protege.explanation;

import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.ui.framelist.OWLFrameListRenderer;
import org.semanticweb.owlapi.model.OWLObject;

import javax.swing.*;

/**
 * Author: Matthew Horridge
 * Stanford University
 * Bio-Medical Informatics Research Group
 * Date: 19/03/2012
 *
 * @apiNote This is a <i>modified</i> copy from the explanation-workbench 5.0.0-beta-19
 * (Revision Number 3c2a4fa7f0591c18693d2b8a6bd0a9739dde2340) at https://github.com/protegeproject/explanation-workbench.git
 * <br>modifications: visibility changes by @author wolfi
 */
public class JustificationFrameListRenderer extends OWLFrameListRenderer {

    JustificationFrameListRenderer(OWLEditorKit owlEditorKit) {
        super(owlEditorKit);
        setHighlightUnsatisfiableClasses(false);
        setHighlightUnsatisfiableProperties(false);
    }


    @Override
    protected OWLObject getIconObject(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
        return null;
    }
}
