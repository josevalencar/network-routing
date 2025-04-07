import java.util.*;

public class Router {
    
    private final String ip;
    private final Map<String, Integer> neighbors;
    private final RoutingTable routingTable;
    private final Map<String, LinkState> linkStateDatabase;
    private final Set<String> processedMessages; 
    private final Network network;
    
    private final String subnetMask;

    public Router(String ip, String subnetMask, Network network) {
        this.ip = ip;
        this.subnetMask = subnetMask;
        this.network = network;
        this.neighbors = new HashMap<>();
        this.routingTable = new RoutingTable(ip);
        this.linkStateDatabase = new HashMap<>();
        this.processedMessages = new HashSet<>();
    }

    public void addNeighbor(String neighborIp, int latency){
        neighbors.put(neighborIp, latency);

        routingTable.addRoute(neighborIp, neighborIp, latency);
    }

    public int getLatencyTo(String neighborIp) {
        return neighbors.getOrDefault(neighborIp, -1);
    }

    public Map<String, Integer> getNeighbors() {
        return new HashMap<>(neighbors);
    }

    public boolean isSameSubnet(String otherIp){
        String[] parts1 = ip.split("\\.");
        String[] parts2 = otherIp.split("\\.");
        String[] mask = subnetMask.split("\\.");

        for (int i = 0; i<4; i++) {
            if(mask[i].equals("255")){
                if (!parts1[i].equals(parts2[i])){
                    return false;
                }
            }
        }

        return true;
    }

    public String getSubnet() {
        String[] ipParts = ip.split("\\.");
        String[] maskParts = subnetMask.split("\\.");
        
        StringBuilder subnet = new StringBuilder();
        for (int i = 0; i < 4; i++) {
            if (maskParts[i].equals("255")) {
                subnet.append(ipParts[i]).append(".");
            } else {
                break;
            }
        }
        
        if (subnet.length() > 0 && subnet.charAt(subnet.length() - 1) == '.') {
            subnet.setLength(subnet.length() - 1);
        }
        
        return subnet.toString();
    }

    public void startLinkStateRouting(){
        LinkState linkState = new LinkState(ip);

        for(Map.Entry<String, Integer> neighbor : neighbors.entrySet()) {
            linkState.addNeighbor(neighbor.getKey(), neighbor.getValue());
        }

        linkStateDatabase.put(ip, linkState);

        Message message = new Message(
            ip,
            "255.255.255.255",
            linkState,
            10,
            Message.MessageType.LINK_STATE
        );

        sendMessage(message);
    }

    public void sendMessage(Message message) {
        if (processedMessages.contains(message.getId())) {
            return;
        }

        processedMessages.add(message.getId());
        
        if (message.isBroadcast()) {
            for (String neighborIp : neighbors.keySet()) {
                System.out.println("Roteador " + ip + " enviando broadcast para " + neighborIp);
                network.deliverMessage(message, neighborIp);
            }
        } 
        else {
            String destination = message.getDestination();
            String nextHop = routingTable.getNextHop(destination);
            
            if (nextHop != null) {
                System.out.println("Roteador " + ip + " enviando mensagem para " + destination + 
                               " via próximo salto: " + nextHop);
                network.deliverMessage(message, nextHop);
            } else {
                System.out.println("Roteador " + ip + ": não há rota para " + destination);
            }
        }
    }

    public void receiveMessage(Message message) {
        if (processedMessages.contains(message.getId())) {
            return;
        }
        
        processedMessages.add(message.getId());
        
        boolean valid = message.decrementHopLimit();
        if (!valid) {
            return;
        }
        
        if (message.getType() == Message.MessageType.LINK_STATE) {
            processLinkStateMessage(message);
        } else if (message.getDestination().equals(ip)) {
            System.out.println("Roteador " + ip + " recebeu mensagem: " + message.getPayload());
        } else if (message.isBroadcast() || !message.getDestination().equals(ip)) {
            sendMessage(message);
        }
    }

    private void processLinkStateMessage(Message message) {
        LinkState linkState = (LinkState) message.getPayload();
        String sourceRouterIp = linkState.getRouterIp();
        
        LinkState existingLinkState = linkStateDatabase.get(sourceRouterIp);
        if (existingLinkState != null && existingLinkState.getTimestamp() >= linkState.getTimestamp()) {
            return;
        }
        
        linkStateDatabase.put(sourceRouterIp, linkState);
        
        for (String neighborIp : neighbors.keySet()) {
            if (!neighborIp.equals(message.getSource())) {
                Message forwardMessage = new Message(
                    ip,
                    neighborIp,
                    linkState,
                    message.getHopLimit(),
                    Message.MessageType.LINK_STATE
                );
                network.deliverMessage(forwardMessage, neighborIp);
            }
        }
        
        recalculateRoutes();
    }

    public void recalculateRoutes(){
        Map<String, Integer> distance = new HashMap<>();
        Map<String, String> predecessor = new HashMap<>();

        Set<String> unvisited = new HashSet<>();

        for (String routerIp : linkStateDatabase.keySet()){
            unvisited.add(routerIp);
            distance.put(routerIp, Integer.MAX_VALUE);
        }

        distance.put(ip, 0);

        while (!unvisited.isEmpty()){
            String current = null;
            int minDistance = Integer.MAX_VALUE;

            for (String node : unvisited) {
                int nodeDist = distance.get(node);
                if (nodeDist < minDistance) {
                    minDistance = nodeDist;
                    current = node;
                }
            }

            if (current == null){
                break;
            }

            unvisited.remove(current);

            if (distance.get(current) == Integer.MAX_VALUE) {
                break;
            }

            LinkState currentLinkState = linkStateDatabase.get(current);
            if (currentLinkState == null) {
                continue;
            }

            for (Map.Entry<String, Integer> neighborEntry : currentLinkState.getNeighbors().entrySet()) {
                String neighbor = neighborEntry.getKey();
                int weight = neighborEntry.getValue();
                
                int newDist = distance.get(current) + weight;
                
                if (newDist < distance.getOrDefault(neighbor, Integer.MAX_VALUE)) {
                    distance.put(neighbor, newDist);
                    predecessor.put(neighbor, current);
                }
            }
        }

        for (String dest : distance.keySet()) {
            if (!dest.equals(ip) && distance.get(dest) < Integer.MAX_VALUE) {
                String nextHop = dest;
                String prev = dest;
                
                while (predecessor.containsKey(prev) && !predecessor.get(prev).equals(ip)) {
                    prev = predecessor.get(prev);
                    nextHop = prev;
                }
                
                if (predecessor.containsKey(dest) && predecessor.get(dest).equals(ip)) {
                    nextHop = dest;
                }
                
                routingTable.addRoute(dest, nextHop, distance.get(dest));
            }
        }
    }

    public String getIp() {
        return ip;
    }

    public RoutingTable getRoutingTable() {
        return routingTable;
    }

    public boolean isGateway() {
        String mySubnet = getSubnet();
        
        for (String neighborIp : neighbors.keySet()) {
            Router neighbor = network.getRouter(neighborIp);
            if (neighbor != null && !neighbor.getSubnet().equals(mySubnet)) {
                return true;
            }
        }
        
        return false;
    }
}
