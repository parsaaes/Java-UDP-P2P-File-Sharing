package ir.ac.aut.ceit.cn.Logic;

import java.io.IOException;
import java.net.*;

public class Server extends NetworkPeer implements Runnable {
    public final static int PORT = 12345;

    public void run() {
        waitToBeDiscovered();
    }

    public void waitToBeDiscovered() {
        try {
            // listen to all traffic that is going to this PORT
            DatagramSocket datagramSocket = new DatagramSocket(PORT, InetAddress.getByName("0.0.0.0"));
            datagramSocket.setBroadcast(true);
            while (true) {
                byte[] recv = new byte[10000];
                DatagramPacket datagramPacket = new DatagramPacket(recv, recv.length);
                datagramSocket.receive(datagramPacket);
                printLog(new String(datagramPacket.getData()));
            }
        } catch (SocketException e) {
            e.printStackTrace();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void printLog(String text) {
        System.out.println("[Server:] " + text);
    }


    public static void main(String[] args) {
        new Server().waitToBeDiscovered();
    }
}
