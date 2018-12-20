package ir.ac.aut.ceit.cn.Message;

import java.io.Serializable;

public class IHaveMessage extends Message implements Serializable {
    private String fileName;
    private long fileSize;

    public IHaveMessage(String fileName, long fileSize) {
        this.fileName = fileName;
        this.fileSize = fileSize;
    }

    @Override
    public byte getType() {
        return MessageTypes.I_HAVE_MESSAGE;
    }

    public long getFileSize() {
        return fileSize;
    }

    @Override
    public String toString() {
        return "IHaveMessage{" +
                "fileName='" + fileName + '\'' +
                ", fileSize=" + fileSize +
                '}';
    }
}
