import java.io.File;
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
		int reg_port = 1099; // The port from which the rmi registry can be accessed.
		int obj_port = 0; // The port from which the remote object can be accessed.
		if (args.length != 0) reg_port = Integer.parseInt(args[0]);
		if (args.length == 2) obj_port = Integer.parseInt(args[1]);
		
		RemoteObject obj = new RemoteObject();

		try{
			obj.logger = setupLogger();
		} catch (Exception e){
			System.err.println("Failed to create logger. Quitting...");
			return;
		}
		
		Map<Integer, Set<Integer>> graph = readFromFile("initial.txt");
		obj.setGraph(graph);
		
		try{
			LocateRegistry.createRegistry(reg_port);
			obj.logger.info("RMI Registry setup on port " + reg_port);
			UnicastRemoteObject.exportObject(obj, obj_port);
			
			Registry registry = LocateRegistry.getRegistry(reg_port);
			registry.bind("Graph Server", obj);
			obj.logger.info("Remote Object available at port " + obj_port);
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


	private static Map<Integer, Set<Integer>> readFromStdIn()
	{
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
		scn.close();
		return graph;
	}
	
	private static Map<Integer, Set<Integer>> readFromFile(String path){
		System.out.println("Reading graph from " + path);
		Map<Integer, Set<Integer>> graph = new HashMap<>();
		try{
			Scanner scn = new Scanner(new File(path));
			while(scn.hasNextInt()){
				int from = scn.nextInt();
				int to = scn.nextInt();
				if (!graph.containsKey(from))
				graph.put(from, new HashSet<Integer>());
					
				if (!graph.containsKey(to))
				graph.put(to, new HashSet<Integer>());
				
				graph.get(from).add(to);
			}
			scn.close();
			return graph;
		}
		catch (Exception e){
			System.err.println(e.getMessage());
			return null;
		}

	}
    
}
