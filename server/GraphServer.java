package server;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class GraphServer extends UnicastRemoteObject implements GraphService {
    private static final long serialVersionUID = 1L;
    private Graph graph;
    private Logger logger;
    private int requestCount;
    private long totalProcessingTime;

    protected GraphServer() throws RemoteException {
        super();
        graph = new Graph();
        setupLogger();
        requestCount = 0;
        totalProcessingTime = 0;
        initializeGraphFromFile();
    }

    private void setupLogger() {
        try {
            logger = Logger.getLogger("GraphServer");
            FileHandler fh = new FileHandler("server.log");
            logger.addHandler(fh);
            SimpleFormatter formatter = new SimpleFormatter();
            fh.setFormatter(formatter);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initializeGraphFromFile() {
        try (BufferedReader reader = new BufferedReader(new FileReader("test/initial_graph.txt"))) {
            List<String> edges = new ArrayList<>();
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.trim().equals("S")) {
                    break;
                }
                edges.add(line.trim());
            }
            graph.initializeFromEdges(edges);
            logger.info("Graph initialized from file with " + edges.size() + " edges");
        } catch (IOException e) {
            logger.severe("Error reading initial graph file: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public void initializeGraph(List<String> edges) throws RemoteException {
        long startTime = System.currentTimeMillis();
        graph.initializeFromEdges(edges);
        long endTime = System.currentTimeMillis();
        logger.info("Graph initialized with " + edges.size() + " edges in " + (endTime - startTime) + "ms");
    }

    @Override
    public void signalReady() throws RemoteException {
        logger.info("Server is ready to process requests");
    }

    @Override
    public List<Integer> processBatch(List<String> operations) throws RemoteException {
        long startTime = System.currentTimeMillis();
        List<Integer> results = new ArrayList<>();
        
        for (String operation : operations) {
            if (operation.equals("F")) continue;  // Skip the batch end marker
            
            String[] parts = operation.trim().split("\\s+");
            if (parts.length != 3) continue;

            char op = parts[0].charAt(0);
            int from = Integer.parseInt(parts[1]);
            int to = Integer.parseInt(parts[2]);

            switch (op) {
                case 'Q':
                    results.add(graph.getShortestPath(from, to));
                    break;
                case 'A':
                    graph.addEdge(from, to);
                    break;
                case 'D':
                    graph.removeEdge(from, to);
                    break;
            }
        }

        long endTime = System.currentTimeMillis();
        long processingTime = endTime - startTime;
        totalProcessingTime += processingTime;
        requestCount++;

        logger.info(String.format("Processed batch of %d operations in %dms. Average processing time: %dms",
                operations.size() - 1, processingTime, totalProcessingTime / requestCount));

        return results;
    }

    public static void main(String[] args) {
        try {
            // Set the hostname property
            System.setProperty("java.rmi.server.hostname", "localhost");
            
            // Create and export the remote object
            GraphServer server = new GraphServer();
            
            // Create the registry
            Registry registry = LocateRegistry.createRegistry(1099);
            
            // Bind the remote object to the registry
            registry.rebind("GraphService", server);
            
            System.out.println("Server is ready");
        } catch (Exception e) {
            System.err.println("Server exception: " + e.toString());
            e.printStackTrace();
        }
    }
} 