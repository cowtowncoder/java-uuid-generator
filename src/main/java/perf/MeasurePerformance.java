package perf;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.UUID;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;

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

        final MessageDigest digester = MessageDigest.getInstance("SHA-1");
        final StringArgGenerator nameGen = Generators.nameBasedGenerator(namespaceForNamed, digester);
        final StringArgGenerator nameGenConcurrent = Generators.nameBasedGenerator(namespaceForNamed);

        while (true) {
            try {  Thread.sleep(100L); } catch (InterruptedException ie) { }
            int round = (i++ % 10);
   
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

            case 7:
                msg = "Jug, name-based, concurrent";
                testNameBasedConcurrent(uuids, ROUNDS, nameGenConcurrent);
                break;

            case 8:
                msg = "Jug, name-based, ten threads";
                testNameBasedTenThreads(uuids, ROUNDS, namespaceForNamed);
                break;

            case 9:
                msg = "Jug, name-based, concurrent, ten threads";
                testNameBasedConcurrentTenThreads(uuids, ROUNDS, namespaceForNamed);
                break;
                /*
            case 8:
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

    private final void testNameBasedConcurrent(Object[] uuids, int rounds, StringArgGenerator uuidGen)
    {
        while (--rounds >= 0) {
            for (int i = 0, len = uuids.length; i < len; ++i) {
                uuids[i] = uuidGen.concurrentGenerate(NAME);
            }
        }
    }

    private final void testNameBasedTenThreads(Object[] uuids, int rounds, final UUID namespaceForNamed) throws InterruptedException, BrokenBarrierException, NoSuchAlgorithmException
    {
        while (--rounds >= 0) {
            final CyclicBarrier gate = new CyclicBarrier(11);
            final MessageDigest digester = MessageDigest.getInstance("SHA-1");
            final StringArgGenerator nameGen = Generators.nameBasedGenerator(namespaceForNamed, digester);
            for (int j = 0; j < 10; j ++) {
                final Object[] fuuids = uuids;
                Thread t = new Thread(new Runnable() {
                    public void run() {
                        try {
                            gate.await();
                            for (int i = 0, len = fuuids.length; i < len; ++i) {
                                fuuids[i] = nameGen.generate(NAME);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
                t.start();
            }
            gate.await();
        }
    }

    private final void testNameBasedConcurrentTenThreads(Object[] uuids, int rounds, final UUID namespaceForNamed) throws InterruptedException, BrokenBarrierException, NoSuchAlgorithmException
    {
        while (--rounds >= 0) {
            final CyclicBarrier gate = new CyclicBarrier(11);
            final Object[] fuuids = uuids;
            for (int j = 0; j < 10; j ++) {
                Thread t = new Thread(new Runnable() {
                    public void run() {
                        final StringArgGenerator nameGen = Generators.nameBasedGenerator(namespaceForNamed);
                         try {
                            gate.await();
                            for (int i = 0, len = fuuids.length; i < len; ++i) {
                                fuuids[i] = nameGen.concurrentGenerate(NAME);
                            }
                        } catch (Exception e){
                            e.printStackTrace();
                        }
                    }
                });
                t.start();
            }
            gate.await();
        }
    }

    public static void main(String[] args) throws Exception
    {
        new MeasurePerformance().test();
    }
}
