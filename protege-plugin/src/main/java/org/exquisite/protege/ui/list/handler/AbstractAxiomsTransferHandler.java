package org.exquisite.protege.ui.list.handler;

import org.exquisite.protege.Debugger;
import org.exquisite.protege.ui.list.BasicAxiomList;
import org.exquisite.protege.ui.list.item.AxiomListItem;
import org.semanticweb.owlapi.model.OWLLogicalAxiom;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * An abstract transfer handler for drag and drop support in the input ontology view.
 * Implementing classes have to manipulate the diagnoses model.
 *
 * @author wolfi
 */
public abstract class AbstractAxiomsTransferHandler extends TransferHandler {

    protected Logger logger = LoggerFactory.getLogger(AbstractAxiomsTransferHandler.class.getCanonicalName());

    /**
     * Instances of this transfer handler accept instances of AxiomListItem.
     */
    private final static DataFlavor dataFlavor = new DataFlavor(AxiomListItem.class, "AxiomListItem");

    /**
     * An instance of this debugger.
     */
    protected Debugger debugger;

    /**
     * Transfer handler constructor for drag and drop actions between correct and possibly faulty axioms.
     *
     * @param debugger the debugger instance.
     */
    AbstractAxiomsTransferHandler(Debugger debugger) {
        this.debugger = debugger;
    }

    // Import Methods

    @Override
    public boolean importData(TransferSupport support) {
        return support.isDrop() && support.isDataFlavorSupported(dataFlavor);

    }

    @Override
    public boolean canImport(TransferSupport support) {
        if (!support.isDataFlavorSupported(dataFlavor))
            return false;

        JList.DropLocation dl = (JList.DropLocation)support.getDropLocation();
        if (dl.getIndex() == -1)
            return false;

        try {
            List<AxiomListItem> selectedAxioms = (List)support.getTransferable().getTransferData(dataFlavor);

            final List<OWLLogicalAxiom> axioms = new ArrayList<>();
            for (AxiomListItem item : selectedAxioms)
                axioms.add(item.getAxiom());

            // a subclass has to implement the canImport() method
            return !axioms.isEmpty() && debugger.isSessionStopped() && canImport(axioms);
        } catch (UnsupportedFlavorException | IOException e) {
            logger.error(e.getMessage(), e);
        }

        return false;
    }

    // Export Methods

    @Override
    public int getSourceActions(JComponent c) {
        return MOVE;
    }

    @Override
    protected Transferable createTransferable(JComponent c) {
        BasicAxiomList list = (BasicAxiomList)c;
        final List<AxiomListItem> axioms = list.getSelectedValuesList();

        return new Transferable() {
            @Override
            public DataFlavor[] getTransferDataFlavors() {
                return new DataFlavor[] {dataFlavor};
            }

            @Override
            public boolean isDataFlavorSupported(DataFlavor flavor) {
                return flavor.equals(dataFlavor);
            }

            @Override
            public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException, IOException {
                return axioms;
            }
        };
    }

    @Override
    protected void exportDone(JComponent source, Transferable data, int action) {
        if (action == MOVE) {
            try {
                List<AxiomListItem> selectedAxioms = (List)data.getTransferData(dataFlavor);
                moveSelectedAxioms(selectedAxioms);
            } catch (UnsupportedFlavorException | IOException e) {
                logger.error(e.getMessage(), e);
            }
        }
    }

    /**
     * A sublass has to implement the transfer operation of the selected axioms.
     * This method is only called if {@link #canImport(List)} returns <code>true</code>.
     *
     * @param selectedAxioms A list of selected axioms.
     */
    protected abstract void moveSelectedAxioms(List<AxiomListItem> selectedAxioms);

    /**
     * A subclass has to check whether it is possible to import the selected axioms into it's part (either background
     * or knowledge base) of the diagnosis model or not.
     *
     * @param selectedAxioms The set of selected axioms.
     * @return <code>true</code> if an import is possible.
     */
    protected abstract boolean canImport(List<OWLLogicalAxiom> selectedAxioms);

}
