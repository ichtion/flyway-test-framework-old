package org.flywaydb.test.runner;

import org.flywaydb.test.AfterMigration;
import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;

public class MigrateRule implements TestRule {
    private final FlywayTest flywayTest;

    public MigrateRule(FlywayTest flywayTest) {
        this.flywayTest = flywayTest;
    }

    @Override
    public Statement apply(final Statement base, final Description description) {
        return new Statement() {
            @Override
            public void evaluate() throws Throwable {
                if (description.getAnnotation(AfterMigration.class) != null) {
                    flywayTest.migrate();
                }
                base.evaluate();
            }
        };
    }
}
