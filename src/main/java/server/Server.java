package server;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.RMIClientSocketFactory;
import java.rmi.server.RMIServerSocketFactory;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Timer;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import client.Client;
import model.FileConsistencyState;
import model.MessageID;
import model.P2PFile;
import rmi.RMISecurityClientSocketFactory;
import rmi.RMISecurityServerSocketFactory;
import security.RSAEncryption;
import security.RSAKeyPair;
import security.RSAPrivateKey;
import security.RSAPublicKey;
import util.Constants;
import util.FileDownloader;
import util.P2PTimerTask;

/**
 * The Class Server.
 *
 * @author Shantanoo
 */
public class Server implements IRemote, Serializable {
	
	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 4106010658890702237L;
	
	/** The Constant logger. */
	private static final Logger logger = LogManager.getLogger(Server.class);

	byte pattern = (byte) 0xAC;
	
	/** The id. */
	private String id;
	
	/** The client. */
	private Client client;
	
	/** The leaf node address. */
	private String nodeAddress;
	
	/** The ttl. */
	private long TTL;
	
	/** The ttr. */
	private long TTR;
	
	/** The is pull. */
	private boolean isPull = false;
	
	/** The peer network topology. */
	private String peerNetworkTopology;
	
	/** The leaf node master files. */
	private Map<String, Map</*File*/String, P2PFile>> leafNodeMasterFiles = new ConcurrentHashMap<String, Map</*File*/String, P2PFile>>();
	
	/** The leaf node shared files. */
	private Map<String, Map</*File*/String, P2PFile>> leafNodeSharedFiles = new ConcurrentHashMap<String, Map</*File*/String, P2PFile>>();
	
	/** The neighbours. */
	private List<String> neighbours;
	
	/** The user dir. */
	private File USER_DIR = new File(System.getProperty(Constants.USER_DIR));
	
	/** The topology file path. */
	private File TOPOLOGY_FILE_PATH = new File(new File(new File(USER_DIR.getParent()).getParent()) + File.separator + Constants.TOPOLOGY_FOLDER);
	
	/** The upstream map. */
	private Map<MessageID, String> upstreamMap;
	
	/** The Constant upstreamMapMaxSize. */
	private final static int upstreamMapMaxSize = 50;
	
	/** The prop. */
	private Properties prop = new Properties();

	/** The is super peer. */
	private boolean isSuperPeer = false;
	
	/** The super peer. */
	private String superPeer;
	
	/** The leaf nodes. */
	private List<String> leafNodes = new ArrayList<>();
	
	/** The leaf nodes map. */
	public Map<String, Client> leafNodesMap = new HashMap<>();
	
	private File keysDirectory;
	private File sharedKeysDirectory;

	private RSAKeyPair rsaKeyPair;
	
	private RSAPublicKey rsaPublicKey = null;
    private RSAPrivateKey rsaPrivateKey = null;
    
    public RSAPublicKey getRsaPublicKey() {
		return rsaPublicKey;
	}

	public void setRsaPublicKey(RSAPublicKey rsaPublicKey) {
		this.rsaPublicKey = rsaPublicKey;
	}

	public RSAPrivateKey getRsaPrivateKey() {
		return rsaPrivateKey;
	}

	public void setRsaPrivateKey(RSAPrivateKey rsaPrivateKey) {
		this.rsaPrivateKey = rsaPrivateKey;
	}
	
	/**
	 * Instantiates a new server.
	 */
	private Server() {
		super();
	}

	/**
	 * Instantiates a new server.
	 *
	 * @param client the client
	 * @param id the id
	 * @param peerNetworkTopology the peer network topology
	 * @param TTR the ttr
	 */
	public Server(Client client, String id, String peerNetworkTopology, long TTR, RSAKeyPair rsaKeyPair) {
		this();
		this.id = id;
		this.client = client;
		this.peerNetworkTopology = peerNetworkTopology;
		this.TTR = TTR;
//		this.rsaKeyPair = rsaKeyPair;
//		this.rsaPrivateKey = rsaKeyPair.getPrivate();
//		this.rsaPublicKey = rsaKeyPair.getPublic();
		
		if(TTR>0)
			setPull(true);
		
		//initialize the server
		init();

		//register the neigbours of a super peer node
		getNeighbors();
		
		//configuring to make the associative array maintain a defined size
		//This will remove the oldest entry from the hash map
		upstreamMap = new LinkedHashMap<MessageID, String>(upstreamMapMaxSize, 0.75F, false) {
			/**
			 * Overriding LinkedHashMap removeEldestEntry implementation to remove the
			 * oldest entry. This is flush out old entries at appropriate times (we don't
			 * want this buffer to grow indefinitely)
			 */
			private static final long serialVersionUID = -8793504079665963454L;

			protected boolean removeEldestEntry(Map.Entry<MessageID, String> eldest) {
				return size() >= upstreamMapMaxSize;
			}
		};
		
		if(isPull()) {
			logger.info("[" + this.id + "] Scheduling polling task every " + TTR + " ms");
			Timer timer = new Timer();
	        timer.schedule(new P2PTimerTask(this), 0, TTR);
		}
	}

	/**
	 * Gets the id.
	 *
	 * @return the id
	 */
	public String getId() {
		return id;
	}

	/**
	 * Sets the id.
	 *
	 * @param id            the id to set
	 */
	public void setId(String id) {
		this.id = id;
	}

	/**
	 * Gets the ip address.
	 *
	 * @return the ipAddress
	 */
	public String getIpAddress() {
		return nodeAddress;
	}

	/**
	 * Sets the ip address.
	 *
	 * @param ipAddress            the ipAddress to set
	 */
	public void setIpAddress(String ipAddress) {
		this.nodeAddress = ipAddress;
	}

	/**
	 * Gets the neighbours.
	 *
	 * @return the neighbours
	 */
	public List<String> getNeighbours() {
		return neighbours;
	}

	/**
	 * Sets the neighbours.
	 *
	 * @param neighbours            the neighbours to set
	 */
	public void setNeighbours(List<String> neighbours) {
		this.neighbours = neighbours;
	}

	/**
	 * Gets the upstream map.
	 *
	 * @return the upstreamMap
	 */
	public Map<MessageID, String> getUpstreamMap() {
		return upstreamMap;
	}

	/* (non-Javadoc)
	 * @see server.IRemote#checkUpstreamMap(model.MessageID)
	 */
	public boolean checkUpstreamMap(MessageID messageId) {
		return upstreamMap.containsKey(messageId);
	}
	
	/**
	 * Gets the ttl.
	 *
	 * @return the tTL
	 */
	public long getTTL() {
		return TTL;
	}

