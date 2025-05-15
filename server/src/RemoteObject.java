import java.util.HashMap;
import java.util.HashSet;

public class RemoteObject implements RemoteInterface 
{
	
	private HashMap<Integer, HashSet<Integer>> graph; 
	

	public void setGraph(HashMap<Integer, HashSet<Integer>> graph){
		this.graph = graph;
	}

	public void processBatch(String batch)
	{
		System.out.println("Batch Processed");
	}

	private void addEdge(int from, int to)
	{
		// implement later
	}
	
	private void deleteEdge(int from, int to)
	{
		// implement later
		
	}
	
	private void findPath(int form, int to)
	{
		// implement later

	}
}
