/*
 * 
 */
package util;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimerTask;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import model.FileConsistencyState;
import model.P2PFile;
import server.IRemote;
import server.Server2;

/**
 * The Class P2PTimerTask.
 */
/**
 * @author Shantanoo
 *
 */
public class P2PTimerTask2 extends TimerTask {

	/** The Constant logger. */
	private static final Logger logger = LogManager.getLogger(P2PTimerTask2.class);
	
	/** The server. */
	private Server2 server;
	
	/**
	 * Instantiates a new p 2 P timer task.
	 *
	 * @param server the server
	 */
	public P2PTimerTask2(Server2 server) {
		super();
		this.server = server;
	}

	/* (non-Javadoc)
	 * @see java.util.TimerTask#run()
	 */
	@Override
	public void run() {
		logger.info("[" + this.server.getId() + "] Starting polling");
		Registry registry;
		try {
			registry = LocateRegistry.getRegistry();
			IRemote serverStub = null;
			if(server.isSuperPeer()) {
				// polling other super peers
				logger.info("[" + this.server.getId() + "] Polling other Super Peers");
				
				logger.info("[" + this.server.getId() + "] getLeafNodeSharedFiles count: " + this.server.getLeafNodeSharedFiles().values().size());
				Map<String, List<P2PFile>> filesPerSuperPeer = new HashMap<>();
				for(Map<String, P2PFile> files : this.server.getLeafNodeSharedFiles().values()) {
					for(P2PFile p2pFile : files.values()) {
						if(!filesPerSuperPeer.containsKey(p2pFile.getOriginServerSuperPeerID()))
							filesPerSuperPeer.put(p2pFile.getOriginServerSuperPeerID(), new ArrayList<P2PFile>());
						filesPerSuperPeer.get(p2pFile.getOriginServerSuperPeerID()).add(p2pFile);
					}
				}
				logger.info("[" + this.server.getId() + "] filesPerSuperPeer count: " + filesPerSuperPeer.size());
				List<P2PFile> allFiles = new ArrayList<>();
				for(Map.Entry<String, List<P2PFile>> entry : filesPerSuperPeer.entrySet()) {
					/*if(entry.getKey().equals(this.server.getIpAddress())) {
						//logger.info("[" + this.server.getId() + "] Skip sending the message to self.");
						continue;
					}*/

					serverStub = (IRemote) registry.lookup(entry.getKey());
					logger.info("[" + this.server.getId() + "] " + "Polling neighbour Super Peer:" + entry.getKey() + " for " + entry.getValue().size() + " files");
					allFiles.addAll(serverStub.poll(entry.getValue()));
				}
				Map<String, List<P2PFile>> filesPerLeafNode = new HashMap<>();
				for(P2PFile file : allFiles) {
					if(file.getState().equals(FileConsistencyState.EXPIRED)) {
						if(!filesPerLeafNode.containsKey(file.getCurrentAddress()))
							filesPerLeafNode.put(file.getCurrentAddress(), new ArrayList<P2PFile>());
						filesPerLeafNode.get(file.getCurrentAddress()).add(file);
					}
				}
				for(Map.Entry<String, List<P2PFile>> entry : filesPerLeafNode.entrySet()) {
					serverStub = (IRemote) registry.lookup(entry.getKey());
					logger.info("[" + this.server.getId() + "] " + "Invalidating " + entry.getValue().size() + " files on leaf node:" + entry.getKey());
					serverStub.invalidate(entry.getValue());
					for(P2PFile p2pFile : entry.getValue()) {
						for(Map.Entry<String, Map<String, P2PFile>> leafNodeSharedFiles : this.server.getLeafNodeSharedFiles().entrySet()) {
							if(leafNodeSharedFiles.getValue().containsKey(p2pFile.getFileName())) {
								leafNodeSharedFiles.getValue().get(p2pFile.getFileName()).setState(FileConsistencyState.EXPIRED);
							}
						}
					}
				}
				
			} else {
				// polling other master leaf nodes
				logger.info("[" + this.server.getId() + "] Polling other master leaf nodes");
				for(P2PFile val: this.server.getClient().getSharedFiles().values()) {
					serverStub = (IRemote) registry.lookup(val.getOriginServerID());
					logger.info("[" + this.server.getId() + "] " + "Polling master leaf node:" + val.getOriginServerID() + " for file:" + val.getFileName());
					long pollResult = serverStub.poll(val);	
					if(pollResult<0) {
						val.setState(FileConsistencyState.EXPIRED);
						logger.info("[" + this.server.getId() + "] " + "File " + val.getFileName() + " validity expired. Please refresh the file.");
						
						serverStub = (IRemote) registry.lookup(this.server.getSuperPeer());
						logger.info("[" + this.server.getId() + "] " + "Invalidate file: " + val.getFileName() + " on Super Peer:" + this.server.getSuperPeer());	
						serverStub.updateSharedFilesToSuperPeer(this.server.getId(), this.server.getClient().getSharedFiles());
					} else {
						logger.info("[" + this.server.getId() + "] " + "File:" + val.getFileName() + " in sync with master leaf node:" + val.getOriginServerID());
					}
				}
			}
		} catch (Exception e) {
			logger.error("[" + this.server.getId() + "] " + "Server exception: Unable to poll.");
			e.printStackTrace();
		}
	}
}
