import java.util.*;

public class Graph {
    private final Map<Integer, Set<Integer>> adjacencyList = new HashMap<>();

    public synchronized void addEdge(int from, int to) {
        adjacencyList.computeIfAbsent(from, k -> new HashSet<>()).add(to);
    }

    public synchronized void deleteEdge(int from, int to) {
        if (adjacencyList.containsKey(from)) {
            adjacencyList.get(from).remove(to);
        }
    }

    public synchronized int shortestPath(int from, int to) {
        if (from == to) return 0;
        if (!adjacencyList.containsKey(from)) return -1;

        Queue<Integer> queue = new LinkedList<>();
        Set<Integer> visited = new HashSet<>();
        queue.offer(from);
        visited.add(from);
        int distance = 0;

        while (!queue.isEmpty()) {
            int levelSize = queue.size();
            distance++;
            for (int i = 0; i < levelSize; i++) {
                int current = queue.poll();
                for (int neighbor : adjacencyList.getOrDefault(current, Collections.emptySet())) {
                    if (neighbor == to) return distance;
                    if (visited.add(neighbor)) {
                        queue.offer(neighbor);
                    }
                }
            }
        }

        return -1;
    }

    public synchronized void loadInitialGraph(Scanner scanner) {
        while (scanner.hasNextLine()) {
            String line = scanner.nextLine().trim();
            if (line.equals("S")) break;
            String[] parts = line.split(" ");
            if (parts.length == 2) {
                int from = Integer.parseInt(parts[0]);
                int to = Integer.parseInt(parts[1]);
                addEdge(from, to);
            }
        }
        System.out.println("R");  // Signal readiness
    }
}