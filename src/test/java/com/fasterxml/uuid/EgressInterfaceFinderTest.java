package com.fasterxml.uuid;

import com.fasterxml.uuid.EgressInterfaceFinder.EgressResolutionException;
import com.fasterxml.uuid.EgressInterfaceFinder.Finder;
import junit.framework.TestCase;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.NetworkInterface;
import java.net.UnknownHostException;

import static com.fasterxml.uuid.EgressInterfaceFinder.DEFAULT_TIMEOUT_MILLIS;

public class EgressInterfaceFinderTest extends TestCase {

    private final EgressInterfaceFinder finder = new EgressInterfaceFinder();

    public void testUnspecifiedIPv4LocalAddress() throws UnknownHostException {
        EgressResolutionException ex = null;
        try {
            finder.fromLocalAddress(InetAddress.getByName("0.0.0.0"));
        } catch (EgressResolutionException e) {
            ex = e;
        }
        assertNotNull("EgressResolutionException was not thrown", ex);
        String message = ex.getMessage();
        assertTrue(String.format(
                        "message [%s] does not begin with \"local address\"",
                        message),
                message.startsWith("local address"));
        assertEquals(1, ex.getMessages().size());
    }

    public void testUnspecifiedIPv6LocalAddress() throws Exception {
        EgressResolutionException ex = null;
        try {
            finder.fromLocalAddress(InetAddress.getByName("::"));
        } catch (EgressResolutionException e) {
            ex = e;
        }
        assertNotNull("EgressResolutionException was not thrown", ex);
        String message = ex.getMessage();
        assertTrue(String.format(
                        "message [%s] does not begin with \"local address\"",
                        message),
                message.startsWith("local address"));
        assertEquals(1, ex.getMessages().size());
    }

    public void testFromLocalAddress() throws Exception {
        NetworkInterface anInterface =
                NetworkInterface.getNetworkInterfaces().nextElement();
        InetAddress anAddress = anInterface.getInetAddresses().nextElement();
        assertEquals(anInterface, finder.fromLocalAddress(anAddress));
    }

    public void testFromIncorrectLocalAddress() throws Exception {
        EgressResolutionException ex = null;
        try {
            String name = EgressInterfaceFinder.randomRootServerName();
            finder.fromLocalAddress(InetAddress.getByName(name));
        } catch (EgressResolutionException e) {
            ex = e;
        }
        assertNotNull("EgressResolutionException was not thrown", ex);
        String message = ex.getMessage();
        assertTrue(String.format(
                        "message [%s] does not begin with \"no interface found\"",
                        message),
                message.startsWith("no interface found"));
        assertEquals(1, ex.getMessages().size());
    }

    public void testFromRemoteDatagramSocketConnection() throws Exception {
        if (!System.getProperty("os.name").startsWith("Mac")) {
            String name = EgressInterfaceFinder.randomRootServerName();
            InetSocketAddress address = new InetSocketAddress(name, 53);
            finder.fromRemoteDatagramSocketConnection(address);
        }
    }

    public void testFromRemoteSocketConnection() throws Exception {
        String name = EgressInterfaceFinder.randomRootServerName();
        InetSocketAddress address = new InetSocketAddress(name, 53);
        finder.fromRemoteSocketConnection(DEFAULT_TIMEOUT_MILLIS, address);
    }

    public void testFromRemoteConnection() throws Exception {
        String name = EgressInterfaceFinder.randomRootServerName();
        InetSocketAddress address = new InetSocketAddress(name, 53);
        finder.fromRemoteConnection(DEFAULT_TIMEOUT_MILLIS, address);
    }

    public void testFromRootNameServerConnection() throws Exception {
        finder.fromRootNameserverConnection(DEFAULT_TIMEOUT_MILLIS);
    }

    public void testAggregateExceptions() {
        EgressResolutionException ex = null;
        final int[] counter = {0};
        Finder aFinder = new Finder() {
            @Override
            public NetworkInterface egressInterface()
                    throws EgressResolutionException {
                throw new EgressResolutionException(
                        String.format("exception %d", ++counter[0]),
                        new Exception("test exception"));
            }
        };
        try {
            finder.fromAggregate(new Finder[] { aFinder, aFinder, aFinder});
        } catch (EgressResolutionException e) {
            ex = e;
        }
        assertNotNull("EgressResolutionException was not thrown", ex);
        assertEquals(9, ex.getMessages().size());
    }

    public void testDefaultMechanisms() throws Exception {
        try {
            finder.egressInterface();
        } catch (EgressResolutionException e) {
            e.report();
            throw e;
        }
    }
}
