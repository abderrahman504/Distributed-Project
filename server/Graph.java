package server;

import java.util.*;

public class Graph {
    private Map<Integer, Set<Integer>> adjacencyList;
    private static final Object lock = new Object();

    public Graph() {
        adjacencyList = new HashMap<>();
    }

    public void addEdge(int from, int to) {
        synchronized (lock) {
            adjacencyList.computeIfAbsent(from, k -> new HashSet<>()).add(to);
            // Ensure the destination node exists in the graph
            adjacencyList.computeIfAbsent(to, k -> new HashSet<>());
        }
    }

    public void removeEdge(int from, int to) {
        synchronized (lock) {
            if (adjacencyList.containsKey(from)) {
                adjacencyList.get(from).remove(to);
            }
        }
    }

    public int getShortestPath(int from, int to) {
        synchronized (lock) {
            if (!adjacencyList.containsKey(from) || !adjacencyList.containsKey(to)) {
                return -1;
            }
            if (from == to) {
                return 0;
            }

            Queue<Integer> queue = new LinkedList<>();
            Map<Integer, Integer> distances = new HashMap<>();
            queue.offer(from);
            distances.put(from, 0);

            while (!queue.isEmpty()) {
                int current = queue.poll();
                int currentDistance = distances.get(current);

                if (current == to) {
                    return currentDistance;
                }

                for (int neighbor : adjacencyList.getOrDefault(current, Collections.emptySet())) {
                    if (!distances.containsKey(neighbor)) {
                        distances.put(neighbor, currentDistance + 1);
                        queue.offer(neighbor);
                    }
                }
            }

            return -1;
        }
    }

    public void initializeFromEdges(List<String> edges) {
        synchronized (lock) {
            for (String edge : edges) {
                String[] nodes = edge.trim().split("\\s+");
                if (nodes.length == 2) {
                    int from = Integer.parseInt(nodes[0]);
                    int to = Integer.parseInt(nodes[1]);
                    addEdge(from, to);
                }
            }
        }
    }
} 