	/**
	 * Checks if is super peer.
	 *
	 * @return the isSuperPeer
	 */
	public boolean isSuperPeer() {
		return isSuperPeer;
	}

	/**
	 * Gets the super peer.
	 *
	 * @return the super peer
	 */
	public String getSuperPeer() {
		return superPeer;
	}

	/**
	 * Gets the ttr.
	 *
	 * @return the ttr
	 */
	public long getTTR() {
		return TTR;
	}

	/**
	 * Sets the ttr.
	 *
	 * @param tTR the new ttr
	 */
	public void setTTR(long tTR) {
		TTR = tTR;
	}

	/**
	 * Inits the.
	 *
	 * @return the leafNodeFiles
	 */
	public Map<String, Map</*File*/String, P2PFile>> getLeafNodeMasterFiles() {
		return leafNodeMasterFiles;
	}
	
	/**
	 * Gets the leaf node shared files.
	 *
	 * @return the leaf node shared files
	 */
	public Map<String, Map</*File*/String, P2PFile>> getLeafNodeSharedFiles() {
		return leafNodeSharedFiles;
	}
	
	/**
	 * Checks if is pull.
	 *
	 * @return true, if is pull
	 */
	public boolean isPull() {
		return isPull;
	}

	/**
	 * Sets the pull.
	 *
	 * @param isPull the new pull
	 */
	public void setPull(boolean isPull) {
		this.isPull = isPull;
	}

	/**
	 * Gets the leaf nodes.
	 *
	 * @return the leaf nodes
	 */
	public List<String> getLeafNodes() {
		return leafNodes;
	}

	public File getKeysDirectory() {
		return keysDirectory;
	}

	public void setKeysDirectory(File keysDirectory) {
		this.keysDirectory = keysDirectory;
	}

	public File getSharedKeysDirectory() {
		return sharedKeysDirectory;
	}

	public void setSharedKeysDirectory(File sharedKeysDirectory) {
		this.sharedKeysDirectory = sharedKeysDirectory;
	}
	
	/**
	 * Inits the Server.
	 */
	private void init() {
		if (System.getSecurityManager() == null) {
			System.setSecurityManager(new SecurityManager());
		}
		try {
			loadConfig();
			
			System.setProperty(Constants.JAVA_RMI_SERVER_HOSTNAME, Constants.LOCALHOST);
			logger.info("[" + this.id + "] " + Constants.JAVA_RMI_SERVER_HOSTNAME + ":" + System.getProperty(Constants.JAVA_RMI_SERVER_HOSTNAME));
			
			// Initialize the RMI Registry
//			IRemote stub = (IRemote) UnicastRemoteObject.exportObject(this, 0);
			RMIClientSocketFactory csf = new RMISecurityClientSocketFactory(rsaPublicKey, rsaPrivateKey);
		    RMIServerSocketFactory ssf = new RMISecurityServerSocketFactory(rsaPublicKey, rsaPrivateKey);
			
			/*RMIClientSocketFactory csf = new XorClientSocketFactory(pattern);
		    RMIServerSocketFactory ssf = new XorServerSocketFactory(pattern);*/
			
			
		    //LocateRegistry.createRegistry(1099, csf, ssf);
		    IRemote stub = (IRemote) UnicastRemoteObject.exportObject(this, 0, csf, ssf);
		    Registry registry = LocateRegistry.getRegistry(1099);
			registry.rebind(Constants.RMI_LOCALHOST + prop.getProperty(id + Constants.PORT).trim() + Constants.PEER_SERVER, stub);
			
			/*logger.info("[" + this.id + "] Available peers:");
			for (String boundName : registry.list()) {
	            logger.info("[" + this.id + "] " + boundName);
	        }*/
			
			logger.info("[" + this.id + "] " + "Server bound");
			logger.info("*******************************************************************");
			
			initLeafNodes();
		} catch (Exception e) {
			logger.error("[" + this.id + "] " + "Server exception: Unable to init Server.");
			e.printStackTrace();
		}
	}

	/**
	 * Load config.
	 */
	private void loadConfig() {

		InputStream is = null;
		try {
			//loading the configuration from the topology file
			is = new FileInputStream(new File(TOPOLOGY_FILE_PATH + File.separator + peerNetworkTopology + Constants.TXT));
			prop.load(is);
			this.nodeAddress = Constants.RMI_LOCALHOST + prop.getProperty(id + Constants.PORT).trim() + Constants.PEER_SERVER;
//			this.TTL = prop.entrySet().stream().filter(key -> key.toString().startsWith(Constants.CLIENT_PREFIX)).count();
//			this.TTL = prop.getProperty(Constants.SUPER_PEER).split(Constants.SPLIT_REGEX).length+1;
			this.TTL = prop.getProperty(Constants.SUPER_PEER).split(Constants.SPLIT_REGEX).length * 2;
			/*
			 * prop.forEach((key, value) -> { if(key.toString().startsWith("Client"))
			 * this.TTL++; });
			 */
			logger.info("[" + this.id + "] " + "TTL:" + TTL);
			/*
			 * prop.forEach((key, value) -> logger.info("[" + this.id + "] " + key + ":" + value));
			 * prop.forEach((key, value) -> logger.info("[" + this.id + "] " + key + ":" +
			 * prop.getProperty((String) key)));
			 */
			this.keysDirectory = new File(new File(new File(USER_DIR.getParent()).getParent()) + File.separator
					+ Constants.CLIENTS_FOLDER + File.separator + id + File.separator + Constants.KEYS_FOLDER);
			this.sharedKeysDirectory = new File(this.keysDirectory + File.separator + Constants.SHARED_KEYS_FOLDER);
		} catch (Exception e) {
			logger.error("[" + this.id + "] " + "Server exception: Unable to load config.");
			e.printStackTrace();
		}
	}
	
