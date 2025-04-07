public class Main {
    public static void main(String[] args){
        Network network = new Network();

        Router routerA = new Router("192.168.1.1", "255.255.255.0", network);
        Router routerB = new Router("192.168.1.2", "255.255.255.0", network);
        Router routerC = new Router("192.168.1.3", "255.255.255.0", network);

        Router routerD = new Router("192.168.2.1", "255.255.255.0", network);
        Router routerE = new Router("192.168.2.2", "255.255.255.0", network);

        network.addRouter(routerA);
        network.addRouter(routerB);
        network.addRouter(routerC);
        network.addRouter(routerD);
        network.addRouter(routerE);

        network.connectRouters(routerA.getIp(), routerB.getIp(), 5);  // A-B: 5ms
        network.connectRouters(routerA.getIp(), routerC.getIp(), 10); // A-C: 10ms
        network.connectRouters(routerB.getIp(), routerC.getIp(), 3);  // B-C: 3ms
        network.connectRouters(routerC.getIp(), routerD.getIp(), 7);  // C-D: 7ms (esse eh o gateway)
        network.connectRouters(routerD.getIp(), routerE.getIp(), 4);  // D-E: 4ms

        System.out.println("Rede criada: ");
        System.out.println("- Roteadores na subrede 192.168.1.0/24: A(192.168.1.1), B(192.168.1.2), C(192.168.1.3)");
        System.out.println("- Roteadores na subrede 192.168.2.0/24: D(192.168.2.1), E(192.168.2.2)");
        System.out.println("- C e D atuam como gateways entre as subredes");
        System.out.println("\nConexões (arestas do grafo):");
        System.out.println("- A-B: 5ms");
        System.out.println("- A-C: 10ms");
        System.out.println("- B-C: 3ms");
        System.out.println("- C-D: 7ms");
        System.out.println("- D-E: 4ms");

        System.out.println("\n=== Tabelas de roteamento iniciais (apenas rotas diretas) ===");
        network.printAllRoutingTables();

        network.startLinkStateRouting();

        try {
            System.out.println("Aguardando propagação das mensagens de estado de link...");
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println("\n=== Tabelas de roteamento finais (após link state routing) ===");
        network.printAllRoutingTables();

        Message message = new Message(
            routerA.getIp(),
            routerE.getIp(),
            "Eae do roteador A para o roteador E",
            10,
            Message.MessageType.DATA
        );

        System.out.println("Enviando mensagem de A para E..");
        routerA.sendMessage(message);
    }
}
