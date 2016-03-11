package org.exquisite.core.query;

import java.util.Set;

/**
 * Copy of QueryAnswering and slightly adapted for requirements of NewQC.
 *
 * Created by pr8 and wolfi on 10.03.2015.
 */
public interface NewQueryAnswering<F> {

    Answer<F> getAnswer(Set<F> query);
}
