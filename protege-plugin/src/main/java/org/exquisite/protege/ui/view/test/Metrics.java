package org.exquisite.protege.ui.view.test;

import org.exquisite.core.model.Diagnosis;
import org.protege.editor.owl.model.OWLModelManager;
import org.protege.editor.owl.model.event.EventType;
import org.protege.editor.owl.model.event.OWLModelManagerChangeEvent;
import org.protege.editor.owl.model.event.OWLModelManagerListener;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.*;

public class Metrics extends JPanel {
    private static final long serialVersionUID = -2017045836890114258L;
    private JButton refreshButton = new JButton("Refresh");
    private JLabel textComponent = new JLabel();
    private OWLModelManager modelManager;

    private Set<Diagnosis<Integer>> diagnoses = null;
    private Iterator<Diagnosis<Integer>> iterator = null;

    private ActionListener refreshAction = new ActionListener() {
        public void actionPerformed(ActionEvent e) {
            showNextDiagnosis();
        }
    };

    private OWLModelManagerListener modelListener = new OWLModelManagerListener() {
        public void handleChange(OWLModelManagerChangeEvent event) {
            if (event.getType() == EventType.ACTIVE_ONTOLOGY_CHANGED) {
                recalculate();
            }
        }
    };

    public Metrics(OWLModelManager modelManager) {
        this.modelManager = modelManager;
        recalculate();

        modelManager.addListener(modelListener);
        refreshButton.addActionListener(refreshAction);

        add(textComponent);
        add(refreshButton);
    }

    public void dispose() {
        modelManager.removeListener(modelListener);
        refreshButton.removeActionListener(refreshAction);
    }

    private void recalculate() {
        int count = modelManager.getActiveOntology().getClassesInSignature().size();
        if (count == 0) {
            count = 1;  // owl:Thing is always there.
        }

        Diagnosis<Integer> D1 = getDiagnosis(3, 4);
        Diagnosis<Integer> D2 = getDiagnosis(4, 5);
        Diagnosis<Integer> D3 = getDiagnosis(6, 7);
        Diagnosis<Integer> D4 = getDiagnosis(1, 4);
        Diagnosis<Integer> D5 = getDiagnosis(1, 2);
        Diagnosis<Integer> D6 = getDiagnosis(2, 3);

        this.diagnoses = getSet(D1, D2, D3, D4, D5, D6);
        this.iterator = this.diagnoses.iterator();

        showNextDiagnosis();
    }

    private void showNextDiagnosis() {
        if (this.iterator == null)
            textComponent.setText("no diagnoses");
        else if (this.iterator.hasNext())
            textComponent.setText("" + this.iterator.next());
        else
            textComponent.setText("no more diagnoses");
    }


    @SafeVarargs
    public static <T> Diagnosis<T> getDiagnosis(T... elements) {
        List<T> ts = Arrays.asList(elements);
        return new Diagnosis<T>(ts);
    }

    @SafeVarargs
    public static <T> HashSet<T> getSet(T... elements) {
        return new HashSet<>(Arrays.asList(elements));
    }
}
