import java.util.HashMap;
import java.util.Map;

public class RoutingTable {
    private final String routerIp;
    private final Map<String, RouteEntry> routes;

    public RoutingTable(String routerIp) {
        this.routerIp = routerIp;
        this.routes = new HashMap<>();
        
        addRoute(routerIp, routerIp, 0);
    }

    public boolean addRoute(String destinationIp, String nextHopIp, int totalLatency) {
        RouteEntry currentRoute = routes.get(destinationIp);
        
        if (currentRoute == null || totalLatency < currentRoute.getTotalLatency()) {
            routes.put(destinationIp, new RouteEntry(nextHopIp, totalLatency));
            return true;
        }
        
        return false;
    }


    public String getNextHop(String destinationIp) {
        RouteEntry entry = routes.get(destinationIp);
        return entry != null ? entry.getNextHop() : null;
    }


    public int getTotalLatency(String destinationIp) {
        RouteEntry entry = routes.get(destinationIp);
        return entry != null ? entry.getTotalLatency() : -1;
    }

    public boolean hasRoute(String destinationIp) {
        return routes.containsKey(destinationIp);
    }

    public Map<String, RouteEntry> getAllRoutes() {
        return new HashMap<>(routes);
    }

    public void printTable() {
        System.out.println("Tabela de Roteamento para " + routerIp + ":");
        System.out.println("+-----------------+-----------------+---------------+");
        System.out.println("| Destino         | Próximo Salto   | Latência (ms) |");
        System.out.println("+-----------------+-----------------+---------------+");
        
        for (Map.Entry<String, RouteEntry> entry : routes.entrySet()) {
            String destinationIp = entry.getKey();
            RouteEntry route = entry.getValue();
            
            System.out.printf("| %-15s | %-15s | %-13d |\n", 
                  destinationIp, 
                  route.getNextHop().equals(routerIp) ? "direto" : route.getNextHop(), 
                  route.getTotalLatency());
        }
        
        System.out.println("+-----------------+-----------------+---------------+");
    }


    public static class RouteEntry {
        private final String nextHop;
        private final int totalLatency;

        public RouteEntry(String nextHop, int totalLatency) {
            this.nextHop = nextHop;
            this.totalLatency = totalLatency;
        }

        public String getNextHop() {
            return nextHop;
        }

        public int getTotalLatency() {
            return totalLatency;
        }
    }
}