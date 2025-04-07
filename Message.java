import java.util.UUID;

public class Message {
    public enum MessageType {
        DATA,
        LINK_STATE
    }

    private final String id;
    private final String source; 
    private final String destination;
    private final Object payload;
    private int hopLimit;
    private final MessageType type;

    public Message(String source, String destination, Object payload, int hopLimit, MessageType type) {
        this.id = UUID.randomUUID().toString();
        this.source = source;
        this.destination = destination;
        this.payload = payload;
        this.hopLimit = hopLimit;
        this.type = type;
    }


    public boolean decrementHopLimit() {
        hopLimit--;
        return hopLimit > 0;
    }

    public boolean isBroadcast() {
        return "255.255.255.255".equals(destination);
    }

    public String getId() {
        return id;
    }

    public String getSource() {
        return source;
    }

    public String getDestination() {
        return destination;
    }

    public Object getPayload() {
        return payload;
    }

    public int getHopLimit() {
        return hopLimit;
    }

    public MessageType getType() {
        return type;
    }

    @Override
    public String toString() {
        return "Message{" +
                "id='" + id + '\'' +
                ", source='" + source + '\'' +
                ", destination='" + destination + '\'' +
                ", payload=" + payload +
                ", hopLimit=" + hopLimit +
                ", type=" + type +
                '}';
    }
}