package com.fasterxml.uuid;

import java.util.UUID;

import junit.framework.TestCase;

public class SimpleGenerationTest extends TestCase
{
    public void testIssue5() throws Exception
    {
        UUID uuid = Generators.randomBasedGenerator().generate();
        assertNotNull(uuid);

        // but second time's the charm...
        uuid = Generators.randomBasedGenerator().generate();
        assertNotNull(uuid);
    }
}
