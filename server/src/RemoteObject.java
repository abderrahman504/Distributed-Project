import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Scanner;

public class RemoteObject implements RemoteInterface 
{
	
	private HashMap<Integer, HashSet<Integer>> graph; 
	

	public void setGraph(HashMap<Integer, HashSet<Integer>> graph){
		this.graph = graph;
	}

	public void processBatch(String batch)
	{
		Scanner scn = new Scanner(batch);
		List<GraphRequest> batchList = new ArrayList<>(); // A list of requests in this batch.
		int queryCount = 0;
		// Convert string into list of request objects
		while(scn.hasNextLine()){
			GraphRequest req = new GraphRequest();
			String[] line = scn.nextLine().split(" ");
			req.type = line[0].charAt(0);
			req.from = Integer.parseInt(line[1]);
			req.to = Integer.parseInt(line[2]);
			batchList.add(req);
			if (req.type == 'Q') queryCount++;
		}
		scn.close();

		// Create a job list for each Q type request to dispatch to a thread.
		List<List<GraphRequest>> threadJobs = new ArrayList<>();
		for(int i=0; i<queryCount; i++) threadJobs.add(new ArrayList<>());

		int fullJobs = 0;
		for (int i=0; i<batchList.size(); i++){
			// Add this request to each thread job that requires it
			for (int j=fullJobs; j<threadJobs.size(); j++){
				threadJobs.get(j).add(batchList.get(i).clone());
				if (batchList.get(i).type == 'Q') fullJobs++;
			}
		}

		// Spawn threads
		List<Thread> threads = new ArrayList<>(threadJobs.size()); 
		for (List<GraphRequest> job : threadJobs){
			// Create thread and pass graph copy and job.
			HashMap<Integer, HashSet<Integer>> copy = deepCopyGraph();
			Thread th = new Thread(new ThreadJob(copy, job));
			th.start();
			threads.add(th);
		}

		// Update original graph with A, D requests only.
		for(GraphRequest req : batchList){
			if (req.type == 'Q') continue;
			
			if (req.type == 'A') addEdge(graph, req.from, req.to);
			else if (req.type == 'D') deleteEdge(graph, req.from, req.to);
		}

		// Wait for all threads to finish.
		try{
			for(Thread th : threads){
				th.join();
			}

		} catch (InterruptedException e){
			System.err.println("Error while waiting for job thread:");
			System.err.println(e.toString());
		}
		
		// Whatever else we need to do now. Logs??
		
	}
	
	private HashMap<Integer, HashSet<Integer>> deepCopyGraph() {
		HashMap<Integer, HashSet<Integer>> copy = new HashMap<>();
		for (Integer key : graph.keySet()) {
			// Create a new HashSet for each key to ensure deep copy
			copy.put(key, new HashSet<>(graph.get(key)));
		}
		return copy;
	}

	private static void addEdge(HashMap<Integer, HashSet<Integer>> graph, int from, int to)
	{
		if(!graph.containsKey(from)) graph.put(from, new HashSet<>());
		if(!graph.containsKey(to)) graph.put(from, new HashSet<>());
		graph.get(from).add(to);
	}
	
	private static void deleteEdge(HashMap<Integer, HashSet<Integer>> graph, int from, int to)
	{
		if(graph.containsKey(from)) graph.get(from).remove(to);
	}

	private static void findPath(HashMap<Integer, HashSet<Integer>> graph, int from, int to)
	{
		// implement later
	}
	
	

	class ThreadJob implements Runnable
	{
		HashMap<Integer, HashSet<Integer>> graph;
		List<GraphRequest> job;

		public ThreadJob(HashMap<Integer, HashSet<Integer>> graph, List<GraphRequest> job){
			this.graph = graph;
			this.job = job;
		}

		@Override
		public void run(){
			for (GraphRequest req : job){
				switch (req.type){
					case 'Q':
					findPath(graph, req.from, req.to);
					break;

					case 'A':
					addEdge(graph, req.from, req.to);
					break;
					
					case 'D':
					deleteEdge(graph, req.from, req.to);
					break;
				}
			}
		}
	}

	
	class GraphRequest{
		char type; // [Q]uery, [A]dd, or [D]elete.
		int from, to;

		public GraphRequest(){}

		public GraphRequest(char type, int from, int to){
			this.type = type;
			this.from = from;
			this.to = to;
		}
		public GraphRequest clone(){ return new GraphRequest(type, from, to);}
	}


}

