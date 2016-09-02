package org.exquisite.protege.model.error;

import javax.swing.*;
import java.awt.*;

import static org.exquisite.protege.model.OntologyDebugger.ErrorStatus;

public abstract class AbstractErrorHandler {

     public abstract void errorHappened(ErrorStatus error, Exception ex);

     public void errorHappened(ErrorStatus error) {
          errorHappened(error, null);
     }

     protected void showMessageDialog(Component parentComponent, Object message, String title, int messageType, Exception ex) {
          JOptionPane.showMessageDialog(parentComponent, message + ((ex!=null) ? "\n" + ex.getMessage():""), title, messageType);
     }

}
