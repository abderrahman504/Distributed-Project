
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

public class Server extends RemoteObject
{
	
	public static void main(String[] args) {
		System.err.println("Server Started.");

		RemoteObject obj = new RemoteObject();

		// Read the initial graph from args[1] and call obj.setGraph().


		try{
			RemoteInterface stub = (RemoteInterface) UnicastRemoteObject.exportObject(obj, 0);

			Registry registry = LocateRegistry.getRegistry();
			registry.bind("Graph Server", obj);
			System.out.println("Server ready");
			
		}
		catch (Exception e){
			System.err.println("Server exception: " + e.toString());
		}

	}
}
