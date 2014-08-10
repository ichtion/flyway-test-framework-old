package org.flywaydb.test.runner;

import org.flywaydb.core.Flyway;
import org.flywaydb.core.api.MigrationInfo;
import org.flywaydb.core.api.MigrationInfoService;
import org.flywaydb.core.api.MigrationVersion;
import org.flywaydb.test.CleanDb;
import org.flywaydb.test.Configuration;
import org.flywaydb.test.util.PropertiesUtils;

public class FlywayTest {

    private enum State {
        INITIALIZED,
        MIGRATED_TO_PREVIOUS_VERSION,
        MIGRATED
    }

    private final MigrationVersion version;

    private final Flyway flyway;
    private State state = State.INITIALIZED;
    private boolean cleanDb = false;

    public FlywayTest(Flyway flyway, MigrationVersion version) {
        this.flyway = flyway;
        this.version = version;
    }

    public Flyway getFlyway() {
        return flyway;
    }

    public boolean isCleanDb() {
        return cleanDb;
    }

    public boolean migrateToPreviousVersion() {
        // todo: clean if needed
        if (state == State.MIGRATED) {
            throw new IllegalStateException();
        }
        if (state == State.INITIALIZED) {
            flyway.setTarget(getPreviousVersion());
            flyway.migrate();
            state = State.MIGRATED_TO_PREVIOUS_VERSION;
            return true;
        }
        return false;
    }

    public boolean migrate() {
        // todo: clean if needed
        if (state != State.MIGRATED) {
            flyway.setTarget(version);
            flyway.migrate();
            state = State.MIGRATED;
            return true;
        }
        return false;
    }

    private MigrationVersion getPreviousVersion() {
        MigrationVersion previous = MigrationVersion.EMPTY;
        for (MigrationInfo migrationInfo : flyway.info().all()) {
            if (migrationInfo.getVersion().equals(version)) {
                return previous;
            } else {
                previous = migrationInfo.getVersion();
            }
        }
        throw new IllegalStateException("previous version not found");
    }

    public static FlywayTest create(Class<?> testClass) {
        Configuration configuration = testClass.getAnnotation(Configuration.class);
        Flyway flyway = new Flyway();
        flyway.configure(PropertiesUtils.load(configuration.location()));
        String versionAsString = testClass.getAnnotation(org.flywaydb.test.MigrationVersion.class).value();
        MigrationVersion version = MigrationVersion.fromVersion(versionAsString);
        if (!hasMigration(flyway.info(), version)) {
            throw new IllegalArgumentException("not existing migration " + versionAsString);
        }
        FlywayTest flywayTest = new FlywayTest(flyway, version);
        if (testClass.getAnnotation(CleanDb.class) != null) {
            flywayTest.cleanDb = true;
        }
        return flywayTest;
    }

    private static boolean hasMigration(MigrationInfoService migrationInfoService, MigrationVersion version) {
        for (MigrationInfo migrationInfo : migrationInfoService.all()) {
            if (migrationInfo.getVersion().equals(version)) {
                return true;
            }
        }
        return false;
    }
}
