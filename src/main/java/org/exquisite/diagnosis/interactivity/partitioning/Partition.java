package org.exquisite.diagnosis.interactivity.partitioning;

import org.exquisite.diagnosis.models.Diagnosis;
import org.exquisite.diagnosis.models.DiagnosisModel;
import org.exquisite.tools.Utilities;

import java.math.BigDecimal;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * A partition of constraints that splits the diagnoses into the 3 parts dx, dnx, and dz.
 *
 * @author Schmitz
 */
public class Partition<Formula> {
    /**
     * Diagnoses that are supported by the partition
     */
    public Set<Diagnosis<Formula>> dx = new LinkedHashSet<Diagnosis<Formula>>();
    /**
     * Diagnoses that are not supported by the partition
     */
    public Set<Diagnosis<Formula>> dnx = new LinkedHashSet<Diagnosis<Formula>>();
    /**
     * Diagnoses that are unaffected by the partition
     */
    public Set<Diagnosis<Formula>> dz = new LinkedHashSet<Diagnosis<Formula>>();
    public Set<Formula> partition;
    public BigDecimal score = BigDecimal.valueOf(Double.MAX_VALUE);
    public BigDecimal difference = new BigDecimal(Double.MAX_VALUE);
    public boolean isVerified = false;
    DiagnosisModel<Formula> model;

    public Partition(DiagnosisModel<Formula> model) {
        this.model = model;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Partition partition1 = (Partition) o;

        if (!dnx.equals(partition1.dnx)) return false;
        if (!dx.equals(partition1.dx)) return false;
        if (!dz.equals(partition1.dz)) return false;
        //if (!partition.equals(partition1.partition)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = dx.hashCode();
        result = 31 * result + dnx.hashCode();
        result = 31 * result + dz.hashCode();
        //result = 31 * result + partition.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return Utilities.printConstraintListOrderedByName(partition, model);
    }
}
