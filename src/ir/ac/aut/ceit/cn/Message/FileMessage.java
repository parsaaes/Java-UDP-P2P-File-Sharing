package ir.ac.aut.ceit.cn.Message;

import java.io.Serializable;

public class FileMessage extends Message implements Serializable {
    public static int MAX_PACKET_SIZE = 30000;
    private static String firstLine = "File response:";
    private int offset;
    private byte[] data;

    public FileMessage(int offset, byte[] data) {
        this.offset = offset;
        this.data = data;
    }

    public static int getMaximumSize() {
        return 10000 + (firstLine.length() + 4 + 4);
    }

    public int getOffset() {
        return offset;
    }

    public byte[] getData() {
        return data;
    }

    @Override
    public byte getType() {
        return MessageTypes.FILE_MESSAGE;
    }

    public static int getChunkSize(long fileSize) {
        return (int)Math.ceil((double)fileSize / (double)MAX_PACKET_SIZE);
    }
}