	/**
	 * Inits the leaf nodes.
	 */
	private void initLeafNodes() {
		try {
			// loading super peer information
			if (Arrays.asList(prop.getProperty(Constants.SUPER_PEER).split(Constants.SPLIT_REGEX)).contains(id)) {
				isSuperPeer = true;
				logger.info("*******************************************************************");
				logger.info("[" + this.id + "] This is a Super Peer.");
				
				/*Arrays.asList(prop.getProperty(id + Constants.LEAF).split(Constants.SPLIT_REGEX)).forEach(x -> leafNodes
						.add(Constants.RMI_LOCALHOST + prop.getProperty(x + Constants.PORT).trim() + Constants.PEER_SERVER));*/
				Arrays.asList(prop.getProperty(id + Constants.LEAF).split(Constants.SPLIT_REGEX)).forEach(x -> leafNodes.add(x));
				
				logger.info("[" + this.id + "] Connected leaf nodes:");
				leafNodes.forEach(leafNode -> {
					logger.info("[" + this.id + "] " + leafNode);
				});
				logger.info("*******************************************************************");
			} else {
				// loading leaf node information
				logger.info("*******************************************************************");
				logger.info("[" + this.id + "] This is a leaf node.");
				Arrays.asList(prop.getProperty(Constants.SUPER_PEER).split(Constants.SPLIT_REGEX)).forEach(x -> {
					if (Arrays.asList(prop.getProperty(x + Constants.LEAF).split(Constants.SPLIT_REGEX)).contains(this.id))
						superPeer = Constants.RMI_LOCALHOST + prop.getProperty(x + Constants.PORT).trim() + Constants.PEER_SERVER;
				});
				logger.info("[" + this.id + "] Connected Super Peer:" + superPeer);
				logger.info("*******************************************************************");
			}
		} catch (Exception e) {
			logger.error("[" + this.id + "] " + "Server exception: Unable to load Super Peer - Leaf node information.");
			e.printStackTrace();
		}
	}

	/**
	 * Register masterFiles.
	 *
	 * @param messageID the message ID
	 * @param fileName the file name
	 * @throws RemoteException the remote exception
	 *//*
	public void registerFiles() {
		if (isSuperPeer)
			return;
		
		//loading masterFiles information
		File[] filesArr = filesDirectory.listFiles();
		logger.info("*******************************************************************");
		logger.info("[" + this.id + "] " + this.id + " Files Directory:" + filesDirectory);
		logger.info("[" + this.id + "] " + this.id + " Available Files:");
		for (int i = 0; i < filesArr.length; i++) {
			logger.info("[" + this.id + "] " + filesArr[i].getName());
			masterFiles.put(filesArr[i], filesArr[i]);
		}
		new Thread(new DirectoryWatcher(this)).start();
		logger.info("[" + this.id + "] " + this.id + " Files Count:" + masterFiles.size());
		logger.info("*******************************************************************");
	}*/

	/**
	 * Delete file.
	 *
	 * @param fileName the file name
	 * @throws RemoteException the remote exception
	 *//*
	public void deleteFile(String fileName) throws RemoteException {
		logger.info("[" + this.id + "] " + "Deleting Server file...");
		removeFileFromRegistry(fileName);
		logger.info("[" + this.id + "] " + "Updated Server masterFiles count:" + masterFiles.size());
	}
	
	*//**
	 * Adds the file.
	 *
	 * @param fileName the file name
	 * @throws RemoteException the remote exception
	 *//*
	public void addFile(String fileName) throws RemoteException {
		logger.info("[" + this.id + "] " + "Updating Server file...");
		addFileToRegistry(fileName);
		logger.info("[" + this.id + "] " + "Updated Server masterFiles count:" + masterFiles.size());
	}*/

	/**
	 * Query.
	 *
	 * @param messageID the message ID
	 * @param fileName the file name
	 * @throws RemoteException the remote exception
	 */
	public void query(MessageID messageID, String fileName) throws RemoteException {

		try {
			long fileCount = client.getMasterFiles().size() + client.getSharedFiles().size();
			// query current leaf node
			if (client.getMasterFiles().containsKey(fileName)) {
				logger.info("[" + this.id + "] " + "Requested master file " + fileName + " is already present on the requesting client");
				return;
			} else if (client.getSharedFiles().containsKey(fileName) && client.getSharedFiles().get(fileName).getState().equals(FileConsistencyState.VALID)) {
				logger.info("[" + this.id + "] " + "Requested shared file " + fileName + " with VALID state is already present on the requesting client");
				return;
			} else if (client.getSharedFiles().containsKey(fileName) && client.getSharedFiles().get(fileName).getState().equals(FileConsistencyState.INVALID)) {
				logger.info("[" + this.id + "] " + "Requested shared file " + fileName + " with INVALID state is already present on the requesting client. Use refresh");
				return;
			} else if (client.getSharedFiles().containsKey(fileName) && client.getSharedFiles().get(fileName).getState().equals(FileConsistencyState.EXPIRED)) {
				logger.info("[" + this.id + "] " + "Requested shared file " + fileName + " with EXPIRED state is already present on the requesting client. Use refresh");
				return;
			} else {
				//querying super peer
				logger.info("[" + this.id + "] " + "Requested file " + fileName + " is not present on the requesting client. Looking on others.");
				if (!upstreamMap.containsKey(messageID) && TTL >= 0)
					upstreamMap.put(messageID, nodeAddress);
				query(messageID, this.TTL-1, fileName, nodeAddress);
			}
			if((client.getMasterFiles().size() + client.getSharedFiles().size()) == fileCount 
					&& !client.getMasterFiles().containsKey(fileName)
					&& !client.getSharedFiles().containsKey(fileName)) {
				logger.info("[" + this.id + "] Requested file is not present on any of the Clients. Please try with a different file.");
			}
		} catch (Exception e) {
			logger.error("[" + this.id + "] " + "Server exception: Unable to query Server.");
			e.printStackTrace();
		}
	}

