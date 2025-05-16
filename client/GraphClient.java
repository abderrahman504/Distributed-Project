package client;

import server.GraphService;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public class GraphClient {
    private static final Random random = new Random();
    private final Logger logger;
    private final GraphService graphService;
    private final int clientId;

    public GraphClient(int clientId) throws Exception {
        this.clientId = clientId;
        this.logger = setupLogger();
        
        // Look up the remote object
        Registry registry = LocateRegistry.getRegistry("localhost", 1099);
        graphService = (GraphService) registry.lookup("GraphService");
    }

    private Logger setupLogger() {
        try {
            Logger logger = Logger.getLogger("GraphClient" + clientId);
            FileHandler fh = new FileHandler("client" + clientId + ".log");
            logger.addHandler(fh);
            SimpleFormatter formatter = new SimpleFormatter();
            fh.setFormatter(formatter);
            return logger;
        } catch (Exception e) {
            e.printStackTrace();
            // Return a basic logger if file handler setup fails
            return Logger.getLogger("GraphClient" + clientId);
        }
    }

    private List<String> generateBatch(int size, double writePercentage) {
        List<String> batch = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            if (random.nextDouble() < writePercentage) {
                // Generate write operation (A or D)
                char op = random.nextBoolean() ? 'A' : 'D';
                int from = random.nextInt(4) + 1;  // Random node 1-4
                int to = random.nextInt(4) + 1;    // Random node 1-4
                batch.add(op + " " + from + " " + to);
            } else {
                // Generate query operation
                int from = random.nextInt(4) + 1;  // Random node 1-4
                int to = random.nextInt(4) + 1;    // Random node 1-4
                batch.add("Q " + from + " " + to);
            }
        }
        return batch;
    }

    public void run() {
        try {
            // Generate and process batches
            for (int batchNum = 1; batchNum <= 5; batchNum++) {
                // Generate a batch with 5 operations, 50% write operations
                List<String> batch = generateBatch(5, 0.5);

                // Log the batch operations
                logger.info("Batch " + batchNum + " operations:");
                for (String operation : batch) {
                    logger.info("  " + operation);
                }
                
                // Add batch end marker
                batch.add("F");
                
                // Process the batch
                long startTime = System.currentTimeMillis();
                List<Integer> results = graphService.processBatch(batch);
                long endTime = System.currentTimeMillis();
                
                // Log results
                logger.info(String.format("Batch %d processed in %dms", batchNum, endTime - startTime));
                // logger.info("Results: " + results);
                logger.info("Results for batch " + batchNum + ":");
                for (int i = 0; i < results.size(); i++) {
                    logger.info("  Query " + (i + 1) + ": " + results.get(i));
                }
                
                // Sleep for random time between 1-10 seconds
                Thread.sleep(random.nextInt(9000) + 1000);
            }
        } catch (Exception e) {
            logger.severe("Error in client: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        try {
            GraphClient client = new GraphClient(1);  // Start with client ID 1
            client.run();
        } catch (Exception e) {
            System.err.println("Client exception: " + e.toString());
            e.printStackTrace();
        }
    }
}