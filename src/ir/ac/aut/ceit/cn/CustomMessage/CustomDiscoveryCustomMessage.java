package ir.ac.aut.ceit.cn.CustomMessage;

import java.nio.ByteBuffer;

public class CustomDiscoveryCustomMessage extends CustomMessage {
    private static String originalFirstLine = "File Request:\n";
    private String recvdFirstLine;
    private String fileName;

    public static int MAXIMUM_SIZE = 10000 - (4 + 1 + 1 + 4 + originalFirstLine.getBytes().length + 4);


    public CustomDiscoveryCustomMessage(String fileName) {
        this.fileName = fileName;
        serialize();
    }

    public CustomDiscoveryCustomMessage(byte[] serialized) {
        this.serialized = serialized;
        deserialize();
    }

    public boolean checkSizeIsValid(String name) {
        return name.getBytes().length <= MAXIMUM_SIZE;
    }

    @Override
    public byte getMessageType() {
        return CustomMessageTypes.DISCOVERY_MESSAGE;
    }

    @Override
    protected void serialize() {
        int firstLineLength = originalFirstLine.getBytes().length;
        int fileNameLength = fileName.getBytes().length;
        // messageLength + ProtocolVer + MessageType + [data]
        int messageLength = 4 + 1 + 1 + 4 + firstLineLength + 4 + fileNameLength;
        ByteBuffer byteBuffer = ByteBuffer.allocate(messageLength);
        byteBuffer.putInt(messageLength);
        byteBuffer.put(CustomMessageTypes.PROTOCOL_VER);
        byteBuffer.put(CustomMessageTypes.DISCOVERY_MESSAGE);
        byteBuffer.putInt(firstLineLength);
        byteBuffer.put(originalFirstLine.getBytes());
        byteBuffer.putInt(fileNameLength);
        byteBuffer.put(fileName.getBytes());
        serialized = byteBuffer.array();
    }

    @Override
    protected void deserialize() {
        ByteBuffer byteBuffer = ByteBuffer.wrap(serialized);
        int messageLength = byteBuffer.getInt();
        byte protocolVersion = byteBuffer.get();
        byte messageType = byteBuffer.get();
        int firstLineLength = byteBuffer.getInt();
        byte[] firstLineBytes = new byte[firstLineLength];
        byteBuffer.get(firstLineBytes);
        recvdFirstLine = new String(firstLineBytes);
        int fileNameLength = byteBuffer.getInt();
        byte[] fileNameBytes = new byte[fileNameLength];
        byteBuffer.get(fileNameBytes);
        fileName = new String(fileNameBytes);
    }

    public String getFileName() {
        return fileName;
    }
}
