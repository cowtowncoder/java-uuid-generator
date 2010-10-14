package test;

import java.util.UUID;

import com.fasterxml.uuid.*;
import com.fasterxml.uuid.impl.NameBasedGenerator;

/**
 * Simple micro-benchmark for evaluating performance of various UUID generation
 * techniques, including JDK's method as well as JUG's variants.
 *
 * @since 3.0
 */
public class MeasurePerformance
{
    // Let's generate quarter million UUIDs per test
    
    private static final int ROUNDS = 250;
    private static final int COUNT = 1000;
    
    public void test() throws Exception
    {
        int i = 0;

        final UUID[] uuids = new UUID[COUNT];

        // can either use bogus address; or local one, no difference perf-wise
        EthernetAddress nic = EthernetAddress.fromInterface();

        UUID namespaceForNamed = NameBasedGenerator.NAMESPACE_DNS;
        
        final NoArgGenerator secureRandomGen = Generators.randomBasedGenerator();
        final NoArgGenerator utilRandomGen = Generators.randomBasedGenerator(new java.util.Random(123));
        final NoArgGenerator timeGen = Generators.timeBasedGenerator(nic);
        final StringArgGenerator nameGen = Generators.nameBasedGenerator(namespaceForNamed);

        // also: let's just use a single name for name-based, to avoid extra overhead:
        final String NAME = "http://www.cowtowncoder.com/blog/blog.html";
        
        while (true) {
            try {  Thread.sleep(100L); } catch (InterruptedException ie) { }
            int round = (i++ % 5);
    
            long curr = System.currentTimeMillis();
            String msg;
            boolean lf = (round == 0);
    
            switch (round) {
    
            case 0:
                msg = "JDK";
                testJDK(uuids, ROUNDS);
                break;
    
            case 1:
                msg = "Jug, SecureRandom";
                testNoArgs(uuids, ROUNDS, secureRandomGen);
                break;

            case 2:
                msg = "Jug, java.util.Random";
                testNoArgs(uuids, ROUNDS, utilRandomGen);
                break;
                
            case 3:
                msg = "Jug, time-based";
                testNoArgs(uuids, ROUNDS, timeGen);
                break;

            case 4:
                msg = "Jug, name-based";
                testStringArg(uuids, ROUNDS, nameGen, NAME);
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

    private final void testNoArgs(UUID[] uuids, int rounds, NoArgGenerator uuidGen)
    {
        while (--rounds >= 0) {
            for (int i = 0, len = uuids.length; i < len; ++i) {
                uuids[i] = uuidGen.generate();
            }
        }
    }

    private final void testStringArg(UUID[] uuids, int rounds, StringArgGenerator uuidGen,
            String name)
    {
        while (--rounds >= 0) {
            for (int i = 0, len = uuids.length; i < len; ++i) {
                uuids[i] = uuidGen.generate(name);
            }
        }
    }
    
    public static void main(String[] args) throws Exception
    {
        new MeasurePerformance().test();
    }
}
