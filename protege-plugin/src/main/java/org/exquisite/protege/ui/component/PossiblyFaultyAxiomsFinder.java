package org.exquisite.protege.ui.component;

import org.exquisite.protege.ui.view.InputOntologyView;
import org.protege.editor.core.ui.util.AugmentedJTextField;
import org.protege.editor.core.ui.util.ComponentFactory;
import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.model.find.OWLEntityFinderPreferences;
import org.semanticweb.owlapi.model.OWLEntity;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.*;
import java.util.Set;
import java.util.TreeSet;

public class PossiblyFaultyAxiomsFinder extends AugmentedJTextField {

    private OWLEditorKit editorKit;

    private JList resultsList;

    private JWindow window;

    private InputOntologyView parent;


    public PossiblyFaultyAxiomsFinder(InputOntologyView parent, OWLEditorKit editorKit) {
        super(20, "Filter by entity");
        putClientProperty("JTextField.variant", "start");
        this.parent = parent;
        this.editorKit = editorKit;
        addKeyListener(new KeyAdapter() {
            public void keyReleased(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
                    closeResults();
                }
                if (e.getKeyCode() == KeyEvent.VK_ENTER ) {
                    if (resultsList.getSelectedValue() instanceof OWLEntity)
                        updateOWLEntity((OWLEntity) (resultsList.getSelectedValue()));
                    else
                        updateOWLEntity(null);
                }
            }


            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_UP) {
                    decrementListSelection();
                }
                if (e.getKeyCode() == KeyEvent.VK_DOWN) {
                    incrementListSelection();
                }
            }
        });
        getDocument().addDocumentListener(new DocumentListener() {
            public void changedUpdate(DocumentEvent e) {}

            public void insertUpdate(DocumentEvent e) {
                performFind();
            }


            public void removeUpdate(DocumentEvent e) {
                performFind();
            }
        });

        // tooltip and fixed cell size code
        resultsList = new JList();
        resultsList.setCellRenderer(editorKit.getWorkspace().createOWLCellRenderer());
        resultsList.addMouseListener(new MouseAdapter() {
            public void mouseReleased(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    if (resultsList.getSelectedValue() instanceof OWLEntity)
                        updateOWLEntity((OWLEntity) (resultsList.getSelectedValue()));
                    else
                        updateOWLEntity(null);
                }
            }
        });
    }

    private void updateOWLEntity(OWLEntity entity) {

        closeResults();
        //parent.setSelectedEntity (entitiy);
        parent.updateDisplayedPossiblyFaultyAxioms();
        if (entity != null)
            setText(editorKit.getModelManager().getOWLEntityRenderer().render(entity));
        else
            setText("");
    }

    private void incrementListSelection() {
        if (resultsList.getModel().getSize() > 0) {
            int selIndex = resultsList.getSelectedIndex();
            selIndex++;
            if (selIndex > resultsList.getModel().getSize() - 1) {
                selIndex = 0;
            }
            resultsList.setSelectedIndex(selIndex);
            resultsList.scrollRectToVisible(resultsList.getCellBounds(selIndex, selIndex));
        }
    }

    private void decrementListSelection() {
        if (resultsList.getModel().getSize() > 0) {
            int selIndex = resultsList.getSelectedIndex();
            selIndex--;
            if (selIndex < 0) {
                selIndex = resultsList.getModel().getSize() - 1;
            }
            resultsList.setSelectedIndex(selIndex);
            resultsList.scrollRectToVisible(resultsList.getCellBounds(selIndex, selIndex));
        }
    }

    private void closeResults() {
        getWindow().setVisible(false);

        /*DefaultListModel model = (DefaultListModel) resultsList.getModel();
        model.clear();*/

        resultsList.setListData(new Object []{});

    }

    private Timer timer = new Timer(400, new ActionListener() {
        public void actionPerformed(ActionEvent e) {
            executeFind();
            timer.stop();
        }
    });

    private void executeFind() {
        if (getText().trim().length() > 0) {
            Set<OWLEntity> results = editorKit.getModelManager().getOWLEntityFinder().getMatchingOWLEntities(getText());
            showResults(results);
        }
        else {
            closeResults();
        }
    }

    private void performFind() {
        timer.setDelay((int) OWLEntityFinderPreferences.getInstance().getSearchDelay());
        timer.restart();
    }

    private JWindow getWindow() {
        if (window == null) {
            Window w = (Window) SwingUtilities.getAncestorOfClass(Window.class, parent);
            window = new JWindow(w);
            window.setFocusableWindowState(false);
            JScrollPane sp = ComponentFactory.createScrollPane(resultsList);
            sp.setBorder(null);
            window.setContentPane(sp);
            addFocusListener(new FocusAdapter() {
                public void focusLost(FocusEvent e) {
                    window.setVisible(false);
                    /*DefaultListModel model = (DefaultListModel) resultsList.getModel();
                    model.clear();*/
                    resultsList.setListData(new Object []{});
                }
            });
            SwingUtilities.getRoot(this).addComponentListener(new ComponentAdapter() {
                public void componentMoved(ComponentEvent e) {
                    closeResults();
                }
            });
        }
        return window;
    }

    private void showResults(Set<OWLEntity> results) {
        JWindow window = getWindow();
        if (results.size() > 0) {
            Point pt = new Point(0, 0);
            SwingUtilities.convertPointToScreen(pt, this);
            window.setLocation(pt.x, pt.y + getHeight() + 2);
            window.setSize(getWidth(), 400);

            /*DefaultListModel model = (DefaultListModel) resultsList.getModel();
            model.clear();
            for (Object item : getSortedResults(results))
                model.addElement(item);*/

            resultsList.setListData(getSortedResults(results));
            window.setVisible(true);
            window.validate();
            resultsList.setSelectedIndex(0);
        }
        else {
            /*DefaultListModel model = (DefaultListModel) resultsList.getModel();
            model.clear();*/
            resultsList.setListData(new Object [0]);
        }
    }

    private Object[] getSortedResults(Set<OWLEntity> results) {
        TreeSet<OWLEntity> ts = new TreeSet<OWLEntity>(editorKit.getModelManager().getOWLObjectComparator());
        ts.addAll(results);
        int maxSize = 150;
        boolean tooMany = ts.size() > maxSize;
        Object[] arrayResults = new Object[tooMany ? maxSize : ts.size()];
        int i = 0;
        for (OWLEntity e : ts) {
            if (tooMany && i >= arrayResults.length - 1) {
                break;
            }
            arrayResults[i++] = e;
        }
        if (tooMany) {
            arrayResults[maxSize - 1] = "More...";
        }
        return arrayResults;
    }

}
