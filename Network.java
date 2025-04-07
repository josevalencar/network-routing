import java.util.HashMap;
import java.util.Map;

public class Network {
    
    private final Map<String, Router> routers;

    public void startLinkStateRouting(){
        for (Router router : routers.values()){
            router.startLinkStateRouting();
        }
    }

    public void printAllRoutingTables(){
        for (Router router : routers.values()){
            System.out.println("\n" + (router.isGateway() ? "GATEWAY " : "") + 
                              "Roteador " + router.getIp() + 
                              " (Subrede: " + router.getSubnet() + ")");
            router.getRoutingTable().printTable();
        }
    }

    public Network() {
        this.routers = new HashMap<>();
    }

    public void addRouter(Router router){
        routers.put(router.getIp(), router);
    }

    public boolean connectRouters(String ip1, String ip2, int latency){
        Router router1 = routers.get(ip1);
        Router router2 = routers.get(ip2);

        if (router1 == null || router2 == null) {
            return false;
        }

        router1.addNeighbor(ip2, latency);
        router2.addNeighbor(ip1, latency);
        
        return true;
    }

    public boolean deliverMessage(Message message, String routerIp) {
        Router router = routers.get(routerIp);
        
        if (router == null) {
            return false;
        }
        
        router.receiveMessage(message);
        return true;
    }

    public Router getRouter(String ip) {
        return routers.get(ip);
    }

    public Map<String, Router> getAllRouters() {
        return new HashMap<>(routers);
    }

}