	/* (non-Javadoc)
	 * @see server.IRemote#query(model.MessageID, long, java.lang.String, java.lang.String)
	 */
	public void query(MessageID messageID, long TTL, String fileName, String upstreamIP) throws RemoteException {

		try {
			logger.info("[" + this.id + "] " + "file query: " + fileName + ", TTL:" + TTL + ", messageID:" + messageID );
			logger.info("[" + this.id + "] " + "query received from : " + upstreamIP);
			
			if (TTL >= 0) {
				
				if(isSuperPeer) {
					//querying super peer
					if (!upstreamMap.containsKey(messageID) && TTL >= 0) {
						upstreamMap.put(messageID, upstreamIP);
						logger.info("[" + this.id + "] " + "Upstream Address: " + upstreamIP);
					}

					// querying leaf nodes
					logger.info("[" + this.id + "] " + "Looking file: " + fileName + " on connected " + nodeAddress + " leaf nodes");	
					for(String leafNodeAddress: leafNodes) {
						if((Constants.RMI_LOCALHOST + prop.getProperty(leafNodeAddress + Constants.PORT).trim() + Constants.PEER_SERVER).equalsIgnoreCase(upstreamIP))
							continue;
						logger.info("[" + this.id + "] " + "Looking file: " + fileName + " on connected leaf node: " + leafNodeAddress);
						Registry registry;
						try {
							registry = LocateRegistry.getRegistry(1099);
//							registry = LocateRegistry.getRegistry(Constants.LOCALHOST, 1099, new XorClientSocketFactory(pattern));
							IRemote serverStub = (IRemote) registry.lookup(Constants.RMI_LOCALHOST + prop.getProperty(leafNodeAddress + Constants.PORT).trim() + Constants.PEER_SERVER);
							
							if(!serverStub.checkUpstreamMap(messageID)) {
								logger.info("[" + this.id + "] " + "Looking file: " + fileName + " on connected leaf node:" + leafNodeAddress);	
								serverStub.query(messageID, TTL-1, fileName, upstreamIP);
							} else {
								logger.info("[" + this.id + "] " + leafNodeAddress + " already saw this message. Hence, skipping.");
							}
							
						} catch (Exception e) {
							logger.error("[" + this.id + "] " + "Server exception: Unable to query leaf nodes.");
							e.printStackTrace();
						}
					}
					
					// querying other super peers
					logger.info("[" + this.id + "] Requesting file on other Super Peers");
					List<String> neighbourSuperPeers = Arrays.asList(prop.getProperty(Constants.SUPER_PEER).split(Constants.SPLIT_REGEX));
					for(String neighbourSuperPeer: neighbourSuperPeers) {
						if(neighbourSuperPeer.equals(this.id)) {
							//logger.info("[" + this.id + "] Skip sending the message to self.");
							continue;
						}
							
						String neighbourSuperPeerAddress = Constants.RMI_LOCALHOST + prop.getProperty(neighbourSuperPeer + Constants.PORT).trim() + Constants.PEER_SERVER;
						Registry registry;
						try {
							registry = LocateRegistry.getRegistry(1099);
//							registry = LocateRegistry.getRegistry(Constants.LOCALHOST, 1099, new XorClientSocketFactory(pattern));
							IRemote serverStub = (IRemote) registry.lookup(neighbourSuperPeerAddress);
							if(!serverStub.checkUpstreamMap(messageID)) {
								logger.info("[" + this.id + "] " + "Looking file: " + fileName + " on neighbour Super Peer:" + neighbourSuperPeerAddress);	
								serverStub.query(messageID, TTL-1, fileName, nodeAddress);
							} else {
								logger.info("[" + this.id + "] " + neighbourSuperPeerAddress + " already saw this message. Hence, skipping.");
							}
						} catch (Exception e) {
							logger.error("[" + this.id + "] " + "Server exception: Unable to query neighbours.");
							e.printStackTrace();
						}
					}
				} else if ((client.getMasterFiles().containsKey(fileName)
								&& client.getMasterFiles().get(fileName).getState().equals(FileConsistencyState.VALID))
						|| (client.getSharedFiles().containsKey(fileName)
								&& client.getSharedFiles().get(fileName).getState().equals(FileConsistencyState.VALID))) {
					if (!upstreamMap.containsKey(messageID) && TTL >= 0) {
						upstreamMap.put(messageID, upstreamIP);
						logger.info("[" + this.id + "] " + "Upstream Address: " + upstreamIP);
					}
					logger.info("[" + this.id + "] Requested file: " + fileName + " found on node:" + this.id);
					// sending queryHit message to leaf node
					queryHit(messageID, TTL - 1, fileName, nodeAddress);
				} else {
					if (!upstreamMap.containsKey(messageID) && TTL >= 0) {
						upstreamMap.put(messageID, upstreamIP);
						logger.info("[" + this.id + "] " + "Upstream Address: " + upstreamIP);
					}
					// querying super peer
					Registry registry;
					try {
						registry = LocateRegistry.getRegistry(1099);
//						registry = LocateRegistry.getRegistry(Constants.LOCALHOST, 1099, new XorClientSocketFactory(pattern));
						IRemote serverStub = (IRemote) registry.lookup(superPeer);
						logger.info("[" + this.id + "] " + "Looking file: " + fileName + " on Super Peer:" + superPeer);	
						
						if(!serverStub.checkUpstreamMap(messageID)) {
							logger.info("[" + this.id + "] " + "Looking file: " + fileName + " on connected Super Peer:" + superPeer);	
							serverStub.query(messageID, TTL-1, fileName, nodeAddress);
						} else {
							logger.info("[" + this.id + "] " + superPeer + " already saw this message. Hence, skipping.");
						}
						
					} catch (Exception e) {
						logger.error("[" + this.id + "] " + "Server exception: Unable to query connected Super Peer.");
						e.printStackTrace();
					}
				}
			}
		} catch (Exception e) {
			logger.error("[" + this.id + "] " + "Server exception: Unable to query Server.");
			e.printStackTrace();
		}
	}

	/* (non-Javadoc)
	 * @see server.IRemote#queryhit(model.MessageID, long, java.lang.String, java.lang.String)
	 */
	public void queryHit(MessageID messageID, long TTL, String fileName, String leafNodeIP) throws RemoteException {
		try {
			if (this.nodeAddress.equals(messageID.getLeafNodeId())) {
				//this is the requestor leaf node. It will download the file
				logger.info("[" + this.id + "] " + this.id + ": receiving query hit for file:" + fileName + " from " + leafNodeIP);
				logger.info("[" + this.id + "] " + "Checking just before file download");

				if (/*this.masterFiles*/client.getSharedFiles().containsKey(fileName)) {
					logger.info("[" + this.id + "] " + "Requested file " + fileName + " is already present on the requesting client. Skipping downloading again.");
				} else {
					logger.info("[" + this.id + "] " + "Requested file download from leaf node " + leafNodeIP);
					Registry registry = LocateRegistry.getRegistry(1099);
//					Registry registry = LocateRegistry.getRegistry(Constants.LOCALHOST, 1099, new XorClientSocketFactory(pattern));
					IRemote serverStub = (IRemote) registry.lookup(leafNodeIP);
					new FileDownloader(serverStub, this, fileName).start();
				}
			} else {
				// back propagating queryHit message to the requestor leaf node
				String upstreamIPAddress = upstreamMap.get(messageID);
				logger.info("[" + this.id + "] " + "Sending back-propogation message for requested file " + fileName + " to " + upstreamIPAddress);
				Registry registry = LocateRegistry.getRegistry(1099);
//				Registry registry = LocateRegistry.getRegistry(Constants.LOCALHOST, 1099, new XorClientSocketFactory(pattern));
				IRemote serverStub = (IRemote) registry.lookup(upstreamIPAddress);
				serverStub.queryHit(messageID, TTL, fileName, leafNodeIP);
			}
		} catch (Exception e) {
			logger.error("[" + this.id + "] " + "Server exception: Unable to send queryHit Server.");
			e.printStackTrace();
		}
	}

