package zokoo.wsd_chat.Entities;

import java.io.Serializable;

/**
 * Created by Piotyr-Szef on 17.12.2017.
 */

public class Message implements Serializable{
    private byte[] data;
    private String from;
    private String to;
    private long id;
    private long validUntil;

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getValidUntil() {
        return validUntil;
    }

    public void setValidUntil(long validUntil) {
        this.validUntil = validUntil;
    }
}
