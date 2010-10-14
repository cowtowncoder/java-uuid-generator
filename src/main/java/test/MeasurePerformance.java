package test;

import java.util.UUID;

import com.fasterxml.uuid.*;
import com.fasterxml.uuid.impl.NameBasedGenerator;

/**
 * Simple micro-benchmark for evaluating performance of various UUID generation
 * techniques, including JDK's method as well as JUG's variants.
 *<p>
 * Notes: for name-based variant we will pass plain Strings, assuming this is the
 * most common use case; even though it is possible to also pass raw byte arrays.
 * JDK and Jug implementations have similar performance so this only changes
 * relative speeds of name- vs time-based variants.
 *
 * @since 3.1
 */
public class MeasurePerformance
{
    // Let's generate quarter million UUIDs per test
    
    private static final int ROUNDS = 250;
    private static final int COUNT = 1000;

    private final static UUID NAMESPACE = NameBasedGenerator.NAMESPACE_DNS;
    
    // also: let's just use a single name for name-based, to avoid extra overhead:
    final String NAME = "http://www.cowtowncoder.com/blog/blog.html";
    final byte[] NAME_BYTES;

    public MeasurePerformance() throws java.io.IOException
    {
        NAME_BYTES = NAME.getBytes("UTF-8");
    }
    
    public void test() throws Exception
    {
        int i = 0;

        final UUID[] uuids = new UUID[COUNT];

        // can either use bogus address; or local one, no difference perf-wise
        EthernetAddress nic = EthernetAddress.fromInterface();

        // Whether to include namespace? Depends on whether we compare with JDK (which does not)
//        UUID namespaceForNamed = NAMESPACE;
        UUID namespaceForNamed = null;

        final NoArgGenerator secureRandomGen = Generators.randomBasedGenerator();
        final NoArgGenerator utilRandomGen = Generators.randomBasedGenerator(new java.util.Random(123));
        final NoArgGenerator timeGen = Generators.timeBasedGenerator(nic);
        final StringArgGenerator nameGen = Generators.nameBasedGenerator(namespaceForNamed);
        
        while (true) {
            try {  Thread.sleep(100L); } catch (InterruptedException ie) { }
            int round = (i++ % 6);
    
            long curr = System.currentTimeMillis();
            String msg;
            boolean lf = (round == 0);
    
            switch (round) {
    
            case 0:
                msg = "JDK, random";
                testJDK(uuids, ROUNDS);
                break;

            case 1:
                msg = "JDK, name";
                testJDKNames(uuids, ROUNDS);
                break;
                
            case 2:
                msg = "Jug, SecureRandom";
                testNoArgs(uuids, ROUNDS, secureRandomGen);
                break;

            case 3:
                msg = "Jug, java.util.Random";
                testNoArgs(uuids, ROUNDS, utilRandomGen);
                break;
                
            case 4:
                msg = "Jug, time-based";
                testNoArgs(uuids, ROUNDS, timeGen);
                break;

            case 5:
                msg = "Jug, name-based";
                testStringArg(uuids, ROUNDS, nameGen);
                break;
                
            default:
                throw new Error("Internal error");
            }

            curr = System.currentTimeMillis() - curr;
            if (lf) {
                System.out.println();
            }
            System.out.println("Test '"+msg+"' -> "+curr+" msecs; last UUID: "+uuids[COUNT-1]);
        }
    }

    private final void testJDK(UUID[] uuids, int rounds)
    {
        while (--rounds >= 0) {
            for (int i = 0, len = uuids.length; i < len; ++i) {
                uuids[i] = UUID.randomUUID();
            }
        }
    }

    private final void testJDKNames(UUID[] uuids, int rounds) throws java.io.IOException
    {
        while (--rounds >= 0) {
            for (int i = 0, len = uuids.length; i < len; ++i) {
                final byte[] nameBytes = NAME.getBytes("UTF-8");
                uuids[i] = UUID.nameUUIDFromBytes(nameBytes);
            }
        }
    }
    
    private final void testNoArgs(UUID[] uuids, int rounds, NoArgGenerator uuidGen)
    {
        while (--rounds >= 0) {
            for (int i = 0, len = uuids.length; i < len; ++i) {
                uuids[i] = uuidGen.generate();
            }
        }
    }

    private final void testStringArg(UUID[] uuids, int rounds, StringArgGenerator uuidGen)
    {
        while (--rounds >= 0) {
            for (int i = 0, len = uuids.length; i < len; ++i) {
                uuids[i] = uuidGen.generate(NAME);
            }
        }
    }
    
    public static void main(String[] args) throws Exception
    {
        new MeasurePerformance().test();
    }
}
