package util;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.TimerTask;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import model.FileConsistencyState;
import model.P2PFile;
import server.IRemote;
import server.Server;

public class P2PTimerTask extends TimerTask {

	/** The Constant logger. */
	private static final Logger logger = LogManager.getLogger(P2PTimerTask.class);
	
	private Server server;
	
	/**
	 * @param remote
	 * @param server
	 * @param fileName
	 * @param p2pFile
	 */
	public P2PTimerTask(Server server) {
		super();
		this.server = server;
	}

	@Override
	public void run() {
		logger.info("[" + this.server.getId() + "] Starting polling");
		Registry registry;
		try {
			registry = LocateRegistry.getRegistry();
			IRemote serverStub = null;
			if(server.isSuperPeer()) {
				/*List<P2PFile> leafNodeFiles = new ArrayList<>();
				for(String leafNodeAddress: server.getLeafNodes()) {
					logger.info("[" + this.server.getId() + "] " + "Getting leaf node shared file objects.");
					serverStub = (IRemote) registry.lookup(leafNodeAddress);
					leafNodeFiles.addAll(serverStub.getFiles());
				}
				logger.info("[" + this.server.getId() + "] " + "leaf node shared file object size:" + leafNodeFiles.size());
				
				// polling other super peers
				logger.info("[" + this.server.getId() + "] Polling other Super Peers");
				List<String> neighbourSuperPeers = Arrays.asList(this.server.getProp().getProperty(Constants.SUPER_PEER).split(Constants.SPLIT_REGEX));
				
				for(String neighbourSuperPeer: neighbourSuperPeers) {
					
					if(neighbourSuperPeer.equals(this.server.getId())) {
						//logger.info("[" + this.server.getId() + "] Skip sending the message to self.");
						continue;
					}
						
					String neighbourSuperPeerAddress = Constants.RMI_LOCALHOST + this.server.getProp().getProperty(neighbourSuperPeer + Constants.PORT).trim() + Constants.PEER_SERVER;
					serverStub = (IRemote) registry.lookup(neighbourSuperPeerAddress);
					
					logger.info("[" + this.server.getId() + "] " + "Polling neighbour Super Peer:" + neighbourSuperPeerAddress);	
//					long pollResult = serverStub.poll(p2pFile);
						
				}*/
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
					}
				}
			}
		} catch (Exception e) {
			logger.error("[" + this.server.getId() + "] " + "Server exception: Unable to poll.");
			e.printStackTrace();
		}
		
	}
}