	/* (non-Javadoc)
	 * @see server.IRemote#obtain(java.lang.String)
	 */
	public P2PFile obtain(String fileName, String leafNodeIP) throws IOException {
		byte[] fileContent = null;
		String filePath = null;
		P2PFile p2pFile = null;
		try {
			// reading file content
			if(client.getMasterFiles().containsKey(fileName)) {
				filePath = client.getMasterFilesDirectory() + File.separator + fileName;
				p2pFile = client.getMasterFiles().get(fileName);
			} else {
				filePath = client.getSharedFilesDirectory() + File.separator + fileName;
				p2pFile = client.getSharedFiles().get(fileName);
			}
			
			logger.info("[" + this.id + "] " + "Starting sending file content from " + filePath + " to requesting node " + leafNodeIP);
			fileContent = Files.readAllBytes(Paths.get(filePath));
			p2pFile.setFileContent(fileContent);
			logger.info("[" + this.id + "] " + "Finished sending file content from " + filePath  + " to requesting node " + leafNodeIP);
			
		} catch (Exception e) {
			logger.error("[" + this.id + "] " + "Server exception: Failed to read the requested file content" + e.getMessage());
			e.printStackTrace();
			fileContent = "Failed to read the requested file content. Please try again.".getBytes();
		}
		return p2pFile;
	}

	/**
	 * Adds the file to registry.
	 *
	 * @param p2pFile the p 2 p file
	 * @param fileName the file name
	 * @return true, if successful
	 *//*
	private synchronized boolean addFileToRegistry(String fileName) {
		masterFiles.put(new File(filesDirectory + File.separator + fileName), new File(filesDirectory + File.separator + fileName));
		return true;
	}
	
	*//**
	 * Removes the file from registry.
	 *
	 * @param fileName the file name
	 * @return true, if successful
	 *//*
	private synchronized boolean removeFileFromRegistry(String fileName) {
		masterFiles.remove(new File(filesDirectory + File.separator + fileName), new File(filesDirectory + File.separator + fileName));
		return true;
	}*/

	/**
	 * Write file content.
	 *
	 * @param fileContent the file content
	 * @param fileName the file name
	 * @return true, if successful
	 */
	public boolean writeFileContent(P2PFile p2pFile, String fileName) {
		// writing file to leaf node
		FileOutputStream fileOutputStream = null;
		boolean isFileDownloaded = false;
		boolean isFileAddedToRegistry = false;
		try {
			logger.info("[" + this.id + "] " + "Writing File Content: " + /*filesDirectory*/client.getSharedFilesDirectory() + File.separator + fileName);
			fileOutputStream = new FileOutputStream(getFileObj(client.getSharedFilesDirectory(), fileName));
			fileOutputStream.write(p2pFile.getFileContent());
			logger.info(p2pFile.getVersion() + ", " + p2pFile.getTTR() + ", " + p2pFile.getOriginServerID() + ", " + getFileObj(client.getSharedFilesDirectory(), p2pFile.getFileName()) + ", " + p2pFile.getFileName() + ", " + p2pFile.getState());
			P2PFile localP2PFile = new P2PFile(p2pFile.getVersion(), p2pFile.getTTR(), p2pFile.getOriginServerID(), p2pFile.getOriginServerSuperPeerID(), nodeAddress, null, getFileObj(client.getSharedFilesDirectory(), p2pFile.getFileName()), p2pFile.getFileName(), p2pFile.getState());
			/*isFileAddedToRegistry = addFileToRegistry(fileName);*/
			isFileAddedToRegistry = client.addSharedFileToRegistry(fileName, localP2PFile);
			registerSharedFilesToSuperPeer();
			if(isFileAddedToRegistry)
				isFileDownloaded = true;
		} catch (Exception e) {
			logger.error("[" + this.id + "] " + "Server exception: Unable to write file content.");
			isFileDownloaded = false;
		} finally {
			try {
				if (fileOutputStream != null)
					fileOutputStream.close();
			} catch (IOException e) {
				logger.error("[" + this.id + "] " + "Server exception: Unable to close FIleOutputStream.");
				e.printStackTrace();
			}
		}
		return isFileDownloaded;
	}

	/**
	 * Gets the neighbors.
	 *
	 * @return the neighbors
	 */
	public void getNeighbors() {
		logger.info("*******************************************************************");
		if(!isSuperPeer) {
			/*logger.info("[" + this.id + "] Super Peer:" + superPeer);
			logger.info("*******************************************************************");*/
			return;
		}
		try {
			// loading the neighbours
			String clientNeighbour = prop.getProperty(id + ".next");
			if (clientNeighbour != null) {
				neighbours = (Arrays.asList(clientNeighbour.split(Constants.SPLIT_REGEX)));
				logger.info("[" + this.id + "] " + id + " (Running on port:" + prop.getProperty(id + Constants.PORT) + ") Super Peer neighbours:");
				neighbours.forEach(x -> {
					//String str = getPropertyFromValue(x);
					logger.info("[" + this.id + "] " + x + "(Port:" + prop.getProperty(x + Constants.PORT) + ")");	
				});
			}
		} catch (Exception e) {
			logger.error("[" + this.id + "] " + "Exception: Unable to get neigbours for Super Peer: " + e.toString());
			e.printStackTrace();
		}
		logger.info("*******************************************************************");
	}

	/**
	 * Gets the prop.
	 *
	 * @return the prop
	 */
	public Properties getProp() {
		return prop;
	}
	
	/**
	 * Gets the property.
	 *
	 * @param key the key
	 * @return the property
	 */
	public String getProperty(String key) {
		return prop.getProperty(key);
	}
	
	/*private String getPropertyFromValue(String value) {
		return (String) prop.entrySet().stream()
				.filter(entry -> entry.getKey().toString().startsWith(Constants.CLIENT_PREFIX) && entry.getValue().toString().equals(value))
				.findAny()
				.map(Map.Entry::getKey).get();
	}*/
	
	/**
	 * Gets the client.
	 *
	 * @return the client
	 */
	public Client getClient() {
		return this.client;
	}

	/**
	 * Gets the file obj.
	 *
	 * @param dir the dir
	 * @param fileName the file name
	 * @return the file obj
	 */
	private File getFileObj(File dir, String fileName) {
		return new File(dir + File.separator + fileName);
	}

