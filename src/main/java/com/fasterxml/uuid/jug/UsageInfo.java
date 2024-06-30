package com.fasterxml.uuid.jug;

import com.fasterxml.uuid.Jug;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class UsageInfo
{

    private static final List<String> usageMessages = new ArrayList<>();

    static {
        usageMessages.add("Usage: java " + Jug.class.getName() + " [options] type");
        usageMessages.add("Where options are:");
        usageMessages.add("  --count / -c <number>: will generate <number> UUIDs (default: 1)");
        usageMessages.add("  --ethernet-address / -e <ether-address>: defines the ethernet address");
        usageMessages.add("    (in xx:xx:xx:xx:xx:xx notation, usually obtained using 'ifconfig' etc)");
        usageMessages.add("    to use with time-based UUID generation");
        usageMessages.add("  --help / -h: lists the usage (ie. what you see now)");
        usageMessages.add("  --name / -n: specifies");
        usageMessages.add("     o name for name-based UUID generation");
        usageMessages.add("     o 'information' part of tag-URI for tag-URI UUID generation");
        usageMessages.add("  --namespace / -s: specifies");
        usageMessages.add("    o the namespace (DNS or URL) for name-based UUID generation");
        usageMessages.add("    o 'authority' part of tag-URI for tag-URI UUID generation;");
        usageMessages.add("        (fully-qualified domain name, email address)");
        usageMessages.add("  --performance / -p: measure time it takes to generate UUID(s).");
        usageMessages.add("    [note that UUIDs are not printed out unless 'verbose' is also specified]");
        usageMessages.add("  --verbose / -v: lists additional information about UUID generation\n    (by default only UUIDs are printed out (to make it usable in scripts)");
        usageMessages.add("And type is one of:");
        usageMessages.add("  time-based / t: generate UUID based on current time and optional\n    location information (defined with -e option)");
        usageMessages.add("  random-based / r: generate UUID based on the default secure random number generator");
        usageMessages.add("  name-based / n: generate UUID based on MD5 hash of given String ('name')");
        usageMessages.add("  reordered-time-based / o: generate UUID based on current time and optional\n    location information (defined with -e option)");
        usageMessages.add("  epoch-based / e: generate UUID based on current time (as 'epoch') and random number");
    }

    /**
     * Uses default print mechanism
     */
    public void print() {
        print(System.err::println);
    }

    /**
     * Uses specific print mechanism
     *
     * @param printer specific printer for messages
     */
    public void print(Consumer<String> printer) {
        usageMessages.forEach(printer);
    }
}
