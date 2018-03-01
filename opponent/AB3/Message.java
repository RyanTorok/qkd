package qkd;

import java.io.Serializable;

public class Message implements Serializable {
    Identity recipient;
    Object message;
    public Message(Identity recipient, Object message) {
        this.recipient = recipient;
        this.message = message;
    }
}