	/* (non-Javadoc)
	 * @see server.IRemote#invalidate(model.MessageID, model.P2PFile, java.lang.String)
	 */
	@Override
	public void invalidate(MessageID messageID, P2PFile p2pFile, String upstreamIP) throws RemoteException {

		try {
			logger.info("[" + this.id + "] " + "file invalidate: " + p2pFile.getFileName() + ", TTL:" + TTL + ", messageID:" + messageID );
			logger.info("[" + this.id + "] " + "file invalidate received from : " + p2pFile.getOriginServerID());
			
				if(isSuperPeer) {
					//sending invalidate message to super peer
					if (!upstreamMap.containsKey(messageID)) {
						upstreamMap.put(messageID, upstreamIP);
						logger.info("[" + this.id + "] " + "Upstream Address: " + upstreamIP);
					}

					// sending file invalidate message to leaf nodes
					logger.info("[" + this.id + "] " + "Sending file invalidate message for file: " + p2pFile.getFileName() + " on connected " + nodeAddress + " leaf nodes");	
					for(String leafNodeAddress: leafNodes) {
						if((Constants.RMI_LOCALHOST + prop.getProperty(leafNodeAddress + Constants.PORT).trim() + Constants.PEER_SERVER).equalsIgnoreCase(upstreamIP))
							continue;
						logger.info("[" + this.id + "] " + "Sending file invalidate message for file: " + p2pFile.getFileName() + " on connected leaf node: " + leafNodeAddress);
						Registry registry;
						try {
							registry = LocateRegistry.getRegistry(1099);
//							registry = LocateRegistry.getRegistry(Constants.LOCALHOST, 1099, new XorClientSocketFactory(pattern));
							IRemote serverStub = (IRemote) registry.lookup(Constants.RMI_LOCALHOST + prop.getProperty(leafNodeAddress + Constants.PORT).trim() + Constants.PEER_SERVER);
							
							if(!serverStub.checkUpstreamMap(messageID)) {
								logger.info("[" + this.id + "] " + "Sending file invalidate message for file: " + p2pFile.getFileName()  + " on connected leaf node:" + leafNodeAddress);	
								serverStub.invalidate(messageID, p2pFile, upstreamIP);
							} else {
								logger.info("[" + this.id + "] " + leafNodeAddress + " already saw the message file invalidate message for file: " + p2pFile.getFileName()  + " . Hence, skipping.");
							}
							
						} catch (Exception e) {
							logger.error("[" + this.id + "] " + "Server exception: Unable to send file invalidate message to leaf nodes.");
							e.printStackTrace();
						}
					}
					
					// sending file invalidate message to other super peers
					logger.info("[" + this.id + "] Sending file invalidate message for file: " + p2pFile.getFileName() + " on other Super Peers");
					List<String> neighbourSuperPeers = Arrays.asList(prop.getProperty(Constants.SUPER_PEER).split(Constants.SPLIT_REGEX));
					
					for(String neighbourSuperPeer: neighbourSuperPeers) {
						
						if(neighbourSuperPeer.equals(this.id)) {
							//logger.info("[" + this.id + "] Skip sending the message to self.");
							continue;
						}
							
						String neighbourSuperPeerAddress = Constants.RMI_LOCALHOST + prop.getProperty(neighbourSuperPeer + Constants.PORT).trim() + Constants.PEER_SERVER;
						Registry registry;
						try {
							registry = LocateRegistry.getRegistry(1099);
//							registry = LocateRegistry.getRegistry(Constants.LOCALHOST, 1099, new XorClientSocketFactory(pattern));
							IRemote serverStub = (IRemote) registry.lookup(neighbourSuperPeerAddress);
							if(!serverStub.checkUpstreamMap(messageID)) {
								logger.info("[" + this.id + "] " + "Sending file invalidate message for file: " + p2pFile.getFileName() + " on neighbour Super Peer:" + neighbourSuperPeerAddress);	
								serverStub.invalidate(messageID, p2pFile, nodeAddress);
							} else {
								logger.info("[" + this.id + "] " + neighbourSuperPeerAddress + " already saw the file invalidate message. Hence, skipping.");
							}
						} catch (Exception e) {
							logger.error("[" + this.id + "] " + "Server exception: Unable to query neighbours.");
							e.printStackTrace();
						}
					}
				} else if (client.getSharedFiles().containsKey(p2pFile.getFileName())
						&& client.getSharedFiles().get(p2pFile.getFileName()).getState().equals(FileConsistencyState.VALID)
						&& client.getSharedFiles().get(p2pFile.getFileName()).getVersion() != p2pFile.getVersion()) {
					if (!upstreamMap.containsKey(messageID)) {
						upstreamMap.put(messageID, upstreamIP);
						logger.info("[" + this.id + "] " + "Upstream Address: " + upstreamIP);
					}
					logger.info("[" + this.id + "] Invalidating file: " + p2pFile.getFileName() + " on node:" + this.id);
					// Invalidating file on leaf node
					/*File fileObj = getFileObj(client.getSharedFilesDirectory(), p2pFile.getFileName());
					if(fileObj.isFile() && fileObj.exists())
						fileObj.delete();
					client.getSharedFiles().remove(p2pFile.getFileName());*/
					client.getSharedFiles().get(p2pFile.getFileName()).setState(FileConsistencyState.INVALID);
					logger.info("[" + this.id + "] Shared file size: " + client.getSharedFiles().size());
					
					// Invalidating file on Super Peer					
					Registry registry;
					try {
						registry = LocateRegistry.getRegistry(1099);
//						registry = LocateRegistry.getRegistry(Constants.LOCALHOST, 1099, new XorClientSocketFactory(pattern));
						IRemote serverStub = (IRemote) registry.lookup(superPeer);
						logger.info("[" + this.id + "] " + "Invalidate file: " + p2pFile.getFileName() + " on Super Peer:" + superPeer);	
						serverStub.updateSharedFilesToSuperPeer(id, client.getSharedFiles());
					} catch (Exception e) {
						logger.error("[" + this.id + "] " + "Server exception: Unable to send file invalidate message to connected Super Peer.");
						e.printStackTrace();
					}
					
				} else {
					if (!upstreamMap.containsKey(messageID)) {
						upstreamMap.put(messageID, upstreamIP);
						logger.info("[" + this.id + "] " + "Upstream Address: " + upstreamIP);
					}
					// querying super peer
					Registry registry;
					try {
						registry = LocateRegistry.getRegistry(1099);
//						registry = LocateRegistry.getRegistry(Constants.LOCALHOST, 1099, new XorClientSocketFactory(pattern));
						IRemote serverStub = (IRemote) registry.lookup(superPeer);
						logger.info("[" + this.id + "] " + "Sending file invalidate message for file: " + p2pFile.getFileName() + " on Super Peer:" + superPeer);	
						
						if(!serverStub.checkUpstreamMap(messageID)) {
							logger.info("[" + this.id + "] " + "Sending file invalidate message for file: " + p2pFile.getFileName() + " on connected Super Peer:" + superPeer);	
							serverStub.invalidate(messageID, p2pFile, nodeAddress);
						} else {
							logger.info("[" + this.id + "] " + superPeer + " already saw the file invalidate message. Hence, skipping.");
						}
						
					} catch (Exception e) {
						logger.error("[" + this.id + "] " + "Server exception: Unable to send file invalidate message to connected Super Peer.");
						e.printStackTrace();
					}
				}
		} catch (Exception e) {
			logger.error("[" + this.id + "] " + "Server exception: Unable to send file invalidate message to Server.");
			e.printStackTrace();
		}
	}

