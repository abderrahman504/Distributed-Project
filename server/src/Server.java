import java.io.IOException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public class Server
{
	
	public static void main(String[] args) {

		RemoteObject obj = new RemoteObject();

		try{
			obj.logger = setupLogger();
		} catch (Exception e){
			System.err.println("Failed to create logger. Quitting...");
			return;
		}
		// Read the initial graph from standard input and call obj.setGraph().
		System.out.println("Enter initial graph:");
		Scanner scn = new Scanner(System.in);
		Map<Integer, Set<Integer>> graph = new HashMap<>();
		while(scn.hasNextInt()){
			int from = scn.nextInt();
			int to = scn.nextInt();
			if (!graph.containsKey(from))
				graph.put(from, new HashSet<Integer>());
				
			if (!graph.containsKey(to))
				graph.put(to, new HashSet<Integer>());
		
			graph.get(from).add(to);
		}
		obj.setGraph(graph);
		scn.close();

		try{
			LocateRegistry.createRegistry(1099);
			UnicastRemoteObject.exportObject(obj, 0);

			Registry registry = LocateRegistry.getRegistry();
			registry.bind("Graph Server", obj);
			System.out.println("Server ready");
		}
		catch (Exception e){
			System.err.println("Server exception: " + e.toString());
		}

	}

	private static Logger setupLogger() throws Exception {
		Logger logger = Logger.getLogger("GServer");
		FileHandler fh = new FileHandler("server_log.txt");
		logger.addHandler(fh);
		SimpleFormatter formatter = new SimpleFormatter();
		fh.setFormatter(formatter);
		return logger;
	}
    
}
