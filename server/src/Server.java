import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Scanner;

public class Server extends RemoteObject
{
	
	public static void main(String[] args) {
		
		if (args.length < 1){
			System.out.println("Usage: java Server" + " <intial graph file>");
			return;
		}
		
		System.err.println("Server Started.");

		RemoteObject obj = new RemoteObject();


		// Read the initial graph from args[1] and call obj.setGraph().
		try{
			Scanner scn = new Scanner(new File(args[0]));
			HashMap<Integer, HashSet<Integer>> graph = new HashMap<Integer, HashSet<Integer>>();
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
		} catch (FileNotFoundException e){
			System.err.println(e.toString());
			return;
		}

		try{
			RemoteInterface stub = (RemoteInterface) UnicastRemoteObject.exportObject(obj, 0);

			Registry registry = LocateRegistry.getRegistry();
			registry.bind("Graph Server", obj);
			System.out.println("Server ready");
			
		}
		catch (Exception e){
			System.err.println("Server exception: " + e.toString());
		}

	}
}
