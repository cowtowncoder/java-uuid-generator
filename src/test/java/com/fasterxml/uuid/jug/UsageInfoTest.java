package com.fasterxml.uuid.jug;

import org.junit.Before;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static org.junit.Assert.assertEquals;

public class UsageInfoTest {

    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private final ByteArrayOutputStream errContent = new ByteArrayOutputStream();

    private final String expectedMessage = "Usage: java com.fasterxml.uuid.Jug [options] type\n" +
            "Where options are:\n" +
            "  --count / -c <number>: will generate <number> UUIDs (default: 1)\n" +
            "  --ethernet-address / -e <ether-address>: defines the ethernet address\n" +
            "    (in xx:xx:xx:xx:xx:xx notation, usually obtained using 'ifconfig' etc)\n" +
            "    to use with time-based UUID generation\n" +
            "  --help / -h: lists the usage (ie. what you see now)\n" +
            "  --name / -n: specifies\n" +
            "     o name for name-based UUID generation\n" +
            "     o 'information' part of tag-URI for tag-URI UUID generation\n" +
            "  --namespace / -s: specifies\n" +
            "    o the namespace (DNS or URL) for name-based UUID generation\n" +
            "    o 'authority' part of tag-URI for tag-URI UUID generation;\n" +
            "        (fully-qualified domain name, email address)\n" +
            "  --performance / -p: measure time it takes to generate UUID(s).\n" +
            "    [note that UUIDs are not printed out unless 'verbose' is also specified]\n" +
            "  --verbose / -v: lists additional information about UUID generation\n" +
            "    (by default only UUIDs are printed out (to make it usable in scripts)\n" +
            "And type is one of:\n" +
            "  time-based / t: generate UUID based on current time and optional\n" +
            "    location information (defined with -e option)\n" +
            "  random-based / r: generate UUID based on the default secure random number generator\n" +
            "  name-based / n: generate UUID based on MD5 hash of given String ('name')\n" +
            "  reordered-time-based / o: generate UUID based on current time and optional\n" +
            "    location information (defined with -e option)\n" +
            "  epoch-based / e: generate UUID based on current time (as 'epoch') and random number" +
            "\n";

    @Before
    public void setup() {
        PrintStream stubbedStream = new PrintStream(outContent);
        System.setOut(stubbedStream);

        PrintStream stubbedErrStream = new PrintStream(errContent);
        System.setErr(stubbedErrStream);
    }

    /**
     * This test contains trailed '\n' symbol.
     */
    @Test
    public void validateMessage_defaultStdError() {
        new UsageInfo().print();

        String actual = errContent.toString();

        assertEquals(expectedMessage, actual);
    }

    @Test
    public void validateMessage_defaultStdOut() {
        new UsageInfo().print(System.out::println);

        String actual = outContent.toString();

        assertEquals(expectedMessage, actual);
    }
}
