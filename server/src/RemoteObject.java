import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.List;
import interfaces.RemoteInterface;

public class RemoteObject extends UnicastRemoteObject implements RemoteInterface {

	private final Graph graph;

	public RemoteObject() throws RemoteException {
		super();
		graph = new Graph();
	}

	@Override
	public void addEdge(int from, int to) throws RemoteException {
		graph.addEdge(from, to);
	}

	@Override
	public void deleteEdge(int from, int to) throws RemoteException {
		graph.deleteEdge(from, to);
	}

	@Override
	public int queryShortestPath(int from, int to) throws RemoteException {
		return graph.shortestPath(from, to);
	}

	@Override
	public void processBatch(List<String> operations) throws RemoteException {
		for (String line : operations) {
			if (line.startsWith("Q")) {
				String[] parts = line.split(" ");
				int from = Integer.parseInt(parts[1]);
				int to = Integer.parseInt(parts[2]);
				int result = queryShortestPath(from, to);
				System.out.println(result); // Output for this example
			} else if (line.startsWith("A")) {
				String[] parts = line.split(" ");
				addEdge(Integer.parseInt(parts[1]), Integer.parseInt(parts[2]));
			} else if (line.startsWith("D")) {
				String[] parts = line.split(" ");
				deleteEdge(Integer.parseInt(parts[1]), Integer.parseInt(parts[2]));
			}
		}
	}

	public void loadGraphFromInput(java.util.Scanner scanner) {
		graph.loadInitialGraph(scanner);
	}
}