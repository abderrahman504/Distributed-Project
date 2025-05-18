import java.rmi.registry.Registry;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;
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
		String usage = "Usage : java Client <client id> <Use concurrent> <Option>\n" + 
		"Client id | A unique integer id for each client.\n" +
		"Use concurrent | Set to true to use concurrent method, and to false to use sequential method." + 
		"Option | {-read} for reading the batch from standard input. {-random <graph size> <batch size> <write ratio>} to geenrate random batches.";
		if (args.length != 3 && args.length != 6){
			//System.err.println("Usage : java Client <client id> <true -> concurrent. false -> sequential> <true -> random batches. false -> ask for batches>");
			System.err.println(usage);
			return;
		}
		clientId = Integer.parseInt(args[0]);
		boolean use_concurrent = Boolean.parseBoolean(args[1]);
		boolean random_batches = false;
		int graph_size = 0, batch_size = 0;
		double write_ratio = 0;
		
		// Reading the third argument
		if(args[2].equals("-read") && args.length == 3){
			random_batches = false;
		}
		else if (args[2].equals("-random") && args.length == 6){
			random_batches = true;
			graph_size = Integer.parseInt(args[3]);
			batch_size = Integer.parseInt(args[4]);
			write_ratio = Double.parseDouble(args[5]);
		}
		else{
			System.err.println(usage);
			return;	
		}
		

		setupLogger();
		try{
			Registry registry = LocateRegistry.getRegistry();
			System.out.println("Found registry");
			RemoteInterface obj = (RemoteInterface) registry.lookup("Graph Server");
			System.out.println("Client ready");
	
			
			while (true){
				List<String> batch;
				if (random_batches) batch = generateBatch(graph_size, batch_size, write_ratio);
				else batch = readBatch();
				StringBuilder completeBatch = new StringBuilder();
				for (String str : batch){
					completeBatch.append(str);
					completeBatch.append('\n');
				}

				completeBatch.append('F');
				long t_before = System.currentTimeMillis();		
				logger.info("Batch :\n" + completeBatch.toString());
				try{
					String result;
					if (use_concurrent) result = obj.concurrentProcessBatch(clientId, completeBatch.toString());
					else result = obj.sequentialProcessBatch(clientId, completeBatch.toString());

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


	/**
	 * Reads a batch from standard input.
	 * @return the batch as a list of operations.
	 */
	private static List<String> readBatch(){
		List<String> batch = new ArrayList<>();
		System.out.println("Enter batch of operations:");
		Scanner scn = new Scanner(System.in);
		while(scn.hasNextLine()){
			String line = scn.nextLine();
			batch.add(line);
			if (line.equals("F")) break;
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
