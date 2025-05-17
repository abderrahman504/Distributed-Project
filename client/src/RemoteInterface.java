
import java.rmi.Remote;
import java.rmi.RemoteException;

public interface RemoteInterface extends Remote
{

	String processBatch(int clientId, String batch) throws RemoteException;
} 