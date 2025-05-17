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


	protected static void findPath(HashMap<Integer, HashSet<Integer>> graph, int from, int to) {
		if (from == to) {
			System.out.println("Shortest path: " + from);
			return;
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

			for (int neighbor : graph[current]) {
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
			System.out.println("No path exists between " + from + " and " + to);
			return;
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
		System.out.println("Shortest path: " + path);
	}

	
}
