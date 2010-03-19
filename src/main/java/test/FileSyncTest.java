package test;

import org.safehaus.uuid.*;
import org.safehaus.uuid.ext.*;

/**
 * Simple manual utility test class for manually checking whether file-based
 * synchronization seems to be working or not.
 */
public class FileSyncTest
{
    public static void main(String[] args)
        throws Exception
    {
        UUIDGenerator gen = UUIDGenerator.getInstance();
        FileBasedTimestampSynchronizer sync = 
            new FileBasedTimestampSynchronizer();
        // Let's stress-test it...
        sync.setUpdateInterval(2000L);
        gen.synchronizeExternally(sync);

        int counter = 1;
        while (true) {
            UUID uuid = gen.generateTimeBasedUUID();
	    // Default one is for convenient output
            System.out.println("#"+counter+" -> "+uuid);

	    /* This allows lexical sorting by uuid... (not very useful,
	     * since 'real' UUID ordering is not lexical)
	     */
            //System.out.println(""+uuid+" (#"+counter+")");

	    // And this can be used to ensure there are no dups:
            //System.out.println(""+uuid);
            ++counter;

            try {
                Thread.sleep(120L);
            } catch (InterruptedException ie) { }
        }
    }
}
