package org.exquisite.core.query.scoring;

import org.exquisite.core.model.Diagnosis;
import org.exquisite.core.query.Query;

import java.math.BigDecimal;
import java.util.Set;

/**
 * Created by IntelliJ IDEA.
 * User: kostya
 * Date: 01.06.11
 * Time: 08:44
 * To change this template use File | Settings | File Templates.
 */
public interface QuerySelection<Formula> {

    BigDecimal getScore(Query<Formula> query);

    void normalize(Set<Diagnosis<Formula>> diagnosises);
}