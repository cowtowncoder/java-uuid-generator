package test;

import java.net.*;

public class EgressDiagnostics {
    public static void main(String[] args) throws SocketException {
        System.out.println(System.getProperties());
        tryRemote(new InetSocketAddress("a.root-servers.net", 0));
        tryRemote(new InetSocketAddress("a.root-servers.net", 53));
        tryRemote(new InetSocketAddress("1.1.1.1", 0));
        tryRemote(new InetSocketAddress("1::1", 0));
    }

    public static void tryRemote(InetSocketAddress remote) {
        DatagramSocket socket = null;
        try {
            System.out.println("\nremote: " + remote);
            socket = new DatagramSocket();
            socket.connect(remote);
            InetAddress local = socket.getLocalAddress();
            System.out.println("local: " + local);
            NetworkInterface ni = NetworkInterface.getByInetAddress(local);
            System.out.println("interface: " + ni);
            System.out.println("hardware: " + ni.getHardwareAddress());
        } catch (Throwable t) {
            System.out.println(t);
            t.printStackTrace();
        } finally {
            if (socket != null) {
                socket.close();
            }
        }
    }
}
