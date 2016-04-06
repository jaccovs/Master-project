package org.exquisite.core.solver;

import org.exquisite.core.model.DiagnosisModel;

import java.util.*;
import java.util.stream.Collectors;

/**
 * A very simple forward chaining SAT checker for Horn propositional KBs. This SAT solver is
 * implemented for testing purposes only.
 */
public class SimpleFCSat extends AbstractSolver<FCClause> {

    private final List<FCClause> clauses = new ArrayList<>();
    private Set<FCClause> derived = new HashSet<>();
    private Boolean isConsistent = null;


    public SimpleFCSat(DiagnosisModel<FCClause> model) {
        super(model);
    }

    @Override
    protected Set<FCClause> calculateEntailments() {
        if (isConsistent())
            return new HashSet<>(this.derived);
        throw new RuntimeException("KB is inconsistent!");
    }

    @Override
    protected boolean isEntailed(Collection<FCClause> entailments) {
        return isConsistent() &&
                entailments.parallelStream().allMatch(cl ->
                        this.derived.stream().map(ucl -> ucl.iterator().next())
                                .anyMatch(cl::contains));
    }

    @Override
    protected FCClause negate(FCClause example) {
        throw new RuntimeException("Negation is not supported");
    }

    @Override
    protected boolean supportsNegation() {
        return false;
    }

    @Override
    protected void sync(Set<FCClause> addFormulas, Set<FCClause> removeFormulas) {
        this.derived = null;
        isConsistent = null;
        if (addFormulas.stream().anyMatch(removeFormulas::contains))
            throw new RuntimeException("add formulas have intersection with remove!");
        if (removeFormulas.stream().anyMatch(addFormulas::contains))
            throw new RuntimeException("remove formulas have intersection with add!");

        this.clauses.addAll(addFormulas);
        this.clauses.removeAll(removeFormulas);
    }

    @Override
    protected boolean isConsistent() {
        if (isConsistent != null) return isConsistent;

        if (clauses.isEmpty()) return true;

        this.derived = null;
        int maxSymbol = clauses.stream().max(Comparator.comparing(cl -> cl.maxSymbol)).get().maxSymbol;
        ArrayList<Integer> symbols = new ArrayList<>(Collections.nCopies(maxSymbol, 0));
        List<FCClause> cls = clauses.stream().map(FCClause::new).collect(Collectors.toList());
        while (true) {
            cls.parallelStream().filter(FCClause::isUnit).map(cl -> cl.iterator().next()).forEach(
                    symbol -> {
                        int index = Math.abs(symbol) - 1;
                        if (symbols.get(index) - symbol == 0) {
                            isConsistent = false;
                        }
                        symbols.set(index, -symbol);
                    });

            if (isConsistent != null && !isConsistent) return false;

            // fixpoint
            if (!cls.removeIf(FCClause::isUnit)) {
                isConsistent = true;
                this.derived = symbols.parallelStream().filter(smb -> smb != 0).map(smb -> new FCClause(-smb)).collect(Collectors.toSet());
                return true;
            } else {
                // propagate new assignments
                cls.parallelStream().forEach(cl -> cl.removeAll(symbols));
                if (cls.stream().anyMatch(FCClause::isEmpty)) {
                    isConsistent = false;
                    return false;
                }
            }
        }
    }
}