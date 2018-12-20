package ir.ac.aut.ceit.cn.Logic;

import ir.ac.aut.ceit.cn.Message.*;
import ir.ac.aut.ceit.cn.Model.FileUtils;

import java.io.*;
import java.net.*;

public class Server extends NetworkPeer implements Runnable {
    public final static int PORT = 12345;
    private String fileName;
    private long fileSize;
    private DatagramSocket datagramSocket;
    private InetAddress clientPeerIP;
    private int clientPeerPort;

    public byte[] dataToSend;


    public Server(String fileName,String filePath) {
        this.fileName = fileName;
        dataToSend = FileUtils.readFile(filePath);
        this.fileSize = dataToSend.length;

        try {
            datagramSocket = new DatagramSocket(PORT, InetAddress.getByName("0.0.0.0"));
        } catch (SocketException e) {
            e.printStackTrace();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
    }

    public void run() {
        while (true) {
            boolean sendFile = false;
            waitToBeDiscovered();
            sendFile = waitForDownloadRequest();
            if (sendFile == true) {
                int numberOfChunks = FileMessage.getChunkSize(fileSize);
                for(int chunkIndex = 0; chunkIndex < numberOfChunks; chunkIndex++) {
                    int start = chunkIndex * FileMessage.MAX_PACKET_SIZE;
                    byte[] chunk = new byte[Math.min((int)(fileSize-start),FileMessage.MAX_PACKET_SIZE)];
                    for (int i = 0; i < chunk.length; i++) {
                        chunk[i] = dataToSend[start + i];
                    }
                    printLog("I sent a file chunk [" + String.valueOf(start) + ":" + String.valueOf(chunk.length - 1 + start) + "]");
                    uploadFile(chunkIndex,chunk);
                }
            }
        }
    }

    private void uploadFile(int offset,byte[] chunk) {
        byte[] data;
        try {
            data = getFileMessageBytes(offset,chunk);
            DatagramPacket filePacket = new DatagramPacket(data, data.length, clientPeerIP, clientPeerPort);
            //System.out.println("size---->" + String.valueOf(filePacket.getData().length));
            datagramSocket.send(filePacket);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private boolean waitForDownloadRequest() {
        while (true) {
            byte[] recv = new byte[10000];
            DatagramPacket datagramPacket = new DatagramPacket(recv, recv.length);
            try {
                datagramSocket.setSoTimeout(3000);
                datagramSocket.receive(datagramPacket);
                ObjectInputStream objectInputStream = new ObjectInputStream(new ByteArrayInputStream(datagramPacket.getData()));
                Message message = (Message) objectInputStream.readObject();
                objectInputStream.close();
                if (isDownloadRequest(message)) {
                    printLog("download request received.");
                    clientPeerIP = datagramPacket.getAddress();
                    clientPeerPort = datagramPacket.getPort();
                    return true;
                }
            } catch (SocketTimeoutException e) {
                printLog("time out :(");
                return false;
            } catch (IOException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    private boolean isDownloadRequest(Message message) {
        return message.getType() == MessageTypes.DOWNLOAD_REQUEST_MESSAGE;
    }

    public void waitToBeDiscovered() {
        try {
            boolean wait = true;
            // listen to all traffic that is going to this PORT
            while (wait) {
                wait = waitForDiscoveryMessage();
            }

        } catch (SocketException e) {
            e.printStackTrace();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    private boolean isDiscoveryMessage(Message message) {
//        return message instanceof DiscoveryMessage;
        return message.getType() == MessageTypes.DISCOVERY_MESSAGE;
    }

    private boolean waitForDiscoveryMessage() throws IOException, ClassNotFoundException {
        byte[] recv = new byte[10000];
        DatagramPacket datagramPacket = new DatagramPacket(recv, recv.length);
        datagramSocket.setSoTimeout(0);
        datagramSocket.receive(datagramPacket);
        ObjectInputStream objectInputStream = new ObjectInputStream(new ByteArrayInputStream(datagramPacket.getData()));
        Message message = (Message) objectInputStream.readObject();
        objectInputStream.close();
        if (isDiscoveryMessage(message)) {
            DiscoveryMessage discoveryMessage = (DiscoveryMessage) message;
            printLog(datagramPacket.getAddress().toString() + ":" + String.valueOf(datagramPacket.getPort()) + " wants " + discoveryMessage.getFileName());
            if (discoveryMessage.getFileName().equals(fileName)) {
                sendIHaveMessage(datagramSocket, datagramPacket.getPort(), datagramPacket.getAddress());
                return false;
            }
        }

        return true;
    }

    private void sendIHaveMessage(DatagramSocket datagramSocket, int port, InetAddress inetAddress) throws IOException {
//        DatagramSocket iHaveSocket = new DatagramSocket(port, inetAddress);
        byte[] data = getIHaveBytes();
        DatagramPacket iHavePacket = new DatagramPacket(data, data.length, inetAddress, port);
        datagramSocket.send(iHavePacket);
    }

    private byte[] getIHaveBytes() throws IOException {
        byte[] data;
        IHaveMessage iHaveMessage = new IHaveMessage(fileName, fileSize);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        ObjectOutput oo = new ObjectOutputStream(byteArrayOutputStream);
        oo.writeObject(iHaveMessage);
        data = byteArrayOutputStream.toByteArray();
        oo.close();
        return data;
    }

    private byte[] getFileMessageBytes(int offset, byte[] chunk) throws IOException {
        byte[] data;
        FileMessage fileMessage = new FileMessage(offset, chunk);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        ObjectOutput oo = new ObjectOutputStream(byteArrayOutputStream);
        oo.writeObject(fileMessage);
        data = byteArrayOutputStream.toByteArray();
        oo.close();
        return data;
    }

    @Override
    public void printLog(String text) {
        System.out.println("[Server:] " + text);
    }


    public static void main(String[] args) {
        new Server("test", "//").waitToBeDiscovered();
    }
}
