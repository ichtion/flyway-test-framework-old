package org.flywaydb.test.runner;

import org.flywaydb.test.BeforeMigration;
import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;

public class MigrateToPreviousVersionRule implements TestRule {
    private final FlywayTest flywayTest;

    public MigrateToPreviousVersionRule(FlywayTest flywayTest) {
        this.flywayTest = flywayTest;
    }

    @Override
    public Statement apply(final Statement base, final Description description) {
        return new Statement() {
            @Override
            public void evaluate() throws Throwable {
                if (description.getAnnotation(BeforeMigration.class) != null) {
                    flywayTest.migrateToPreviousVersion();
                }
                base.evaluate();
            }
        };
    }
}
