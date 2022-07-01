package test;

import java.net.*;

public class EgressDiagnostics {
    public static void main(String[] args) throws SocketException {
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
        tryRemote(new InetSocketAddress("a.root-servers.net", 0));
        tryRemote(new InetSocketAddress("a.root-servers.net", 53));
        tryRemote(new InetSocketAddress("1.1.1.1", 0));
        tryRemote(new InetSocketAddress("1::1", 0));
    }

    public static void showProperty(String key) {
        System.out.println(key + ": " + System.getProperty(key));
    }

    public static void tryRemote(InetSocketAddress remote) {
        DatagramSocket socket = null;
        try {
            System.out.println("\nremote: " + remote);
            System.out.println("reachable: " + remote.getAddress().isReachable(3000));
            socket = new DatagramSocket();
            socket.connect(remote);
            InetAddress local = socket.getLocalAddress();
            System.out.println("local: " + local);
            NetworkInterface ni = NetworkInterface.getByInetAddress(local);
            System.out.println("interface: " + ni);
            System.out.println("hardware: " + (ni == null ? null : ni.getHardwareAddress().toString().substring(3)));
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
