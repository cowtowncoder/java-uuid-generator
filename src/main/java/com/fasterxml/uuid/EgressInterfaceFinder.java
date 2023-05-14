package com.fasterxml.uuid;

import java.io.IOException;
import java.net.*;
import java.util.*;

import static java.lang.String.format;

/**
 * A utility to attempt to find the default egress interface on the current
 * system.  The egress interface is the interface which is assigned the default
 * network route, such that outbound network traffic is routed out through that
 * interface.
 *
 * @since 4.2
 */
public class EgressInterfaceFinder {

    public static final int DEFAULT_TIMEOUT_MILLIS = 5000;

    /**
     * Attempt to find the default egress interface on the current system.
     *
     * <p>This is done on a best efforts basis, as Java does not provide the
     * necessary level of OS integration that is required to do this robustly.
     * However, this utility should do a decent job on Windows, Linux and macOS
     * so long as the local system has a working network connection at the time
     * of execution.  If the current system is multihomed with multiple egress
     * interfaces, one such interface will be chosen indeterminately.
     *
     * <p>Accurately determining the egress interface necessitates us attempting
     * to make outbound network connections.  This will be done
     * synchronously and can be a very slow process.  You can tune the amount of
     * time allowed to establish the outbound connections by
     * increasing/decreasing the timeout value.
     *
     * @return the egress interface
     * @throws EgressResolutionException if an egress interface could not be
     *                                   determined
     * @since 4.2
     */
    public NetworkInterface egressInterface() throws EgressResolutionException {
        return fromDefaultMechanisms(DEFAULT_TIMEOUT_MILLIS);
    }

    /**
     * Attempt to find the default egress interface on the current system,
     * using the specified connection timeout duration.
     *
     * <p>This will attempt to connect to one of the root DNS nameservers
     * (chosen randomly), and failing that, simply to IPv4 address 1.1.1.1
     * and finally IPv6 address 1::1.
     *
     * @param timeoutMillis the amount of time (milliseconds) allowed to
     *                      establish an outbound connection
     * @return the egress interface
     * @throws EgressResolutionException if an egress interface could not be
     *                                   determined
     * @since 4.2
     */
    public NetworkInterface fromDefaultMechanisms(final int timeoutMillis)
            throws EgressResolutionException {

        Finder[] finders = new Finder[] {
                rootNameServerFinder(timeoutMillis),
                remoteConnectionFinder(timeoutMillis,
                        new InetSocketAddress("1.1.1.1", 0)),
                remoteConnectionFinder(timeoutMillis,
                        new InetSocketAddress("1::1", 0))
        };

        return fromAggregate(finders);
    }

    /**
     * Attempt to find the default egress interface on the current system,
     * by trying each of the specified discovery mechanisms, in order, until
     * one of them succeeds.
     *
     * @return the egress interface
     * @param finders array of finder callbacks to be executed
     * @throws EgressResolutionException if an egress interface could not be
     *                                   determined
     * @since 4.2
     */
    public NetworkInterface fromAggregate(Finder[] finders)
            throws EgressResolutionException {

        Collection<EgressResolutionException> exceptions =
                new ArrayList<EgressResolutionException>();

        for (Finder finder : finders) {
            try {
                return finder.egressInterface();
            } catch (EgressResolutionException e) {
                exceptions.add(e);
            }
        }

        throw new EgressResolutionException(exceptions.toArray(
                new EgressResolutionException[0]));
    }

    private Finder rootNameServerFinder(final int timeoutMillis) {
        return new Finder() {
            @Override
            public NetworkInterface egressInterface()
                    throws EgressResolutionException {
                return fromRootNameserverConnection(timeoutMillis);
            }
        };
    }

    /**
     * Attempt to find the default egress interface on the current system,
     * by connecting to one of the root name servers (chosen at random).
     *
     * @param timeoutMillis the amount of time (milliseconds) allowed to
     *                      establish an outbound connection
     * @return the egress interface
     * @throws EgressResolutionException if an egress interface could not be
     *                                   determined
     * @since 4.2
     */
    public NetworkInterface fromRootNameserverConnection(int timeoutMillis)
            throws EgressResolutionException {
        String domainName = randomRootServerName();
        InetSocketAddress address = new InetSocketAddress(domainName, 53);
        return fromRemoteConnection(timeoutMillis, address);
    }

