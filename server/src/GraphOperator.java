import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.Collections;

public class GraphOperator {
	
	protected Map<Integer, Set<Integer>> graph; 
	

	public void setGraph(Map<Integer, Set<Integer>> graph){
		this.graph = graph;
	}

	
	protected Map<Integer, Set<Integer>> deepCopyGraph() {
		Map<Integer, Set<Integer>> copy = new HashMap<>();
		for (Integer key : graph.keySet()) {
			// Create a new HashSet for each key to ensure deep copy
			copy.put(key, new HashSet<>(graph.get(key)));
		}
		return copy;
	}

	protected static void addEdge(Map<Integer, Set<Integer>> graph, int from, int to)
	{
		if(!graph.containsKey(from)) graph.put(from, new HashSet<>());
		if(!graph.containsKey(to)) graph.put(from, new HashSet<>());
		graph.get(from).add(to);
	}
	
	protected static void deleteEdge(Map<Integer, Set<Integer>> graph, int from, int to)
	{
		if(graph.containsKey(from)) graph.get(from).remove(to);
	}


	protected static int findPath(Map<Integer, Set<Integer>> graph, int from, int to) {
		if (from == to) {
			//System.out.println("Shortest path: " + from);
			return 0;
		}

		Queue<Integer> queue = new LinkedList<>();
		Map<Integer, Integer> parent = new HashMap<>();
		Set<Integer> visited = new HashSet<>();

		queue.offer(from);
		visited.add(from);
		parent.put(from, null);

		boolean found = false;

		while (!queue.isEmpty()) {
			int current = queue.poll();

			for (int neighbor : graph.get(current)) {
				if (!visited.contains(neighbor)) {
					visited.add(neighbor);
					parent.put(neighbor, current);
					queue.offer(neighbor);

					if (neighbor == to) {
						found = true;
						break;
					}
				}
			}

			if (found) break;
		}

		if (!found) {
			//System.out.println("No path exists between " + from + " and " + to);
			return -1;
		}

		// Reconstruct the path from 'to' to 'from'
		List<Integer> path = new ArrayList<>();
		Integer current = to;
		while (current != null) {
			path.add(current);
			current = parent.get(current);
		}

		// Reverse the path to get 'from' to 'to'
		Collections.reverse(path);
		//System.out.println("Shortest path: " + path);
		return path.size() - 1;
	}

	
}
