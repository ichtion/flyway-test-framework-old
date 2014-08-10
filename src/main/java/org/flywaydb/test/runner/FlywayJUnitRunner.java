package org.flywaydb.test.runner;

import org.flywaydb.core.Flyway;
import org.flywaydb.test.Configuration;
import org.flywaydb.test.util.PropertiesUtils;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.junit.runners.model.FrameworkField;
import org.junit.runners.model.InitializationError;

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

    private Flyway flyway;

    public FlywayJUnitRunner(Class<?> clazz) throws InitializationError {
        super(clazz);
        flyway = createFlyway(clazz);
    }

    private Flyway createFlyway(Class<?> clazz) {
        Configuration configuration = clazz.getAnnotation(Configuration.class);
        Flyway flyway = new Flyway();
        flyway.configure(PropertiesUtils.load(configuration.location()));
        return flyway;
    }

    @Override
    protected Object createTest() throws Exception {
        Object testInstance = super.createTest();
        List<FrameworkField> annotatedFields = getTestClass().getAnnotatedFields(Inject.class);
        for (FrameworkField annotatedField : annotatedFields) {
            if (annotatedField.getType().equals(Flyway.class)) {
                Field field = annotatedField.getField();
                field.setAccessible(true);
                field.set(testInstance, flyway);
            }
        }
        return testInstance;
    }
}
