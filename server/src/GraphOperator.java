import java.util.HashMap;
import java.util.HashSet;

public class GraphOperator {
	
	protected HashMap<Integer, HashSet<Integer>> graph; 
	

	public void setGraph(HashMap<Integer, HashSet<Integer>> graph){
		this.graph = graph;
	}

	
	protected HashMap<Integer, HashSet<Integer>> deepCopyGraph() {
		HashMap<Integer, HashSet<Integer>> copy = new HashMap<>();
		for (Integer key : graph.keySet()) {
			// Create a new HashSet for each key to ensure deep copy
			copy.put(key, new HashSet<>(graph.get(key)));
		}
		return copy;
	}

	protected static void addEdge(HashMap<Integer, HashSet<Integer>> graph, int from, int to)
	{
		if(!graph.containsKey(from)) graph.put(from, new HashSet<>());
		if(!graph.containsKey(to)) graph.put(from, new HashSet<>());
		graph.get(from).add(to);
	}
	
	protected static void deleteEdge(HashMap<Integer, HashSet<Integer>> graph, int from, int to)
	{
		if(graph.containsKey(from)) graph.get(from).remove(to);
	}

	protected static void findPath(HashMap<Integer, HashSet<Integer>> graph, int from, int to)
	{
		// implement later
	}
	
}
