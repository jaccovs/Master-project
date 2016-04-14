package org.exquisite.evals.conferences.ecai2016;

import org.exquisite.core.engines.AbstractDiagnosisEngine;
import org.exquisite.core.query.querycomputation.IQueryComputation;
import org.exquisite.core.query.querycomputation.SimpleNaiveQueryComputation;
import org.exquisite.core.query.querycomputation.heuristic.HeuristicConfiguration;
import org.exquisite.core.query.querycomputation.heuristic.HeuristicQueryComputation;
import org.exquisite.core.query.querycomputation.heuristic.partitionmeasures.EntropyBasedMeasure;
import org.exquisite.core.query.querycomputation.heuristic.partitionmeasures.RiskOptimizationMeasure;
import org.exquisite.core.query.querycomputation.heuristic.partitionmeasures.SplitInHalfMeasure;
import org.exquisite.core.query.scoring.MinScoreQSS;
import org.exquisite.core.query.scoring.RIOQSS;
import org.exquisite.core.query.scoring.SplitInHalf1QSS;
import org.semanticweb.owlapi.model.OWLLogicalAxiom;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

/**
 * @author wolfi
 */
class Configuration {
    private static String[] ontologies = {"ontologies/Economy-SDA.owl", "ontologies/Transportation-SDA.owl", "ontologies/University.owl"};

    private static Integer[] diagnoseSizes = {5, 10, 12, 14, 16, 20, 25};

    private static List<IQueryComputation<OWLLogicalAxiom>> queryComputers;

    // -----------------------------------------------------------------------------------------------------------------

    static Collection<String> getOntologies() {
        return Arrays.asList(ontologies);
    }

    static Collection<Integer> getDiagnoseSizes() {
        return Arrays.asList(diagnoseSizes);
    }

    static Collection<IQueryComputation<OWLLogicalAxiom>> getQueryComputers() {
        return queryComputers;
    }

    static Integer getNrOfQueryComputers() {
        return 16;
    };

    static Integer getIterations() {
        return 3;
    }

    // -----------------------------------------------------------------------------------------------------------------

    static void createQueryComputers(AbstractDiagnosisEngine<OWLLogicalAxiom> e) {
        queryComputers = new ArrayList<>();
        HeuristicConfiguration<OWLLogicalAxiom> c;

        // HeuristicQueryComputation with SPL(0.5)
        c = new HeuristicConfiguration<>(e, new SplitInHalfMeasure<>(new BigDecimal("0.5")));
        queryComputers.add(new HeuristicQueryComputation<>(c));

        // HeuristicQueryComputation with SPL(1.5)
        c = new HeuristicConfiguration<>(e, new SplitInHalfMeasure<>(new BigDecimal("1.5")));
        queryComputers.add(new HeuristicQueryComputation<>(c));

        // HeuristicQueryComputation with ENT(0)
        c = new HeuristicConfiguration<>(e, new EntropyBasedMeasure<>(new BigDecimal("0")));
        queryComputers.add(new HeuristicQueryComputation<>(c));

        // HeuristicQueryComputation with ENT(0.025)
        c = new HeuristicConfiguration<>(e, new EntropyBasedMeasure<>(new BigDecimal("0.025")));
        queryComputers.add(new HeuristicQueryComputation<>(c));

        // HeuristicQueryComputation with ENT(0.05)
        c = new HeuristicConfiguration<>(e, new EntropyBasedMeasure<>(new BigDecimal("0.05")));
        queryComputers.add(new HeuristicQueryComputation<>(c));

        // HeuristicQueryComputation with ENT(0.1)
        c = new HeuristicConfiguration<>(e, new EntropyBasedMeasure<>(new BigDecimal("0.1")));
        queryComputers.add(new HeuristicQueryComputation<>(c));

        // HeuristicQueryComputation with RIO(0.05,0,0.1)
        c = new HeuristicConfiguration<>(e, new RiskOptimizationMeasure<>(new BigDecimal("0.05"), BigDecimal.ZERO, new BigDecimal("0.1")));
        queryComputers.add(new HeuristicQueryComputation<>(c));

        // HeuristicQueryComputation with RIO(0.05,0,0.2)
        c = new HeuristicConfiguration<>(e, new RiskOptimizationMeasure<>(new BigDecimal("0.05"), BigDecimal.ZERO, new BigDecimal("0.2")));
        queryComputers.add(new HeuristicQueryComputation<>(c));

        // HeuristicQueryComputation with RIO(0.05,0,0.3)
        c = new HeuristicConfiguration<>(e, new RiskOptimizationMeasure<>(new BigDecimal("0.05"), BigDecimal.ZERO, new BigDecimal("0.3")));
        queryComputers.add(new HeuristicQueryComputation<>(c));

        // HeuristicQueryComputation with RIO(0.05,0,0.4)
        c = new HeuristicConfiguration<>(e, new RiskOptimizationMeasure<>(new BigDecimal("0.05"), BigDecimal.ZERO, new BigDecimal("0.4")));
        queryComputers.add(new HeuristicQueryComputation<>(c));

        // SimpleNaive with SPL
        queryComputers.add(new SimpleNaiveQueryComputation<>(e, new SplitInHalf1QSS<>()));

        // SimpleNaive with ENT
        queryComputers.add(new SimpleNaiveQueryComputation<>(e, new MinScoreQSS<>()));

        // SimpleNaive with RIO(0.1)
        queryComputers.add(new SimpleNaiveQueryComputation<>(e, new RIOQSS<>(new BigDecimal("0.1"))));

        // SimpleNaive with RIO(0.2)
        queryComputers.add(new SimpleNaiveQueryComputation<>(e, new RIOQSS<>(new BigDecimal("0.2"))));

        // SimpleNaive with RIO(0.3)
        queryComputers.add(new SimpleNaiveQueryComputation<>(e, new RIOQSS<>(new BigDecimal("0.3"))));

        // SimpleNaive with RIO(0.4)
        queryComputers.add(new SimpleNaiveQueryComputation<>(e, new RIOQSS<>(new BigDecimal("0.4"))));

    }
}
