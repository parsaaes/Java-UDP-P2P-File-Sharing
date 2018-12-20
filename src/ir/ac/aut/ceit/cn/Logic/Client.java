package ir.ac.aut.ceit.cn.Logic;

import java.io.IOException;
import java.net.*;
import java.util.ArrayList;
import java.util.Enumeration;

public class Client extends NetworkPeer implements Runnable {

    public void run() {
        sendDiscovery();
    }

    public void sendDiscovery() {
        try {
            DatagramSocket datagramSocket = new DatagramSocket();
            datagramSocket.setBroadcast(true);
            byte[] data = "Hi, I'm a discovery message :D".getBytes();
            ArrayList<InetAddress> interfaces = getAllBroadcastAddresses();
            for (InetAddress anInterface : interfaces) {
                DatagramPacket datagramPacket = new DatagramPacket(data, data.length, anInterface,12345);
                datagramSocket.send(datagramPacket);
            }
        } catch (SocketException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public ArrayList<InetAddress> getAllBroadcastAddresses() {
        ArrayList<InetAddress> result = new ArrayList<>();
        try {
           Enumeration interfaces =  NetworkInterface.getNetworkInterfaces();
           while (interfaces.hasMoreElements()) {
               NetworkInterface networkInterface = (NetworkInterface) interfaces.nextElement();
               for (InterfaceAddress interfaceAddress : networkInterface.getInterfaceAddresses()) {
                   InetAddress broadcast = interfaceAddress.getBroadcast();
                   if(broadcast != null) {
                       result.add(broadcast);
                   }
               }

           }
        } catch (SocketException e) {
            e.printStackTrace();
        }
        for (InetAddress inetAddress : result) {
            printLog(inetAddress.toString());
        }
        return result;
    }


    @Override
    public void printLog(String text) {
        System.out.println("[Client:] " + text);
    }

    public static void main(String[] args) {
        new Client().sendDiscovery();
    }
}
