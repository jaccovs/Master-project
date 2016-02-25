package org.exquisite.datamodel;

import org.exquisite.core.measurements.MeasurementManager;
import org.exquisite.data.DiagnosisConfiguration;
import org.exquisite.diagnosis.models.ConstraintsDiagnosisModel;

/**
 * General implementation of the session object
 */
public class DiagnosisModel<T> {
    private ConstraintsDiagnosisModel<T> diagnosisModel;
    private DiagnosisConfiguration configuration;


    public DiagnosisModel() {
        this(new ConstraintsDiagnosisModel<>(), new DiagnosisConfiguration());
    }


    public DiagnosisModel(ConstraintsDiagnosisModel<T> model) {
        this(model, new DiagnosisConfiguration());
    }

    public DiagnosisModel(ConstraintsDiagnosisModel<T> model, DiagnosisConfiguration configuration) {
        MeasurementManager.reset();
        this.diagnosisModel = model;
        this.configuration = configuration;
    }

    public org.exquisite.core.model.DiagnosisModel<T> getDiagnosisModel() {
        return diagnosisModel;
    }

    public DiagnosisConfiguration getConfiguration() {
        return configuration;
    }
}