	/* (non-Javadoc)
	 * @see server.IRemote#poll(model.P2PFile)
	 */
	@Override
	public long poll(P2PFile p2pFile) throws RemoteException {
		long pollOutput = -1l;
		logger.info("[" + this.id + "] " + "Comparing version with master copy");
		if(client.getMasterFiles().containsKey(p2pFile.getFileName())
				&& client.getMasterFiles().get(p2pFile.getFileName()).getVersion() == p2pFile.getVersion()) {
			pollOutput = getTTR();
		}
		return pollOutput;
	}
	
	/* (non-Javadoc)
	 * @see server.IRemote#poll(java.util.List)
	 */
	@Override
	public List<P2PFile> poll(List<P2PFile> p2pFiles) throws RemoteException {
		if(isSuperPeer) {
			logger.info("[" + this.id + "] " + "Comparing version with super peer leaf node master file");
			Map<String, P2PFile> combinedFiles = new HashMap<>();
			getLeafNodeMasterFiles().values().forEach(x -> combinedFiles.putAll(x));
			System.out.println("Combined size : " + combinedFiles.size());
			for(P2PFile p2pFile : p2pFiles) {
				if(combinedFiles.containsKey(p2pFile.getFileName())
						&& combinedFiles.get(p2pFile.getFileName()).getVersion() != p2pFile.getVersion()) {
					p2pFile.setState(FileConsistencyState.EXPIRED);
					logger.info("[" + this.id + "] " + "Marking " + p2pFile.getFileName() + " on "+ p2pFile.getFile() + " as expired");
				}
			}
		}
		return p2pFiles;
	}

	/* (non-Javadoc)
	 * @see server.IRemote#getFiles()
	 */
	@Override
	public List<P2PFile> getFiles() throws RemoteException {
		return this.client.getSharedFiles().values().stream().collect(Collectors.toList());
	}

	/* (non-Javadoc)
	 * @see server.IRemote#queryLeafNodes()
	 */
	@Override
	public List<P2PFile> queryLeafNodes() throws RemoteException {
		// querying leaf nodes
		logger.info("[" + this.id + "] " + "Looking files on connected leaf nodes");
		List<P2PFile> files = new ArrayList<>();
		for(String leafNodeAddress: leafNodes) {
			Registry registry;
			try {
				registry = LocateRegistry.getRegistry(1099);
//				registry = LocateRegistry.getRegistry(Constants.LOCALHOST, 1099, new XorClientSocketFactory(pattern));
				IRemote serverStub = (IRemote) registry.lookup(Constants.RMI_LOCALHOST + prop.getProperty(leafNodeAddress + Constants.PORT).trim() + Constants.PEER_SERVER);
				files.addAll(serverStub.getFiles());
			} catch (Exception e) {
				logger.error("[" + this.id + "] " + "Server exception: Unable to query leaf nodes.");
				e.printStackTrace();
			}
		}
		return files;
	}

	/* (non-Javadoc)
	 * @see server.IRemote#registerMasterFilesToSuperPeer(java.lang.String, java.util.Map)
	 */
	@Override
	public void registerMasterFilesToSuperPeer(String leafNodeAddress, Map</*File*/String, P2PFile> files) throws RemoteException {
		leafNodeMasterFiles.put(leafNodeAddress, files);
		for(Map.Entry<String, Map</*File*/String, P2PFile>> entry : leafNodeMasterFiles.entrySet()) {
			logger.info("[" + this.id + "] " + "leaf node: " + entry.getKey() + ", master file size" + entry.getValue().size());
		}
	}
	
	/* (non-Javadoc)
	 * @see server.IRemote#registerSharedFilesToSuperPeer(java.lang.String, java.util.Map)
	 */
	@Override
	public void registerSharedFilesToSuperPeer(String leafNodeAddress, Map</*File*/String, P2PFile> files) throws RemoteException {
		if(leafNodeSharedFiles.containsKey(leafNodeAddress))
			leafNodeSharedFiles.get(leafNodeAddress).putAll(files);
		else
			leafNodeSharedFiles.put(leafNodeAddress, files);
		for(Map.Entry<String, Map</*File*/String, P2PFile>> entry : leafNodeSharedFiles.entrySet()) {
			logger.info("[" + this.id + "] " + "leaf node: " + entry.getKey() + ", shared file size" + entry.getValue().size());
		}
	}
	
	/* (non-Javadoc)
	 * @see server.IRemote#updateSharedFilesToSuperPeer(java.lang.String, java.util.Map)
	 */
	@Override
	public void updateSharedFilesToSuperPeer(String leafNodeAddress, Map</*File*/String, P2PFile> files) throws RemoteException {
		if(leafNodeSharedFiles.containsKey(leafNodeAddress)) {
			leafNodeSharedFiles.remove(leafNodeAddress);
			leafNodeSharedFiles.put(leafNodeAddress, files);
		} else
			leafNodeSharedFiles.put(leafNodeAddress, files);
		for(Map.Entry<String, Map</*File*/String, P2PFile>> entry : leafNodeSharedFiles.entrySet()) {
			logger.info("[" + this.id + "] " + "leaf node: " + entry.getKey() + ", shared file size" + entry.getValue().size());
		}
	}
	
	/**
	 * Register master files to super peer.
	 */
	public void registerMasterFilesToSuperPeer() {
		Registry registry;
		try {
			registry = LocateRegistry.getRegistry(1099);
//			registry = LocateRegistry.getRegistry(Constants.LOCALHOST, 1099, new XorClientSocketFactory(pattern));
			IRemote serverStub = (IRemote) registry.lookup(superPeer);
			serverStub.registerMasterFilesToSuperPeer(id, client.getMasterFiles());
		} catch (Exception e) {
			logger.error("[" + this.id + "] " + "Server exception: Unable to register master files to SuperPeer.");
			e.printStackTrace();
		}
	}
	
