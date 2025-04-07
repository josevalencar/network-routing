import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class LinkState implements Serializable {
    private final String routerIp;
    private final Map<String, Integer> neighbors;
    private final long timestamp;

    public LinkState(String routerIp) {
        this.routerIp = routerIp;
        this.neighbors = new HashMap<>();
        this.timestamp = System.currentTimeMillis();
    }

    public void addNeighbor(String neighborIp, int latency) {
        neighbors.put(neighborIp, latency);
    }

    public int getLatencyTo(String neighborIp) {
        return neighbors.getOrDefault(neighborIp, -1);
    }

    public Map<String, Integer> getNeighbors() {
        return new HashMap<>(neighbors);
    }

    public String getRouterIp() {
        return routerIp;
    }

    public long getTimestamp() {
        return timestamp;
    }

    @Override
    public String toString() {
        return "LinkState{routerIp='" + routerIp + "', neighbors=" + neighbors + '}';
    }
}