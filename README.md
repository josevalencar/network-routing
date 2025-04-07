# Network Routing Simulation

A Java-based network routing simulation that implements the Link State Routing Protocol. This project demonstrates how routers in a network discover their neighbors, build routing tables, and forward messages using the shortest path algorithm.

## Overview

This project simulates a network of routers that can:
- Discover and maintain information about their neighbors
- Build and update routing tables using the Link State Routing Protocol
- Forward messages between routers using the optimal path
- Handle communication across different subnets through gateway routers

## How It Works

The simulation consists of several key components:

1. **Routers**: Each router has:
   - A unique IP address
   - A subnet mask
   - A routing table
   - A link state database
   - Information about its neighbors and connection latencies

2. **Network**: The network class manages:
   - Router connections
   - Message delivery between routers
   - Network topology

3. **Message Types**:
   - Link State Messages: Used for router discovery and topology updates
   - Data Messages: Used for actual data transmission between routers

## Algorithms

The project implements two main algorithms:

1. **Link State Routing Protocol**:
   - Each router broadcasts information about its directly connected neighbors
   - Routers maintain a database of the entire network topology
   - Uses Dijkstra's algorithm to calculate the shortest path to all destinations

2. **Dijkstra's Shortest Path Algorithm**:
   - Used to calculate the optimal routes in the network
   - Considers link latencies as edge weights
   - Updates routing tables with the best paths to each destination

## How to Run

### Prerequisites
- Java Development Kit (JDK) 8 or higher

### Running the Simulation

1. Compile the Java files:
```bash
javac *.java
```

2. Run the main program:
```bash
java Main
```

The simulation will:
1. Create a network with multiple routers
2. Establish connections between routers
3. Initialize routing tables
4. Run the Link State Routing Protocol
5. Display the final routing tables
6. Demonstrate message routing between routers

### Example Network Topology
The default simulation creates a network with:
- Subnet 192.168.1.0/24: Routers A, B, and C
- Subnet 192.168.2.0/24: Routers D and E
- Various connections with different latencies
- Gateway routers connecting the subnets

## Project Structure

- `Main.java`: Entry point and network setup
- `Router.java`: Router implementation and routing logic
- `Network.java`: Network topology and message delivery
- `RoutingTable.java`: Routing table management
- `LinkState.java`: Link state information
- `Message.java`: Message handling and types

## Features

- Dynamic routing table updates
- Cross-subnet communication
- Latency-based path selection
- Message forwarding with hop limits
- Network topology visualization
- Gateway router functionality 