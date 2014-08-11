package org.flywaydb.test.util;

import java.util.UUID;

public class TestUtils {

    public static String id() {
        return UUID.randomUUID().toString();
    }
}
