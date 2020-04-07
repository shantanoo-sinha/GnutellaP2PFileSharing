/**
 * 
 */
package server;

import java.io.IOException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;
import java.util.Map;

import model.MessageID;
import model.P2PFile;

/**
 * The Interface IRemote.
 *
 * @author Shantanoo
 */
public interface IRemote extends Remote {

/*//	public Client client = null;
	public Client getClient() throws RemoteException;
	public Map<String, Client> getLeafNodesMap() throws RemoteException;
	public void addToLeafNodesMap(String key, Client value) throws RemoteException;*/
	public List<P2PFile> getFiles() throws RemoteException;
	public List<P2PFile> queryLeafNodes() throws RemoteException;
	
	public void registerMasterFilesToSuperPeer(String leafNodeAddress, Map</*File*/String, P2PFile> files) throws RemoteException;
	public void registerSharedFilesToSuperPeer(String leafNodeAddress, Map</*File*/String, P2PFile> files) throws RemoteException;
	public void updateSharedFilesToSuperPeer(String leafNodeAddress, Map</*File*/String, P2PFile> files) throws RemoteException;
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
	
	/*public boolean queryLeafNode(MessageID messageID, long TTL, String fileName, String upstreamIPAddress) throws RemoteException;*/

	/*public List<String> queryLeafNodes(MessageID messageID, long TTL, String fileName, String requestingLeafNode) throws RemoteException;*/
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
	 * @param leafNodeIP the leaf node IP
	 * @return the byte[]
	 * @throws RemoteException the remote exception
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
//	public byte[] obtain(String fileName, String leafNodeIP) throws RemoteException, IOException;
	public P2PFile obtain(String fileName, String leafNodeIP) throws RemoteException, IOException;
	
	/**
	 * Check upstream map.
	 *
	 * @param messageId the message id
	 * @return true, if successful
	 * @throws RemoteException the remote exception
	 */
	public boolean checkUpstreamMap(MessageID messageID) throws RemoteException;
	
	public void invalidate(MessageID messageID, P2PFile p2pFile, String upstreamIP) throws RemoteException;
	public void invalidate(List<P2PFile> p2pFiles) throws RemoteException;
	public long poll(P2PFile p2pFile) throws RemoteException;
	public List<P2PFile> poll(List<P2PFile> p2pFiles) throws RemoteException;
}
