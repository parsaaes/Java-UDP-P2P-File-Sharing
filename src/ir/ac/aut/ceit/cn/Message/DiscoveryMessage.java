package ir.ac.aut.ceit.cn.Message;

import java.io.Serializable;

public class DiscoveryMessage extends Message implements Serializable {

    public static String firstLine = "File Request:";
    private String fileName;

    public DiscoveryMessage(String fileName) {
        this.fileName = fileName;
    }

    public String getFileName() {
        return fileName;
    }

    @Override
    public byte getType() {
        return MessageTypes.DISCOVERY_MESSAGE;
    }

    public static int getMaxValidSize() {
        return (MAX - firstLine.getBytes().length);
    }
}
