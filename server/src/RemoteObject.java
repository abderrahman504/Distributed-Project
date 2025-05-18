import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.logging.Logger;

public class RemoteObject extends GraphOperator implements RemoteInterface 
{
	public Logger logger;

	@Override
	public String sequentialProcessBatch(int clientId, String batch)
	{
		return process(clientId, batch, false);
	}
	
	@Override
	public String concurrentProcessBatch(int clientId, String batch)
	{
		return process(clientId, batch, true);
	}
	
	private String process(int clientId, String batch, boolean concurrent)
	{
		try
		{
	
			long t_start = System.currentTimeMillis();
			logger.info((concurrent ? "Concurrent" : "Sequential" )+ " - Client " + clientId + "\nBatch :\n" + batch);
			Scanner scn = new Scanner(batch);
			List<GraphRequest> batchList = new ArrayList<>(); // A list of requests in this batch.
			int queryCount = 0;
			// Convert string into list of request objects
			while(scn.hasNextLine()){
				GraphRequest req = new GraphRequest();
				String[] line = scn.nextLine().split(" ");
				if (line.length == 1) break;
				req.type = line[0].charAt(0);
				req.from = Integer.parseInt(line[1]);
				req.to = Integer.parseInt(line[2]);
				batchList.add(req);
				if (req.type == 'Q') queryCount++;
			}
			scn.close();
			
			// Create a job list for each Q type request to dispatch to a thread.
			List<List<GraphRequest>> threadJobs = new ArrayList<>();
			if (concurrent){
				for(int i=0; i<queryCount; i++) threadJobs.add(new ArrayList<>());
				int fullJobs = 0;
				for (int i=0; i<batchList.size(); i++){
					// Add this request to each thread job that requires it
					for (int j=fullJobs; j<threadJobs.size(); j++){
						if (batchList.get(i).type == 'Q'){
							threadJobs.get(fullJobs).add(batchList.get(i).clone());
							fullJobs++;
							break;
						}
						threadJobs.get(j).add(batchList.get(i).clone());
					}
				}
			}
			
	
			String res = startJob(threadJobs, batchList, concurrent);
			long t_end = System.currentTimeMillis();
			long pt = t_end - t_start;
			logger.info("Processing time " + pt + " ms\n" + res);
			return res;
		}
		catch (Exception e){
			logger.severe("Exception: " + e.getMessage() +"\n");
			e.printStackTrace();
			throw e;
		}
	}

	
	private synchronized String startJob(List<List<GraphRequest>> threadJobs, List<GraphRequest> batchList, boolean concurrent)
	{
		// Spawn threads
		int results[] = new int[threadJobs.size()];
		List<Thread> threads = new ArrayList<>(threadJobs.size()); 
		for (int i=0; i<threadJobs.size(); i++){
			List<GraphRequest> job = threadJobs.get(i);
			// Create thread and pass graph copy and job.
			Map<Integer, Set<Integer>> copy = super.deepCopyGraph();
			Thread th = new Thread(new ThreadJob(i, copy, job, results));
			th.start();
			threads.add(th);
		}
		
		
		StringBuilder result = new StringBuilder();
		// If concurrent then main thread runs A,D operations only
		if (concurrent){
			// Update original graph with A, D requests only.
			for(GraphRequest req : batchList){
				if (req.type == 'Q') continue;
				
				if (req.type == 'A') addEdge(graph, req.from, req.to);
				else if (req.type == 'D') deleteEdge(graph, req.from, req.to);
			}
		}
		else{ //Otherwise it runs all operations
			for(GraphRequest req : batchList){
				switch(req.type){
					case 'Q':
					int res = findPath(graph, req.from, req.to);
					result.append(res);
					result.append('\n');
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
		
		// Wait for all threads to finish.
		try{
			for(int i=0; i<threads.size(); i++){
				threads.get(i).join();
				result.append(results[i]);
				result.append('\n');
			}
			
		} catch (InterruptedException e){
			System.err.println("Error while waiting for job thread:");
			System.err.println(e.toString());
		}
		
		if (result.length() != 0) result.deleteCharAt(result.length()-1);
		return result.toString();
	}

	

	class ThreadJob implements Runnable
	{
		int id;
		Map<Integer, Set<Integer>> graph;
		List<GraphRequest> job;
		int[] results_arr;

		public ThreadJob(int id, Map<Integer, Set<Integer>> graph, List<GraphRequest> job, int[] results_arr){
			this.id = id;
			this.graph = graph;
			this.job = job;
			this.results_arr = results_arr;
		}

		@Override
		public void run(){
			for (GraphRequest req : job){
				switch (req.type){
					case 'Q':
					results_arr[id] = findPath(graph, req.from, req.to);
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

