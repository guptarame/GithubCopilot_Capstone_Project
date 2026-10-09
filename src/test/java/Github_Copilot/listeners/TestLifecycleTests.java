package Github_Copilot.listeners;

import org.junit.jupiter.api.Test;
import org.opentest4j.TestAbortedException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;

class TestLifecycleTests {

    @Test
    void classifiesPassFailureAndAbortedTestsConsistently() {
        assertEquals(TestOutcome.PASSED, TestOutcome.from(Optional.empty()));
        assertEquals(TestOutcome.FAILED, TestOutcome.from(Optional.of(new AssertionError())));
        assertEquals(TestOutcome.SKIPPED, TestOutcome.from(Optional.of(new TestAbortedException("skipped"))));
    }
}