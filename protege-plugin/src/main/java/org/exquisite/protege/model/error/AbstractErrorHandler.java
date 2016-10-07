package org.exquisite.protege.model.error;

import org.exquisite.protege.ui.dialog.DebuggingDialog;

import java.awt.*;

import static org.exquisite.protege.model.OntologyDebugger.ErrorStatus;

public abstract class AbstractErrorHandler {

     public abstract void errorHappened(ErrorStatus error, Exception ex);

     public void errorHappened(ErrorStatus error) {
          errorHappened(error, null);
     }

     protected void showErrorDialog(Component parentComponent, String message, String title, int messageType, Exception ex) {
          DebuggingDialog.showErrorDialog(title, message, ex);
     }

}
