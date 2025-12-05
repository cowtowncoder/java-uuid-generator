package com.fasterxml.uuid;

import java.util.UUID;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class SimpleGenerationTest
{
    @Test
    public void testIssue5() throws Exception
    {
        UUID uuid = Generators.randomBasedGenerator().generate();
        assertNotNull(uuid);

        // but second time's the charm...
        uuid = Generators.randomBasedGenerator().generate();
        assertNotNull(uuid);
    }
}
