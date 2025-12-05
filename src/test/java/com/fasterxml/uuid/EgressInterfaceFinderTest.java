package com.fasterxml.uuid;

import com.fasterxml.uuid.EgressInterfaceFinder.EgressResolutionException;
import com.fasterxml.uuid.EgressInterfaceFinder.Finder;
import org.junit.jupiter.api.Test;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.NetworkInterface;
import java.net.UnknownHostException;

import static com.fasterxml.uuid.EgressInterfaceFinder.DEFAULT_TIMEOUT_MILLIS;
import static org.junit.jupiter.api.Assertions.*;

public class EgressInterfaceFinderTest {

    private final EgressInterfaceFinder finder = new EgressInterfaceFinder();

    @Test
    public void testUnspecifiedIPv4LocalAddress() throws UnknownHostException {
        EgressResolutionException ex = null;
        try {
            finder.fromLocalAddress(InetAddress.getByName("0.0.0.0"));
        } catch (EgressResolutionException e) {
            ex = e;
        }
        assertNotNull(ex, "EgressResolutionException was not thrown");
        String message = ex.getMessage();
        assertTrue(message.startsWith("local address"), String.format(
                        "message [%s] does not begin with \"local address\"",
                        message));
        assertEquals(1, ex.getMessages().size());
    }

    @Test
    public void testUnspecifiedIPv6LocalAddress() throws Exception {
        EgressResolutionException ex = null;
        try {
            finder.fromLocalAddress(InetAddress.getByName("::"));
        } catch (EgressResolutionException e) {
            ex = e;
        }
        assertNotNull(ex, "EgressResolutionException was not thrown");
        String message = ex.getMessage();
        assertTrue(message.startsWith("local address"), String.format(
                        "message [%s] does not begin with \"local address\"",
                        message));
        assertEquals(1, ex.getMessages().size());
    }

    @Test
    public void testFromLocalAddress() throws Exception {
        NetworkInterface anInterface =
                NetworkInterface.getNetworkInterfaces().nextElement();
        InetAddress anAddress = anInterface.getInetAddresses().nextElement();
        assertEquals(anInterface, finder.fromLocalAddress(anAddress));
    }

    @Test
    public void testFromIncorrectLocalAddress() throws Exception {
        EgressResolutionException ex = null;
        try {
            String name = EgressInterfaceFinder.randomRootServerName();
            finder.fromLocalAddress(InetAddress.getByName(name));
        } catch (EgressResolutionException e) {
            ex = e;
        }
        assertNotNull(ex, "EgressResolutionException was not thrown");
        String message = ex.getMessage();
        assertTrue(message.startsWith("no interface found"), String.format(
                        "message [%s] does not begin with \"no interface found\"",
                        message));
        assertEquals(1, ex.getMessages().size());
    }

    @Test
    public void testFromRemoteDatagramSocketConnection() throws Exception {
        if (!System.getProperty("os.name").startsWith("Mac")) {
            String name = EgressInterfaceFinder.randomRootServerName();
            InetSocketAddress address = new InetSocketAddress(name, 53);
            finder.fromRemoteDatagramSocketConnection(address);
        }
    }

    @Test
    public void testFromRemoteSocketConnection() throws Exception {
        String name = EgressInterfaceFinder.randomRootServerName();
        InetSocketAddress address = new InetSocketAddress(name, 53);
        finder.fromRemoteSocketConnection(DEFAULT_TIMEOUT_MILLIS, address);
    }

    @Test
    public void testFromRemoteConnection() throws Exception {
        String name = EgressInterfaceFinder.randomRootServerName();
        InetSocketAddress address = new InetSocketAddress(name, 53);
        finder.fromRemoteConnection(DEFAULT_TIMEOUT_MILLIS, address);
    }

    @Test
    public void testFromRootNameServerConnection() throws Exception {
        finder.fromRootNameserverConnection(DEFAULT_TIMEOUT_MILLIS);
    }

    @Test
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
        assertNotNull(ex, "EgressResolutionException was not thrown");
        assertEquals(9, ex.getMessages().size());
    }

    @Test
    public void testDefaultMechanisms() throws Exception {
        try {
            finder.egressInterface();
        } catch (EgressResolutionException e) {
            e.report();
            throw e;
        }
    }
}
