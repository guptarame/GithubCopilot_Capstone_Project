package Github_Copilot.listeners;

import org.opentest4j.TestAbortedException;

import java.util.Optional;

enum TestOutcome {
    PASSED,
    FAILED,
    SKIPPED;

    static TestOutcome from(Optional<Throwable> failure) {
        if (failure.isEmpty()) {
            return PASSED;
        }
        return failure.get() instanceof TestAbortedException ? SKIPPED : FAILED;
    }
}