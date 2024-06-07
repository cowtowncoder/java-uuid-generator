package perf;

import java.nio.charset.StandardCharsets;
import java.util.UUID;

import com.fasterxml.uuid.*;
import com.fasterxml.uuid.impl.RandomBasedGenerator;
import com.fasterxml.uuid.impl.TimeBasedGenerator;

/**
 * Simple micro-benchmark for evaluating performance of various UUID generation
 * techniques, including JDK's method as well as JUG's versions.
 *<p>
 * Notes: for name-based version we will pass plain Strings, assuming this is the
 * most common use case; even though it is possible to also pass raw byte arrays.
 * JDK and Jug implementations have similar performance so this only changes
 * relative speeds of name- vs time-based versions.
 *
 * @since 3.1
 */
public class MeasurePerformance
{

    // also: let's just use a single name for name-based, to avoid extra overhead:
    private final static String NAME_STRING = "http://www.cowtowncoder.com/blog/blog.html";

    private final static byte[] NAME_BYTES = NAME_STRING.getBytes(StandardCharsets.UTF_8);

    // Let's generate 50k UUIDs per test round
    private static final int COUNT = 1000;
    private static final int DEFAULT_ROUNDS = 50;

    private final int rounds;
    private final boolean runForever;

    public MeasurePerformance() { this(DEFAULT_ROUNDS, true); }

    public MeasurePerformance(int rounds, boolean runForever) {
        this.rounds = rounds;
        this.runForever = runForever;
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
        
        boolean running = true;
        final long sleepTime = runForever ? 350L : 1L;

        while (running) {
            Thread.sleep(sleepTime);
            int round = (i++ % 7);
   
            long curr = System.currentTimeMillis();
            String msg;
            boolean lf = (round == 0);
    
            switch (round) {
    
            case 0:
                msg = "JDK, random";
                testJDK(uuids, rounds);
                break;

            case 1:
                msg = "JDK, name";
                testJDKNames(uuids, rounds);
                break;
                
            case 2:
                msg = "Jug, time-based (non-sync)";
                testTimeBased(uuids, rounds, timeGenPlain);
                break;

            case 3:
                msg = "Jug, time-based (SYNC)";
                testTimeBased(uuids, rounds, timeGenSynced);
                break;
                
            case 4:
                msg = "Jug, SecureRandom";
                testRandom(uuids, rounds, secureRandomGen);
                break;

            case 5:
                msg = "Jug, java.util.Random";
                testRandom(uuids, rounds, utilRandomGen);
                break;

                
            case 6:
                msg = "Jug, name-based";
                testNameBased(uuids, rounds, nameGen);

                // Last one, quit unless running forever
                if (!runForever) {
                    running = false;
                }
                break;

                /*
            case 7:
                msg = "http://johannburkard.de/software/uuid/";
                testUUID32(uuids, rounds);
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
                final byte[] nameBytes = NAME_BYTES;
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
                uuids[i] = uuidGen.generate(NAME_STRING);
            }
        }
    }
    
    public static void main(String[] args) throws Exception
    {
        new MeasurePerformance(DEFAULT_ROUNDS, true).test();
    }
}
