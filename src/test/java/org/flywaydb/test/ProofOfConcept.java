package org.flywaydb.test;

import org.flywaydb.core.Flyway;
import org.flywaydb.test.runner.FlywayJUnitRunner;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import javax.inject.Inject;

import static com.google.common.collect.ImmutableMap.of;
import static org.assertj.core.api.Assertions.assertThat;
import static org.flywaydb.test.util.TestUtils.id;

@RunWith(FlywayJUnitRunner.class)
@Migration("2")
@Configuration(location = "/flyway.properties")
@CleanDb
public class ProofOfConcept {

    @Inject
    private Flyway flyway;
    private NamedParameterJdbcTemplate jdbcTemplate;
    private static final String ID = id();
    private static final String NAME = "name";

    @Before
    public void before() {
        jdbcTemplate = new NamedParameterJdbcTemplate(flyway.getDataSource());
    }

    @Ignore
    @Test
    @BeforeMigration
    public void insertEmployee() {
        jdbcTemplate.update("insert into employee values(:id, :name)", of("id", ID, "name", NAME));
    }

    @Ignore
    @Test
    @AfterMigration
    public void assertNameColumnWasRenamedToFirstname() {
        String firstname = jdbcTemplate.queryForObject("select firstname from employee where id=:id", of("id", ID), String.class);

        assertThat(firstname).isEqualTo(NAME);
    }
}
