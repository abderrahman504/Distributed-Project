
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

public class Server extends RemoteObject
{
	
	public static void main(String[] args) {
		RemoteObject obj = new RemoteObject();
		
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
