package zokoo.wsd_chat.Entities;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Piotyr-Szef on 17.12.2017.
 */

public class Container implements Serializable {

    private byte[] privateKey;
    private byte[] publicKey;
    private List<Message> messages;

    public byte[] getPrivateKey() {
        return privateKey;
    }

    public void setPrivateKey(byte[] privateKey) {
        this.privateKey = privateKey;
    }

    public byte[] getPublicKey() {
        return publicKey;
    }

    public void setPublicKey(byte[] publicKey) {
        this.publicKey = publicKey;
    }

    public List<Message> getMessages() {
        return messages;
    }

    public void setMessages(List<Message> messages) {
        this.messages = messages;
    }
}
