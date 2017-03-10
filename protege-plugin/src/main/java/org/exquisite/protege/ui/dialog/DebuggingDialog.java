package org.exquisite.protege.ui.dialog;

import com.google.common.base.Optional;
import org.exquisite.core.model.Diagnosis;
import org.protege.editor.owl.ui.renderer.OWLOntologyCellRenderer;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLLogicalAxiom;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.util.OntologyIRIShortFormProvider;

import javax.swing.*;
import java.awt.*;
import java.io.StringWriter;
import java.util.Set;

/**
 * A central repository of dialogues that may happen during a debugging session.
 */
public class DebuggingDialog {

    /**
     * Information dialog that the ontology is consistent.
     *
     * @param ontology The ontology.
     */
    public static void showConsistentOntologyMessage(OWLOntology ontology) {
        final String title = "Consistent Ontology!";
        final String message = "The ontology \"" + getOntologyName(ontology) + "\" is <u><i>consistent</i></u>.";
        showMessageDialog(title,message);
    }

    /**
     * Information dialog that the ontology is coherent (and consistent).
     *
     * @param ontology The ontology.
     */
    public static void showCoherentOntologyMessage(OWLOntology ontology) {
        final String title = "Coherent (& Consistent) Ontology!";
        final String ontologyName = getOntologyName(ontology);
        final String message = "The ontology \"" + ontologyName + "\" is <u><i>coherent</i></u> and <u><i>consistent</i></u>.";
        showMessageDialog(title,message);
    }

    /**
     * Information dialog that the user has to choose a reasoner to be able to debug the ontology.
     */
    public static void showNoReasonerSelectedMessage() {
        final String title = "No Reasoner Selected!";
        final String message = "<b>There is no reasoner selected!</b><br><br>" +
                "Please select a reasoner from the menu <i>Reasoner</i> (e.g. HermiT).";
        showMessageDialog(title, message);
    }

    /**
     * Information dialog that the debugger has found a diagnosis (or set of faulty axioms) for the ontology.
     *
     * @param diagnoses The set of diagnoses which is expected to have size 1.
     * @param ontology The debugged ontology.
     */
    public static void showDiagnosisFoundMessage(Set<Diagnosis<OWLLogicalAxiom>> diagnoses, OWLOntology ontology) {
        final Diagnosis<OWLLogicalAxiom> diagnosis = diagnoses.iterator().next();

        final String title = "Set Of Faulty Axioms Found!";
        final String message = "The <b>faulty axioms</b> corresponding to your preferences (test cases) are <b>found</b>!<br><br>" +
                "The debugger identified <font color=\"red\">" + diagnosis.getFormulas().size() + " faulty axioms</font> in the " +
                getOntologyName(ontology) + " ontology";
        showMessageDialog(title, message);
    }

    /**
     * Information dialog that a running debugging session has been stopped automatically because of some reason.
     *
     * @param ontology The ontology of the stopped debugging session.
     * @param reason The reason why a session had to be stopped automatically.
     */
    public static void showDebuggingSessionStoppedMessage(OWLOntology ontology, String reason) {
        final String title = "Debugging Session Stopped!";
        final String message = "The debugging session for the ontology " + getOntologyName(ontology) +
                " has been stopped.<br><br>The reason is: <b>" + reason + "</b>";
        showMessageDialog(title, message);
    }

    /**
     * Information dialog that the debugger has been reset.
     */
    public static void showDebuggerResetMessage() {
        final String title = "Debugger Has Been Reset!";
        final String message = "<b>The debugger has been reset.</b>";
        showMessageDialog(title, message);
    }

    /**
     * Confirmation dialog with yes, no and cancel option.
     *
     * @param title A title.
     * @param message A message.
     * @return an int indicating the option selected by the user (JOptionPane.YES_OPTION, JOptionPane.NO_OPTION, JOptionPane.CANCEL_OPTION)
     */
    public static int showConfirmDialog(String title, String message) {
        return JOptionPane.showConfirmDialog(null, message, title, JOptionPane.YES_NO_CANCEL_OPTION);
    }

    /**
     * Error dialog which informs the user that an unexpected error has occurred. Together with the message a
     * stacktrace will be shown.
     *
     * @param title A title.
     * @param message An error message.
     * @param ex An exception that has occurred. The stack trace will be presented too for a more detailed information.
     */
    public static void showErrorDialog(String title, String message, Exception ex) {
        // the panel to be shown as message body
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.PAGE_AXIS));
        panel.setPreferredSize(new Dimension(600,200));

        // the message itself in a label
        panel.add(new JLabel(message));

        // if there is an exception, then show it's stack trace in an scrollable text area below the message
        if (ex != null) {
            panel.add(Box.createRigidArea(new Dimension(0,5))); // some space
            panel.add(new JLabel("Stacktrace:"));

            final JTextArea stackTraceArea = new JTextArea();
            stackTraceArea.setBackground(Color.WHITE);
            stackTraceArea.setFont(new Font("Courier", Font.PLAIN, 10));
            stackTraceArea.setEditable(false);
            stackTraceArea.setWrapStyleWord(true);

            // build the stack trace information for the stackTraceArea
            StringWriter w = new StringWriter();
            w.write((ex.getLocalizedMessage()!=null?ex.getLocalizedMessage():ex.getClass().getCanonicalName()) + "\n");
            StackTraceElement[] elements = ex.getStackTrace();
            for (int i = 0; i < elements.length; i++)
                w.write("  at " + elements[i].toString() + "\n");

            stackTraceArea.setText(w.toString());
            stackTraceArea.setCaretPosition(0);

            JScrollPane scrollPane = new JScrollPane(stackTraceArea);
            scrollPane.setPreferredSize(new Dimension(250, 80));
            scrollPane.setAlignmentX(Component.LEFT_ALIGNMENT);

            panel.add(scrollPane);
        }

        JOptionPane.showMessageDialog(null, panel, title, JOptionPane.ERROR_MESSAGE);
    }

    /**
     * This method extracts a human readable format from the ontology.
     * The code is an adaptation from the class org.protege.editor.owl.ui.renderer.OWLOntologyCellRenderer
     *
     * @param ontology
     * @return
     * @see OWLOntologyCellRenderer
     */
    public static String getOntologyName(OWLOntology ontology) {
        if (ontology.getOntologyID().isAnonymous()) {
            return ontology.getOntologyID().toString();
        }

        final Optional<IRI> iri = ontology.getOntologyID().getDefaultDocumentIRI();
        StringBuilder sb = new StringBuilder();
        sb.append("<b>");
        if (iri.isPresent()) {
            String shortForm = new OntologyIRIShortFormProvider().getShortForm(iri.get());
            sb.append(shortForm);
        }
        else {
            sb.append("Anonymous ontology");
        }
        sb.append("</b> <font color=\"blue\">(");
        if (iri.isPresent()) {
            sb.append(iri.get().toString());
        }
        sb.append(")</font>");
        return sb.toString();
    }

    private static void showMessageDialog(String title, String message) {
        StringBuilder sb = new StringBuilder("<html><body>").append(message).append("</body></html>");
        JOptionPane.showMessageDialog(null, sb.toString(), title, JOptionPane.INFORMATION_MESSAGE);
    }

}
