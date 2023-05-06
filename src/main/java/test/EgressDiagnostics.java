package test;

import java.io.IOException;
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
        DatagramSocket ds = null;
        Socket socket = null;
        try {
            System.out.println("\nremote: " + remote);
            System.out.println("reachable: " + remote.getAddress().isReachable(3000));
            ds = new DatagramSocket();
            ds.connect(remote);
            InetAddress local = ds.getLocalAddress();
            System.out.println("ds local: " + local);
            if (local.equals(InetAddress.getByName("0.0.0.0"))) {
                socket = new Socket();
                socket.connect(remote, 3000);
                local = socket.getLocalAddress();
                System.out.println("socket local: " + local);
            }
            NetworkInterface ni = NetworkInterface.getByInetAddress(local);
            System.out.println("interface: " + ni);
            System.out.println("hardware: " + (ni == null ? null : macBytesToHex(ni.getHardwareAddress())));
        } catch (Throwable t) {
            System.out.println(t);
        } finally {
            if (ds != null) {
                ds.close();
            }
            if (socket != null) {
                try {
                    socket.close();
                } catch (IOException ioe) {
                    System.out.println(ioe);
                }
            }
        }
    }

    public static String macBytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < bytes.length; i++) {
            sb.append(String.format("%02X%s", bytes[i], (i < bytes.length - 1) ? "-" : ""));
        }
        return sb.toString();
    }
}
