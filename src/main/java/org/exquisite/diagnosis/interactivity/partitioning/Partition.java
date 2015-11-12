package org.exquisite.diagnosis.interactivity.partitioning;

import java.math.BigDecimal;
import java.util.LinkedHashSet;
import java.util.Set;

import org.exquisite.diagnosis.models.Diagnosis;
import org.exquisite.diagnosis.models.DiagnosisModel;
import org.exquisite.tools.Utilities;

import choco.kernel.model.constraints.Constraint;

/**
 * A partition of constraints that splits the diagnoses into the 3 parts dx, dnx, and dz.
 * 
 * @author Schmitz
 *
 */
public class Partition {
	DiagnosisModel model;
	
	/**
	 * Diagnoses that are supported by the partition
	 */
    public Set<Diagnosis> dx = new LinkedHashSet<Diagnosis>();
    
    /**
     * Diagnoses that are not supported by the partition
     */
    public Set<Diagnosis> dnx = new LinkedHashSet<Diagnosis>();
    
    /**
     * Diagnoses that are unaffected by the partition
     */
    public Set<Diagnosis> dz = new LinkedHashSet<Diagnosis>();

    public Set<Constraint> partition;
    public BigDecimal score = BigDecimal.valueOf(Double.MAX_VALUE);
    public BigDecimal difference = new BigDecimal(Double.MAX_VALUE);
    public boolean isVerified = false;

    public Partition(DiagnosisModel model) {
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
