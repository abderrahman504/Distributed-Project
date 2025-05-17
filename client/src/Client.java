import java.rmi.registry.Registry;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;


public class Client 
{
	static Logger logger;
	static int clientId;
	static Random random = new Random();

    public static void main(String[] args)
	{
		if (args.length != 1){
			System.err.println("Usage : java Client <client id>");
			return;
		}
		clientId = Integer.parseInt(args[0]);
		setupLogger();
		try{
			Registry registry = LocateRegistry.getRegistry();
			System.out.println("Found registry");
			RemoteInterface obj = (RemoteInterface) registry.lookup("Graph Server");
			System.out.println("Client ready");
	
			
			while (true){
				List<String> batch = generateBatch(5, 5, 0.8);
				StringBuilder completeBatch = new StringBuilder();
				for (String str : batch){
					completeBatch.append(str);
					completeBatch.append('\n');
				}
				completeBatch.append('F');
				long t_before = System.currentTimeMillis();		
				logger.info("Batch :\n" + completeBatch.toString());
				try{
					String result = obj.processBatch(clientId, completeBatch.toString());
					//System.out.println("Batch sent");
					
					long t_after = System.currentTimeMillis();
					long response_time = t_after - t_before;
					
					// Compose log message
					StringBuilder msg = new StringBuilder();
					msg.append("Response time : " + response_time + " ms\n");
					msg.append("Result :\n" + result);
					logger.info(msg.toString());
				}
				catch (RemoteException e){
					//System.err.println("Remote exception: " + e.getMessage());
					logger.severe("Remote exception: " + e.getMessage());
				}
				
				int sleep_duration = (Math.abs(random.nextInt()) + 1000) % 10_000; // sleep for 1-10 seconds
				System.out.println("Sleeping for " + sleep_duration + " ms");
				Thread.sleep(sleep_duration);
			}

		}
		catch (Exception e){
			//System.err.println("Client exception: " + e.toString());
			logger.severe("Client exception: " + e.getMessage());
		}

    }

	/**
	 * 
	 * @param num_nodes specifies the max node id that can be in the batch.
	 * @param size is the number of operations in the batch.
	 * @param writePercentage is the percentage of write operations in the batch.
	 * @return a list of operations as strings.
	 */
	private static List<String> generateBatch(int num_nodes, int size, double writePercentage) {
        List<String> batch = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            if (random.nextDouble() < writePercentage) {
                // Generate write operation (A or D)
                char op = random.nextBoolean() ? 'A' : 'D';
                int from = random.nextInt(num_nodes) + 1;  // Random node 1-4
                int to = random.nextInt(num_nodes) + 1;    // Random node 1-4
                batch.add(op + " " + from + " " + to);
            } else {
                // Generate query operation
                int from = random.nextInt(num_nodes) + 1;  // Random node 1-4
                int to = random.nextInt(num_nodes) + 1;    // Random node 1-4
                batch.add("Q " + from + " " + to);
            }
        }
        return batch;
    }


	static Logger setupLogger() {
        try {
            logger = Logger.getLogger("Client " + clientId);
            FileHandler fh = new FileHandler("log" + clientId + ".txt");
            logger.addHandler(fh);
            SimpleFormatter formatter = new SimpleFormatter();
            fh.setFormatter(formatter);
            return logger;
        } catch (Exception e) {
            e.printStackTrace();
            // Return a basic logger if file handler setup fails
            return Logger.getLogger("Client " + clientId);
        }
    }
}
