package perf;

import java.util.UUID;

import com.fasterxml.uuid.*;
import com.fasterxml.uuid.impl.RandomBasedGenerator;
import com.fasterxml.uuid.impl.TimeBasedGenerator;

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

        final Object[] uuids = new Object[COUNT];

        // can either use bogus address; or local one, no difference perf-wise
        EthernetAddress nic = EthernetAddress.fromInterface();

        // Whether to include namespace? Depends on whether we compare with JDK (which does not)
//        UUID namespaceForNamed = NAMESPACE;
        UUID namespaceForNamed = null;

        final RandomBasedGenerator secureRandomGen = Generators.randomBasedGenerator();
        final RandomBasedGenerator utilRandomGen = Generators.randomBasedGenerator(new java.util.Random(123));
        final TimeBasedGenerator timeGenPlain = Generators.timeBasedGenerator(nic);
        final TimeBasedGenerator timeGenSynced = Generators.timeBasedGenerator(nic,
                new com.fasterxml.uuid.ext.FileBasedTimestampSynchronizer());
        final StringArgGenerator nameGen = Generators.nameBasedGenerator(namespaceForNamed);
        
        while (true) {
            try {  Thread.sleep(100L); } catch (InterruptedException ie) { }
            int round = (i++ % 7);
   
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
                msg = "Jug, time-based (non-sync)";
                testTimeBased(uuids, ROUNDS, timeGenPlain);
                break;

            case 3:
                msg = "Jug, time-based (SYNC)";
                testTimeBased(uuids, ROUNDS, timeGenSynced);
                break;
                
            case 4:
                msg = "Jug, SecureRandom";
                testRandom(uuids, ROUNDS, secureRandomGen);
                break;

            case 5:
                msg = "Jug, java.util.Random";
                testRandom(uuids, ROUNDS, utilRandomGen);
                break;

                
            case 6:
                msg = "Jug, name-based";
                testNameBased(uuids, ROUNDS, nameGen);
                break;

                /*
            case 7:
                msg = "http://johannburkard.de/software/uuid/";
                testUUID32(uuids, ROUNDS);
                break;
                */
                
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

    // Test implementation from http://johannburkard.de/software/uuid/
    /*
    private final void testUUID32(Object[] uuids, int rounds)
    {
        while (--rounds >= 0) {
            for (int i = 0, len = uuids.length; i < len; ++i) {
                uuids[i] = new com.eaio.uuid.UUID();
            }
        }
    }
    */
    
    private final void testJDK(Object[] uuids, int rounds)
    {
        while (--rounds >= 0) {
            for (int i = 0, len = uuids.length; i < len; ++i) {
                uuids[i] = UUID.randomUUID();
            }
        }
    }

    private final void testJDKNames(Object[] uuids, int rounds) throws java.io.IOException
    {
        while (--rounds >= 0) {
            for (int i = 0, len = uuids.length; i < len; ++i) {
                final byte[] nameBytes = NAME.getBytes("UTF-8");
                uuids[i] = UUID.nameUUIDFromBytes(nameBytes);
            }
        }
    }
    
    private final void testRandom(Object[] uuids, int rounds, RandomBasedGenerator uuidGen)
    {
        while (--rounds >= 0) {
            for (int i = 0, len = uuids.length; i < len; ++i) {
                uuids[i] = uuidGen.generate();
            }
        }
    }

    private final void testTimeBased(Object[] uuids, int rounds, TimeBasedGenerator uuidGen)
    {
        while (--rounds >= 0) {
            for (int i = 0, len = uuids.length; i < len; ++i) {
                uuids[i] = uuidGen.generate();
            }
        }
    }
    
    private final void testNameBased(Object[] uuids, int rounds, StringArgGenerator uuidGen)
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