    static String randomRootServerName() {
        String roots = "abcdefghijklm";
        int index = new Random().nextInt(roots.length());
        return roots.charAt(index) + ".root-servers.net";
    }

    private Finder remoteConnectionFinder(final int timeoutMillis,
                                          final InetSocketAddress address) {
        return new Finder() {
            @Override
            public NetworkInterface egressInterface()
                    throws EgressResolutionException {
                return fromRemoteConnection(timeoutMillis, address);
            }
        };
    }

    /**
     * Attempt to find the default egress interface on the current system,
     * by connection to the specified address.  This will try two different
     * methods:
     * <ul>
     * <li>using a {@link DatagramSocket}, which seems to work well for Windows
     * &amp; Linux, and is faster to uses than {@link Socket} as opening one does
     * not actually require negotiate a handshake connection, but this does
     * not appear to work on MacOS
     * <li>using a {@link Socket}, which seems to work better for MacOS, but
     * needs to actually negotiate a connection handshake from a remote host
     * </ul>
     *
     * @param timeoutMillis the amount of time (milliseconds) allowed to
     *                      establish an outbound connection
     * @param remoteAddress the address to which a connection will be attempted
     *                      in order to determine which interface is used to
     *                      connect
     * @return the egress interface
     * @throws EgressResolutionException if an egress interface could not be
     *                                   determined
     * @since 4.2
     */
    public NetworkInterface fromRemoteConnection(
            int timeoutMillis, InetSocketAddress remoteAddress)
            throws EgressResolutionException {

        if (remoteAddress.isUnresolved()) {
            throw new EgressResolutionException(
                    format("remote address [%s] is unresolved", remoteAddress));
        }

        Finder socketFinder =
                remoteSocketConnectionFinder(timeoutMillis, remoteAddress);

        Finder datagramSocketFinder =
                remoteDatagramSocketConnectionFinder(remoteAddress);

        // try DatagramSocket first, by default
        Finder[] finders = new Finder[] { datagramSocketFinder, socketFinder };

        String osName = System.getProperty("os.name");
        if (osName != null && osName.startsWith("Mac")) {
            // instead try Socket first, for macOS
            finders = new Finder[] { socketFinder, datagramSocketFinder };
        }

        return fromAggregate(finders);
    }

    /**
     * Returns a finder that tries to determine egress interface by connecting
     * to the specified remote address.
     *
     * @param timeoutMillis give up after this length of time
     * @param address the remote address to connect to
     * @return finder callback
     */
    private Finder remoteSocketConnectionFinder(
            final int timeoutMillis, final InetSocketAddress address) {
        return new Finder() {
            @Override
            public NetworkInterface egressInterface()
                    throws EgressResolutionException {
                return fromRemoteSocketConnection(timeoutMillis, address);
            }
        };
    }

    /**
     * Attempt to find the default egress interface on the current system,
     * using the specified connection timeout duration and connecting with
     * a {@link Socket}.
     *
     * @param timeoutMillis the amount of time (milliseconds) allowed to
     *                      establish an outbound connection
     * @param remoteAddress the address to which a connection will be attempted
     *                      in order to determine which interface is used to
     *                      connect
     * @return the egress interface
     * @throws EgressResolutionException if an egress interface could not be
     *                                   determined
     * @since 4.2
     */
    public NetworkInterface fromRemoteSocketConnection(
            int timeoutMillis, InetSocketAddress remoteAddress)
            throws EgressResolutionException {

        Socket socket = new Socket();

        try {
            socket.connect(remoteAddress, timeoutMillis);
            return fromLocalAddress(socket.getLocalAddress());
        } catch (IOException e) {
            throw new EgressResolutionException(
                    format("Socket connection to [%s]", remoteAddress), e);
        } finally {
            try {
                socket.close();
            } catch (IOException e) {
                // ignore;
            }
        }
    }

    private Finder remoteDatagramSocketConnectionFinder(
            final InetSocketAddress address) {
        return new Finder() {
            @Override
            public NetworkInterface egressInterface()
                    throws EgressResolutionException {
                return fromRemoteDatagramSocketConnection(address);
            }
        };
    }

