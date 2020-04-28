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

	/**
	 * Gets the files.
	 *
	 * @return the files
	 * @throws RemoteException the remote exception
	 */
	public List<P2PFile> getFiles() throws RemoteException;
	
	/**
	 * Query leaf nodes.
	 *
	 * @return the list
	 * @throws RemoteException the remote exception
	 */
	public List<P2PFile> queryLeafNodes() throws RemoteException;
	
	/**
	 * Register master files to super peer.
	 *
	 * @param leafNodeAddress the leaf node address
	 * @param files the files
	 * @throws RemoteException the remote exception
	 */
//	public void registerMasterFilesToSuperPeer(String leafNodeAddress, Map<String, P2PFile> files) throws RemoteException;
	public void registerMasterFilesToSuperPeer(byte[] bytes) throws RemoteException;
	
	/**
	 * Register shared files to super peer.
	 *
	 * @param leafNodeAddress the leaf node address
	 * @param files the files
	 * @throws RemoteException the remote exception
	 */
//	public void registerSharedFilesToSuperPeer(String leafNodeAddress, Map<String, P2PFile> files) throws RemoteException;
	public void registerSharedFilesToSuperPeer(byte[] bytes) throws RemoteException;
	
	/**
	 * Update shared files to super peer.
	 *
	 * @param leafNodeAddress the leaf node address
	 * @param files the files
	 * @throws RemoteException the remote exception
	 */
	public void updateSharedFilesToSuperPeer(String leafNodeAddress, Map<String, P2PFile> files) throws RemoteException;
	public void updateSharedFilesToSuperPeer(byte[] bytes) throws RemoteException;
	/**
	 * Query.
	 *
	 * @param messageID the message ID
	 * @param TTL the ttl
	 * @param fileName the file name
	 * @param upstreamIPAddress the upstream IP address
	 * @throws RemoteException the remote exception
	 */
//	public void query(MessageID messageID, long TTL, String fileName, String upstreamIPAddress) throws RemoteException;
	public void query(byte[] bytes) throws RemoteException;
	
	/**
	 * Queryhit.
	 *
	 * @param messageID the message ID
	 * @param TTL the ttl
	 * @param fileName the file name
	 * @param leafNodeIP the leaf node IP
	 * @throws RemoteException the remote exception
	 */
//	public void queryHit(MessageID messageID, long TTL, String fileName, String leafNodeIP) throws RemoteException;
	public void queryHit(byte[] bytes) throws RemoteException;

	/**
	 * Obtain.
	 *
	 * @param fileName the file name
	 * @param leafNodeIP the leaf node IP
	 * @return the byte[]
	 * @throws RemoteException the remote exception
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
//	public P2PFile obtain(String fileName, String leafNodeIP) throws RemoteException, IOException;
	public byte[] obtain(byte[] bytes) throws RemoteException, IOException;
	
	/**
	 * Check upstream map.
	 *
	 * @param messageID the message ID
	 * @return true, if successful
	 * @throws RemoteException the remote exception
	 */
	public boolean checkUpstreamMap(MessageID messageID) throws RemoteException;
	
	/**
	 * Invalidate.
	 *
	 * @param messageID the message ID
	 * @param p2pFile the p 2 p file
	 * @param upstreamIP the upstream IP
	 * @throws RemoteException the remote exception
	 */
//	public void invalidate(MessageID messageID, P2PFile p2pFile, String upstreamIP) throws RemoteException;
	public void invalidate(byte[] bytes) throws RemoteException;
	
	/**
	 * Invalidate.
	 *
	 * @param p2pFiles the p 2 p files
	 * @throws RemoteException the remote exception
	 */
	public void invalidate(List<P2PFile> p2pFiles) throws RemoteException;
	
	/**
	 * Poll.
	 *
	 * @param p2pFile the p 2 p file
	 * @return the long
	 * @throws RemoteException the remote exception
	 */
	public long poll(P2PFile p2pFile) throws RemoteException;
	
	/**
	 * Poll.
	 *
	 * @param p2pFiles the p 2 p files
	 * @return the list
	 * @throws RemoteException the remote exception
	 */
	public List<P2PFile> poll(List<P2PFile> p2pFiles) throws RemoteException;
	
	public boolean sharePublicKey(byte[] publicKey, String clientName) throws RemoteException;

	public byte[] sharePublicKeyAndGetPublicKey(byte[] publicKey, String id) throws RemoteException;
}
