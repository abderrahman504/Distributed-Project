import java.rmi.registry.Registry;
import java.rmi.registry.LocateRegistry;


public class Client 
{
    public static void main(String[] args)
	{
		try{
			Registry registry = LocateRegistry.getRegistry();
			RemoteInterface obj = (RemoteInterface) registry.lookup("Graph Server");
	
			System.out.println("Client ready");

			// Read batch file from args[1] and send to obj
	
			obj.processBatch("Batch");

		}
		catch (Exception e){
			System.err.println("Client exception: " + e.toString());
		}

    }
}
