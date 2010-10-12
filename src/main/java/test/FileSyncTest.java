package test;

import java.util.UUID;

import com.fasterxml.uuid.*;
import com.fasterxml.uuid.ext.*;
import com.fasterxml.uuid.impl.TimeBasedGenerator;

/**
 * Simple manual utility test class for manually checking whether file-based
 * synchronization seems to be working or not.
 */
public class FileSyncTest
{
    public static void main(String[] args)
        throws Exception
    {
        FileBasedTimestampSynchronizer sync = new FileBasedTimestampSynchronizer();
        // Let's stress-test it...
        sync.setUpdateInterval(2000L);

        // must have a NIC for this to work, should be ok:
        EthernetAddress eth = EthernetAddress.fromInterface();
        TimeBasedGenerator gen = Generators.timeBasedGenerator(eth, sync);

        int counter = 1;
        while (true) {
            UUID uuid = gen.generate();
	    // Default one is for convenient output
            System.out.println("#"+counter+" -> "+uuid);

	    /* This allows lexical sorting by uuid... (not very useful,
	     * since 'real' UUID ordering is not lexical)
	     */
            System.out.println(""+uuid+" (#"+counter+")");

	    // And this can be used to ensure there are no dups:
            System.out.println(""+uuid);
            ++counter;

            try {
                Thread.sleep(120L);
            } catch (InterruptedException ie) { }
        }
    }
}
