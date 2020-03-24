/**
 * 
 */
package server;

import java.io.IOException;
import java.rmi.Remote;
import java.rmi.RemoteException;

import model.MessageID;

/**
 * The Interface IRemote.
 *
 * @author Shantanoo
 */
public interface IRemote extends Remote {

	/**
	 * Query.
	 *
	 * @param messageID the message ID
	 * @param TTL the ttl
	 * @param fileName the file name
	 * @param upstreamIPAddress the upstream IP address
	 * @throws RemoteException the remote exception
	 */
	public void query(MessageID messageID, long TTL, String fileName, String upstreamIPAddress) throws RemoteException;

	/**
	 * Queryhit.
	 *
	 * @param messageID the message ID
	 * @param TTL the ttl
	 * @param fileName the file name
	 * @param leafNodeIP the leaf node IP
	 * @throws RemoteException the remote exception
	 */
	public void queryHit(MessageID messageID, long TTL, String fileName, String leafNodeIP) throws RemoteException;

	/**
	 * Obtain.
	 *
	 * @param fileName the file name
	 * @return the byte[]
	 * @throws RemoteException the remote exception
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public byte[] obtain(String fileName) throws RemoteException, IOException;
	
	/**
	 * Check upstream map.
	 *
	 * @param messageId the message id
	 * @return true, if successful
	 * @throws RemoteException the remote exception
	 */
	public boolean checkUpstreamMap(MessageID messageId) throws RemoteException;
}
