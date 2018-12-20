package ir.ac.aut.ceit.cn.CustomMessage;

public abstract class CustomMessage {

    protected byte[] serialized;


    public abstract byte getMessageType();

    // to bytes
    protected abstract void serialize();

    // from bytes
    protected abstract void deserialize();

    public byte[] getSerialized() {
        return serialized;
    }
}
