
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

public class Server extends RemoteObject
{
	
	public static void main(String[] args) {

		
		try{
			//create an instance of the remote object to be invoked
			RemoteObject obj = new RemoteObject();

//			no need for the stub, the remote object instance suffices
//			RemoteInterface stub = (RemoteInterface) UnicastRemoteObject.exportObject(obj, 0);

			Registry registry = LocateRegistry.createRegistry(1099);

			Scanner sc = new Scanner(System.in);
			obj.loadInitialGraph(sc);

			registry.bind("Graph Server", obj);


			System.out.println("Server ready");
			
		}
		catch (Exception e){
			System.err.println("Server exception: " + e.toString());
		}

	}
}
