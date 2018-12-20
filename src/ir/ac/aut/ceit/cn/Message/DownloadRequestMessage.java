package ir.ac.aut.ceit.cn.Message;

import java.io.Serializable;

public class DownloadRequestMessage extends Message implements Serializable {
    @Override
    public byte getType() {
        return MessageTypes.DOWNLOAD_REQUEST_MESSAGE;
    }
}