	/**
	 * Register shared files to super peer.
	 */
	public void registerSharedFilesToSuperPeer() {
		Registry registry;
		try {
			registry = LocateRegistry.getRegistry(1099);
//			registry = LocateRegistry.getRegistry(Constants.LOCALHOST, 1099, new XorClientSocketFactory(pattern));
			IRemote serverStub = (IRemote) registry.lookup(superPeer);
			serverStub.registerSharedFilesToSuperPeer(id, client.getSharedFiles());
		} catch (Exception e) {
			logger.error("[" + this.id + "] " + "Server exception: Unable to register shared files to SuperPeer.");
			e.printStackTrace();
		}
	}

	/* (non-Javadoc)
	 * @see server.IRemote#invalidate(java.util.List)
	 */
	@Override
	public void invalidate(List<P2PFile> p2pFiles) throws RemoteException {
		for(P2PFile p2pFile : p2pFiles) {
			/*File fileObj = getFileObj(client.getSharedFilesDirectory(), p2pFile.getFileName());
			if(fileObj.isFile() && fileObj.exists())
				fileObj.delete();
			client.getSharedFiles().remove(p2pFile.getFileName());*/
			client.getSharedFiles().get(p2pFile.getFileName()).setState(FileConsistencyState.EXPIRED);
			logger.info("[" + this.id + "] Shared file size: " + client.getSharedFiles().size());
		}
		//updateSharedFilesToSuperPeer(id, client.getSharedFiles());
	}
	
	/**
	 * Refresh.
	 *
	 * @param p2pFile the p 2 p file
	 */
	public void refresh(P2PFile p2pFile) {
		Registry registry;
		try {
			registry = LocateRegistry.getRegistry(1099);
//			registry = LocateRegistry.getRegistry(Constants.LOCALHOST, 1099, new XorClientSocketFactory(pattern));
			IRemote serverStub = (IRemote) registry.lookup(p2pFile.getOriginServerID());
			new FileDownloader(serverStub, this, p2pFile.getFileName()).start();
		} catch (Exception e) {
			logger.error("[" + this.id + "] " + "Server exception: Unable to refresh shared filefrom master leaf node.");
			e.printStackTrace();
		}
	}
	
	private RSAEncryption readKeys(String id) {
		RSAEncryption rsa = null;
		String privateKeyFile = getKeysDirectory() + File.separator + this.getId() + ".key";
		String publicKeyFile = getKeysDirectory() + File.separator + this.getId() + ".pub";
		try {
			FileReader privateFileReader = new FileReader(Paths.get(privateKeyFile).toFile());
            BufferedReader bufferedReader = new BufferedReader(privateFileReader);

            String line = null, N = null, D = null;
            if((line = bufferedReader.readLine()) != null) {
            	N = line;
            	logger.debug("N=> " + line);
            }
            if((line = bufferedReader.readLine()) != null) {
            	D = line;
            	logger.debug("D=> " + line);
            }
            bufferedReader.close();
	        
            FileReader publicFileReader = new FileReader(Paths.get(publicKeyFile).toFile());
            bufferedReader = new BufferedReader(publicFileReader);
            line = null;
            String E = null;
            if((line = bufferedReader.readLine()) != null) {
            	logger.debug("N=> " + line);
            }
            if((line = bufferedReader.readLine()) != null) {
            	E = line;
            	logger.debug("E=> " + line);
            }
            bufferedReader.close();
	        
            String nString = new String(Base64.getDecoder().decode(N)).toString();
			String dString = new String(Base64.getDecoder().decode(D)).toString();
			String eString = new String(Base64.getDecoder().decode(E)).toString();
			
			BigInteger n, d, e;
			n = new BigInteger(nString);
			d = new BigInteger(dString);
			e = new BigInteger(eString);
			rsa = new RSAEncryption(n, e, d);
			
		} catch (IOException e1) {
			logger.error("[" + this.id + "] " + "Client exception: Unable to read RSA keys.");
			e1.printStackTrace();
		}
		return rsa;
	}
	
	public void sharePublicKey() {
		byte[] publicKey = readPublicKey(this.id);
		prop.entrySet().stream().filter(entry -> entry.toString().contains(Constants.PORT)).forEach(entry -> {
//			logger.info("Client " + entry.getValue());
			String nodeAddress = Constants.RMI_LOCALHOST + entry.getValue() + Constants.PEER_SERVER;
//			logger.info("Node Address " + nodeAddress);
			if(this.nodeAddress.equalsIgnoreCase(nodeAddress))
				return;
			
			Registry registry;
			try {
//				byte[] publicKey = readPublicKey(this.id);
				registry = LocateRegistry.getRegistry(1099);
//				registry = LocateRegistry.getRegistry(Constants.LOCALHOST, 1099, new XorClientSocketFactory(pattern));
				IRemote serverStub = (IRemote) registry.lookup(nodeAddress);
				if(serverStub.sharePublicKey(publicKey, this.id))
					logger.info("Public Key shared with " + nodeAddress);
				else
					logger.error("Failed to share public key with " + nodeAddress);
			} catch (Exception e) {
				logger.error("[" + this.id + "] " + "Server exception: Unable to register shared files to SuperPeer.");
				e.printStackTrace();
			}			
		});
	}
	
	@Override
	public boolean sharePublicKey(byte[] publicKey, String clientName) {
		FileOutputStream fileOutputStream = null;
		boolean isFileDownloaded = false;
		try {
			File publicKeyFilePath = this.getSharedKeysDirectory(); 
			logger.info("[" + this.id + "] " + "Writing " + clientName + " Public Key: " + getFileObj(publicKeyFilePath, clientName + ".pub"));
			if(!publicKeyFilePath.exists())
				publicKeyFilePath.mkdirs();
			fileOutputStream = new FileOutputStream(getFileObj(publicKeyFilePath, clientName + ".pub"));
			fileOutputStream.write(publicKey);
			isFileDownloaded = true;
		} catch (Exception e) {
			logger.error("[" + this.id + "] " + "Server exception: Unable to write public key for another client.");
			isFileDownloaded = false;
		} finally {
			try {
				if (fileOutputStream != null)
					fileOutputStream.close();
			} catch (IOException e) {
				logger.error("[" + this.id + "] " + "Server exception: Unable to close FIleOutputStream.");
				e.printStackTrace();
			}
		}
		return isFileDownloaded;
	}
	
	private byte[] readPublicKey(String id) {
		
		String publicKeyFile = getKeysDirectory() + File.separator + id + ".pub";
		byte[] publicKey = null;
		try {
			publicKey = Files.readAllBytes(Paths.get(publicKeyFile));
		} catch (IOException e1) {
			logger.error("[" + this.id + "] " + "Server exception: Unable to share RSA public key.");
			e1.printStackTrace();
			publicKey = "Unable to share RSA public key. Please try again".getBytes();
		}
		return publicKey;
	}
}
