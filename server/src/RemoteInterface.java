
import java.rmi.Remote;
import java.rmi.RemoteException;

public interface RemoteInterface extends Remote
{

	String concurrentProcessBatch(int clientId, String batch) throws RemoteException;

	String sequentialProcessBatch(int clientId, String batch) throws RemoteException;
} 