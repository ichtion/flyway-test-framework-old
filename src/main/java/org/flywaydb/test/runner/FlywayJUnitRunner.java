package org.flywaydb.test.runner;

import org.flywaydb.core.Flyway;
import org.junit.rules.TestRule;
import org.junit.runner.manipulation.Sorter;
import org.junit.runner.notification.RunNotifier;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.junit.runners.model.FrameworkField;
import org.junit.runners.model.InitializationError;
import org.junit.runners.model.Statement;

import javax.inject.Inject;
import java.lang.reflect.Field;
import java.util.List;

// TODO:
// - verify if version in @Migration is valid
// - clean db before running class when @CleanDb
// - migrate to previous version before running class
// - override children method to return: <all before migration tests>, <migration statement>, <all after migration tests>
// - migration should be executed only when there is at least one after migration test
// - exception handling for @Inject annotation
public class FlywayJUnitRunner extends BlockJUnit4ClassRunner {

    private FlywayTest flywayTest;

    public FlywayJUnitRunner(Class<?> clazz) throws InitializationError {
        super(clazz);
        flywayTest = FlywayTest.create(clazz);
    }

    protected Statement childrenInvoker(final RunNotifier notifier) {
        // todo: improve
        sort(new Sorter(new MigrationTestComparator()));
        return super.childrenInvoker(notifier);
    }

    // todo: improve
    @Override
    protected Object createTest() throws Exception {
        Object testInstance = super.createTest();
        List<FrameworkField> annotatedFields = getTestClass().getAnnotatedFields(Inject.class);
        for (FrameworkField annotatedField : annotatedFields) {
            if (annotatedField.getType().equals(Flyway.class)) {
                Field field = annotatedField.getField();
                field.setAccessible(true);
                field.set(testInstance, flywayTest.getFlyway());
            }
        }
        return testInstance;
    }

    @Override
    protected List<TestRule> classRules() {
        List<TestRule> classRules = super.classRules();
        classRules.add(new CleanDbClassRule(flywayTest));
        return classRules;
    }

    @Override
    protected List<TestRule> getTestRules(Object target) {
        List<TestRule> testRules = super.getTestRules(target);
        testRules.add(new MigrateToPreviousVersionRule(flywayTest));
        testRules.add(new MigrateRule(flywayTest));
        return testRules;
    }
}
