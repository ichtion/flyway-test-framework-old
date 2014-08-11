package org.flywaydb.test.runner;

import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;

public class CleanDbClassRule implements TestRule {

    private final FlywayTest flywayTest;

    public CleanDbClassRule(FlywayTest flywayTest) {
        this.flywayTest = flywayTest;
    }

    @Override
    public Statement apply(final Statement base, Description description) {
        return new Statement() {

            @Override
            public void evaluate() throws Throwable {
                if (flywayTest.isCleanDb()) {
                    flywayTest.getFlyway().clean();
                }
                base.evaluate();
            }
        };

    }
}