    /**
     * Attempt to find the default egress interface on the current system,
     * using the specified connection timeout duration and connecting with
     * a {@link DatagramSocket}.
     *
     * @param remoteAddress the address to which a connection will be attempted
     *                      in order to determine which interface is used to
     *                      connect
     * @return the egress interface
     * @throws EgressResolutionException if an egress interface could not be
     *                                   determined
     * @since 4.2
     */
    public NetworkInterface fromRemoteDatagramSocketConnection(
            InetSocketAddress remoteAddress)
            throws EgressResolutionException {

        DatagramSocket socket = null;

        try {
            socket = new DatagramSocket();
            socket.connect(remoteAddress);
            return fromLocalAddress(socket.getLocalAddress());
        } catch (IOException e) {
            throw new EgressResolutionException(
                    format("DatagramSocket connection to [%s]", remoteAddress),
                    e);
        } finally {
            if (socket != null) {
                socket.close();
            }
        }
    }

    /**
     * Attempt to find the default egress interface on the current system, by
     * finding a {@link NetworkInterface} that has the specified network
     * address.  If more than one interface has the specified address, then
     * one of them will be selected indeterminately.
     *
     * @param localAddress the local address which is assigned to an interface
     * @return the egress interface
     * @throws EgressResolutionException if an egress interface could not be
     *                                   determined
     * @since 4.2
     */
    public NetworkInterface fromLocalAddress(InetAddress localAddress)
            throws EgressResolutionException {
        try {
            InetAddress unspecifiedIPv4 = InetAddress.getByName("0.0.0.0");
            InetAddress unspecifiedIPv6 = InetAddress.getByName("::");

            if (localAddress.equals(unspecifiedIPv4) ||
                    localAddress.equals(unspecifiedIPv6)) {
                throw new EgressResolutionException(
                        format("local address [%s] is unspecified",
                                localAddress));
            }

            NetworkInterface ni =
                    NetworkInterface.getByInetAddress(localAddress);

            if (ni == null) {
                throw new EgressResolutionException(format(
                        "no interface found with local address [%s]",
                        localAddress));
            }

            return ni;
        } catch (IOException e) {
            throw new EgressResolutionException(
                    format("local address [%s]", localAddress), e);
        }
    }

    /**
     * An exception representing a failure to determine a default egress
     * network interface.  Please help improve this functionality by
     * providing feedback from the {@link #report()} method, if this is not
     * working for you.
     *
     * @since 4.2
     */
    public static class EgressResolutionException extends Exception {
        private final List<String> messages = new ArrayList<String>();

        public EgressResolutionException(String message) {
            super(message);
            messages.add(message);
        }

        public EgressResolutionException(String message, Throwable cause) {
            super(message, cause);
            messages.add(message);
            messages.add(cause.toString());
        }

        public EgressResolutionException(EgressResolutionException[] priors) {
            super(Arrays.toString(priors));
            for (EgressResolutionException e : priors) {
                messages.add("----------------------------------------------------------------------------");
                messages.addAll(e.messages);
            }
        }

        public void report() {
            reportLine("");
            reportLine("====================================");
            reportLine("| Egress Resolution Failure Report |");
            reportLine("====================================");
            reportLine("");
            reportLine("Please share this report in order to help improve the egress resolution");
            reportLine("mechanism.  Also please indicate if you believe that you have a currently");
            reportLine("working network connection.");
            reportLine("");
            showProperty("java.version");
            showProperty("java.version.date");
            showProperty("java.runtime.name");
            showProperty("java.runtime.version");
            showProperty("java.vendor");
            showProperty("java.vendor.url");
            showProperty("java.vendor.url.bug");
            showProperty("java.vendor.version");
            showProperty("java.vm.name");
            showProperty("java.vm.vendor");
            showProperty("java.vm.version");
            showProperty("os.arch");
            showProperty("os.name");
            showProperty("os.version");

            for (String message : messages) {
                reportLine(message);
            }
        }

        protected void reportLine(String line) {
            System.out.println(line);
        }

        private void showProperty(String key) {
            reportLine(key + ": " + System.getProperty(key));
        }

        public Collection<String> getMessages() {
            return messages;
        }
    }

    interface Finder {
        NetworkInterface egressInterface() throws EgressResolutionException;
    }
}
