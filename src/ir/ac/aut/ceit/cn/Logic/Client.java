package ir.ac.aut.ceit.cn.Logic;

import ir.ac.aut.ceit.cn.Message.*;
import ir.ac.aut.ceit.cn.Model.FileUtils;

import java.io.*;
import java.lang.instrument.Instrumentation;
import java.net.*;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Enumeration;

public class Client extends NetworkPeer implements Runnable {
    private String fileName;
    private long fileSize;
    private DatagramSocket datagramSocket;
    private ArrayList<DatagramSocket> datagramSocketArrayList;
    private InetAddress serverPeerIP;
    private int serverPeerPort;

    public Client(String fileName) {
        this.fileName = fileName;
            datagramSocketArrayList = new ArrayList<>();
            //setUpDatagramSocket(datagramSocket);
    }

    private void setUpDatagramSocket(DatagramSocket ds) throws SocketException {
        ds.setSendBufferSize(100000);
        ds.setReceiveBufferSize(100000);
        ds.setBroadcast(true);
    }

    public void run() {
        boolean requestDownload = false;
        sendDiscovery();
        requestDownload = waitForAnswer();
        if(requestDownload == true) {
            sendDownloadRequest();
            downloadFile();
        }
    }

    private void downloadFile() {
        int numberOfChunks = FileMessage.getChunkSize(fileSize);
        boolean[] chunksReceived = new boolean[numberOfChunks];
        for (int i = 0; i < chunksReceived.length; i++) {
            chunksReceived[i] = false;
        }
        byte[][] chunks = new byte[numberOfChunks][];
        while (downloadIsNotComplete(chunksReceived)) {
            byte[] data = new byte[FileMessage.MAX_PACKET_SIZE + 2000];
            DatagramPacket datagramPacket = new DatagramPacket(data, data.length);
            try {
                datagramSocket.setSoTimeout(0);
                datagramSocket.receive(datagramPacket);
                ObjectInputStream objectInputStream = new ObjectInputStream(new ByteArrayInputStream(datagramPacket.getData()));
                Message message = (Message) objectInputStream.readObject();
                objectInputStream.close();
                if(isFileMessage(message)) {
                    FileMessage fileMessage = (FileMessage)message;
                    chunks[fileMessage.getOffset()] = fileMessage.getData();
                    chunksReceived[fileMessage.getOffset()] = true;
                    printLog("I got a chunk! ->" + String.valueOf(fileMessage.getOffset()));
                }
            } catch (IOException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
        //System.out.println("i am out");
        int totalSize = 0;
        for (byte[] chunk : chunks) {
            totalSize += chunk.length;
        }
        ByteBuffer byteBuffer = ByteBuffer.allocate(totalSize);
        for (byte[] chunk : chunks) {
            byteBuffer.put(chunk);
        }
        FileUtils.writeFile(byteBuffer.array(),System.getProperty("user.dir").toString() + "/receiver/" + fileName);
        printLog("Complete");
    }

    private boolean isFileMessage(Message message) {
        return message.getType() == MessageTypes.FILE_MESSAGE;
    }

    private boolean downloadIsNotComplete(boolean[] chunksReceived) {
        for (boolean b : chunksReceived) {
            if(b == false) {
                return true;
            }
        }
        return false;
    }

    private void sendDownloadRequest() {
        byte[] data;
        try {
            data = getRequestBytes();
            DatagramPacket requestPacket = new DatagramPacket(data, data.length,serverPeerIP,serverPeerPort);
            datagramSocket.send(requestPacket);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void sendDiscovery() {
        try {
            byte[] data = getSendDiscoveryBytes(fileName);
            if(data.length <= DiscoveryMessage.getMaxValidSize()) {
                ArrayList<InetAddress> interfaces = getAllBroadcastAddresses();
                for (InetAddress anInterface : interfaces) {
                    DatagramPacket datagramPacket = new DatagramPacket(data, data.length, anInterface, 12345);
                    DatagramSocket ds = new DatagramSocket();
                    setUpDatagramSocket(ds);
                    datagramSocketArrayList.add(ds);
                    ds.send(datagramPacket);
                }
            }
            else {
                printLog("File name is just too big!!");
            }
        } catch (SocketException e) {
            printLog("a network in list is unreachable. sending discovery to this network failed.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private boolean waitForAnswer() {
        Message message = null;
        try {
            while (true) {
                byte[] answer = new byte[10000];
                DatagramPacket responsePacket = new DatagramPacket(answer, answer.length);
                for (DatagramSocket socket : datagramSocketArrayList) {
                    socket.setSoTimeout(5000);
                    try {
                        socket.receive(responsePacket);
                    }
                    catch (IOException e) {
                        printLog("waiting for answer timed out. waiting for other peers.");
                        continue;
                    }
                    ObjectInputStream objectInputStream = new ObjectInputStream(new ByteArrayInputStream(responsePacket.getData()));
                    message = (Message) objectInputStream.readObject();
                    objectInputStream.close();
                    if(isIHaveMessage(message)) {
                        datagramSocket = socket;
                        break;
                    }
                }
//                datagramSocket.setSoTimeout(5000);
//                datagramSocket.receive(responsePacket);

                if (isIHaveMessage(message)) {
                    IHaveMessage iHaveMessage = (IHaveMessage) message;
                    printLog("received " + iHaveMessage.toString());
                    fileSize = iHaveMessage.getFileSize();
                    serverPeerIP = responsePacket.getAddress();
                    serverPeerPort = responsePacket.getPort();
                    break;
                }
            }
            datagramSocket.setBroadcast(false);
            return true;

        } catch (SocketTimeoutException e) {
            printLog("time out :( it's seems no one has this file.");
            return false;
        }
        catch (IOException e) {
            printLog("waiting for answer timed out.");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return false;
    }

    private boolean isIHaveMessage(Message message) {
        return message.getType() == 1;
    }

    private byte[] getSendDiscoveryBytes(String fileName) throws IOException {
        byte[] data;
        DiscoveryMessage discoveryMessage = new DiscoveryMessage(fileName);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        ObjectOutput oo = new ObjectOutputStream(byteArrayOutputStream);
        oo.writeObject(discoveryMessage);
        oo.close();
        data = byteArrayOutputStream.toByteArray();

        //CustomDiscoveryCustomMessage customDiscoveryMessage = new CustomDiscoveryCustomMessage("hi :D :|");
        //data = customDiscoveryMessage.getSerialized();

        return data;
    }

    public ArrayList<InetAddress> getAllBroadcastAddresses() {
        ArrayList<InetAddress> result = new ArrayList<>();
        try {
            result.add(InetAddress.getByName("127.0.0.1"));
            result.add(InetAddress.getLocalHost());
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
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
        for (int i = 0; i < result.size(); i++) {
            InetAddress inetAddress = result.get(i);
            if(i < 2) {
                printLog("I loopback/localhost address " + inetAddress.toString());
            }
            else {
                printLog("I found a broadcast address " + inetAddress.toString());
            }
        }
        return result;
    }


    @Override
    public void printLog(String text) {
        System.out.println("[Client:] " + text);
    }


    public byte[] getRequestBytes() throws IOException {
        byte[] data;
        DownloadRequestMessage downloadRequestMessage = new DownloadRequestMessage();
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        ObjectOutput oo = new ObjectOutputStream(byteArrayOutputStream);
        oo.writeObject(downloadRequestMessage);
        data = byteArrayOutputStream.toByteArray();
        oo.close();
        return data;    }
}
