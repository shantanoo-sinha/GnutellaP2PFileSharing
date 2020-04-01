package server;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import model.MessageID;
import util.Constants;
import util.DirectoryWatcher;

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

	/** The id. */
	private String id;
	
	/** The leaf node address. */
	private String nodeAddress;
	
	/** The files directory. */
	private File filesDirectory;
	
	/** The ttl. */
	private long TTL;
	
	/** The peer network topology. */
	private String peerNetworkTopology;
	
	/** The files. */
	private Map<File, Object> files = new ConcurrentHashMap<File, Object>();
	
	/*private Map<String, Map<File, Object>> leafNodeFiles = new ConcurrentHashMap<String, Map<File,Object>>();*/
	
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
	/**
	 * Instantiates a new server.
	 */
	private Server() {
		super();
	}

	/**
	 * Instantiates a new server.
	 *
	 * @param id the id
	 * @param peerNetworkTopology the peer network topology
	 */
	public Server(String id, String peerNetworkTopology) {
		this();
		this.id = id;
		this.peerNetworkTopology = peerNetworkTopology;
		
		//initalize the server
		init();
		
		if (!isSuperPeer)
			this.filesDirectory = new File(new File(new File(USER_DIR.getParent()).getParent()) + File.separator
					+ Constants.CLIENTS_FOLDER + File.separator + id + File.separator + Constants.FILES_FOLDER);
		
		//register the available files for a leaf node
		registerFiles();
		
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
	 * Gets the files.
	 *
	 * @return the files
	 */
	public Map<File, Object> getFiles() {
		return files;
	}

	/**
	 * Sets the files.
	 *
	 * @param files            the files to set
	 */
	public void setFiles(Map<File, Object> files) {
		this.files = files;
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
	 * Gets the files directory.
	 *
	 * @return the filesDirectory
	 */
	public File getFilesDirectory() {
		return filesDirectory;
	}

	/**
	 * Sets the files directory.
	 *
	 * @param filesDirectory            the filesDirectory to set
	 */
	public void setFilesDirectory(File filesDirectory) {
		this.filesDirectory = filesDirectory;
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
	 * Inits the.
	 *
	 * @return the leafNodeFiles
	 */
	/*public Map<String, Map<File, Object>> getLeafNodeFiles() {
		return leafNodeFiles;
	}*/

	/**
	 * Inits the Server.
	 */
	private void init() {
/*		if (System.getSecurityManager() == null) {
			System.setSecurityManager(new SecurityManager());
		}*/
		try {
			loadConfig();
			
			System.setProperty(Constants.JAVA_RMI_SERVER_HOSTNAME, Constants.LOCALHOST);
			logger.info("[" + this.id + "] " + Constants.JAVA_RMI_SERVER_HOSTNAME + ":" + System.getProperty(Constants.JAVA_RMI_SERVER_HOSTNAME));
			
			// Initialize the RMI Registry
			IRemote stub = (IRemote) UnicastRemoteObject.exportObject(this, 0);
			Registry registry = LocateRegistry.getRegistry();
			registry.rebind(Constants.RMI_LOCALHOST + prop.getProperty(id + ".port").trim() + Constants.PEER_SERVER, stub);
			
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
			this.nodeAddress = Constants.RMI_LOCALHOST + prop.getProperty(id + ".port").trim() + Constants.PEER_SERVER;
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
				
				Arrays.asList(prop.getProperty(id + ".leaf").split(Constants.SPLIT_REGEX)).forEach(x -> leafNodes
						.add(Constants.RMI_LOCALHOST + prop.getProperty(x + ".port").trim() + Constants.PEER_SERVER));
				
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
					if (Arrays.asList(prop.getProperty(x + ".leaf").split(Constants.SPLIT_REGEX)).contains(this.id))
						superPeer = Constants.RMI_LOCALHOST + prop.getProperty(x + ".port").trim() + Constants.PEER_SERVER;
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
	 * Register files.
	 */
	public void registerFiles() {
		if (isSuperPeer)
			return;
		
		//loading files information
		File[] filesArr = filesDirectory.listFiles();
		logger.info("*******************************************************************");
		logger.info("[" + this.id + "] " + this.id + " Files Directory:" + filesDirectory);
		logger.info("[" + this.id + "] " + this.id + " Available Files:");
		for (int i = 0; i < filesArr.length; i++) {
			logger.info("[" + this.id + "] " + filesArr[i].getName());
			files.put(filesArr[i], filesArr[i]);
		}
		new Thread(new DirectoryWatcher(this)).start();
		logger.info("[" + this.id + "] " + this.id + " Files Count:" + files.size());
		logger.info("*******************************************************************");
	}

	/**
	 * Delete file.
	 *
	 * @param fileName the file name
	 * @throws RemoteException the remote exception
	 */
	public void deleteFile(String fileName) throws RemoteException {
		logger.info("[" + this.id + "] " + "Deleting Server file...");
		removeFileFromRegistry(fileName);
		logger.info("[" + this.id + "] " + "Updated Server files count:" + files.size());
	}
	
	/**
	 * Adds the file.
	 *
	 * @param fileName the file name
	 * @throws RemoteException the remote exception
	 */
	public void addFile(String fileName) throws RemoteException {
		logger.info("[" + this.id + "] " + "Updating Server file...");
		addFileToRegistry(fileName);
		logger.info("[" + this.id + "] " + "Updated Server files count:" + files.size());
	}

	/**
	 * Query.
	 *
	 * @param messageID the message ID
	 * @param fileName the file name
	 * @throws RemoteException the remote exception
	 */
	public void query(MessageID messageID, String fileName) throws RemoteException {

		try {
			long fileCount = files.size();
			// query current leaf node
			if (files.containsKey(new File(filesDirectory + File.separator + fileName))) {
				logger.info("[" + this.id + "] " + "Requested file " + fileName + " is already present on the requesting client");
				return;
			} else {
				//querying super peer
				logger.info("[" + this.id + "] " + "Requested file " + fileName + " is not present on the requesting client. Looking on others.");
				if (!upstreamMap.containsKey(messageID) && TTL >= 0)
					upstreamMap.put(messageID, nodeAddress);
				query(messageID, this.TTL-1, fileName, nodeAddress);
			}
			if(files.size() == fileCount && !files.containsKey(new File(filesDirectory + File.separator + fileName))) {
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
						if(leafNodeAddress.equalsIgnoreCase(upstreamIP))
							continue;
						logger.info("[" + this.id + "] " + "Looking file: " + fileName + " on connected leaf node: " + leafNodeAddress);
						Registry registry;
						try {
							registry = LocateRegistry.getRegistry();
							IRemote serverStub = (IRemote) registry.lookup(leafNodeAddress);
							
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
							
						String neighbourSuperPeerAddress = Constants.RMI_LOCALHOST + prop.getProperty(neighbourSuperPeer + ".port").trim() + Constants.PEER_SERVER;
						Registry registry;
						try {
							registry = LocateRegistry.getRegistry();
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
				} else if (this.files.containsKey(new File(this.filesDirectory + File.separator + fileName))) {
					if (!upstreamMap.containsKey(messageID) && TTL >= 0) {
						upstreamMap.put(messageID, upstreamIP);
						logger.info("[" + this.id + "] " + "Upstream Address: " + upstreamIP);
					}
					logger.info("[" + this.id + "] Requested file: " + fileName + " found on node:" + this.id);
					/*messageID.setFileNotFoundOnAnyNode(false);
					messageID.setLeafNodeIdAddress(nodeAddress);*/
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
						registry = LocateRegistry.getRegistry();
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

				if (this.files.containsKey(new File(this.filesDirectory + File.separator + fileName))) {
					logger.info("[" + this.id + "] " + "Requested file " + fileName + " is already present on the requesting client. Skipping downloading again.");
				} else {
					logger.info("[" + this.id + "] " + "Requested file download from leaf node " + leafNodeIP);
					Registry registry = LocateRegistry.getRegistry();
					IRemote serverStub = (IRemote) registry.lookup(leafNodeIP);
					byte[] fileContent = serverStub.obtain(fileName, this.nodeAddress);
					if(writeFileContent(fileContent, fileName)) {
						logger.info("[" + this.id + "] " + "Requested file " + fileName + " downloaded successfully.");
						/*messageID.setFileNotFoundOnAnyNode(false);
						messageID.setLeafNodeIdAddress(nodeAddress);*/
					} else {
						logger.info("[" + this.id + "] " + "Failed to download the requested file " + fileName + ".");
					}
				}
			} else {
				// back propagating queryHit message to the requestor leaf node
				String upstreamIPAddress = upstreamMap.get(messageID);
				logger.info("[" + this.id + "] " + "Sending back-propogation message for requested file " + fileName + " to " + upstreamIPAddress);
				Registry registry = LocateRegistry.getRegistry();
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
	public byte[] obtain(String fileName, String leafNodeIP) throws IOException {
		byte[] fileContent = null;
		try {
			// reading file content
			logger.info("[" + this.id + "] " + "Starting sending file content from " + this.filesDirectory + File.separator + fileName + " to requesting node " + leafNodeIP);
			fileContent = Files.readAllBytes(Paths.get(this.filesDirectory + File.separator + fileName));
			logger.info("[" + this.id + "] " + "Finished sending file content from " + this.filesDirectory + File.separator + fileName + " to requesting node " + leafNodeIP);
		} catch (Exception e) {
			logger.error("[" + this.id + "] " + "Server exception: Failed to read the requested file content" + e.getMessage());
			e.printStackTrace();
			fileContent = "Failed to read the requested file content. Please try again.".getBytes();
		}
		return fileContent;
	}

	/**
	 * Adds the file to registry.
	 *
	 * @param fileName the file name
	 * @return true, if successful
	 */
	private synchronized boolean addFileToRegistry(String fileName) {
		files.put(new File(filesDirectory + File.separator + fileName), new File(filesDirectory + File.separator + fileName));
		return true;
	}
	
	/**
	 * Removes the file from registry.
	 *
	 * @param fileName the file name
	 * @return true, if successful
	 */
	private synchronized boolean removeFileFromRegistry(String fileName) {
		files.remove(new File(filesDirectory + File.separator + fileName), new File(filesDirectory + File.separator + fileName));
		return true;
	}

	/**
	 * Write file content.
	 *
	 * @param fileContent the file content
	 * @param fileName the file name
	 * @return true, if successful
	 */
	public boolean writeFileContent(byte[] fileContent, String fileName) {
		// writing file to leaf node
		FileOutputStream fileOutputStream = null;
		boolean isFileDownloaded = false;
		boolean isFileAddedToRegistry = false;
		try {
			logger.info("[" + this.id + "] " + "Writing File Content: " + filesDirectory + File.separator + fileName);
			fileOutputStream = new FileOutputStream(new File(filesDirectory + File.separator + fileName));
			fileOutputStream.write(fileContent);
			isFileAddedToRegistry = addFileToRegistry(fileName);
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
				logger.info("[" + this.id + "] " + id + " (Running on port:" + prop.getProperty(id + ".port") + ") Super Peer neighbours:");
				neighbours.forEach(x -> {
					//String str = getPropertyFromValue(x);
					logger.info("[" + this.id + "] " + x + "(Port:" + prop.getProperty(x + ".port") + ")");	
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

	/*@Override
	public boolean queryLeafNode(MessageID messageID, long TTL, String fileName, String requestingLeafNodeAddress, List<String> leafNodesWithFile)
			throws RemoteException {
		if (!upstreamMap.containsKey(messageID) && TTL >= 0) {
			upstreamMap.put(messageID, requestingLeafNodeAddress);
			logger.info("[" + this.id + "] " + "Upstream Address: " + requestingLeafNodeAddress);
		}
		
		logger.info("[" + this.id + "] " + "Received file: " + fileName + " lookup request from: " + requestingLeafNodeAddress);
		if (!this.isSuperPeer && TTL >= 0 && this.files.containsKey(new File(this.filesDirectory + File.separator + fileName))) {
			logger.info("[" + this.id + "] " + "Requested file: " + fileName + " found on " + this.id + ", address: " + this.nodeAddress);
			leafNodesWithFile.add(this.id);
			return true;
		}
		return false;
	}*/
	
	/*@Override
	public List<String> queryLeafNodes(MessageID messageID, long TTL, String fileName, String requestingLeafNode, List<String> leafNodesWithFile) {
		List<String> leafNodesWithFile = new ArrayList<>();
		if(isSuperPeer) {
			if(!checkUpstreamMap(messageID)) {
				if (!upstreamMap.containsKey(messageID) && TTL >= 0) {
					upstreamMap.put(messageID, requestingLeafNode);
					logger.info("[" + this.id + "] " + "Upstream Address: " + requestingLeafNode);
				}
				logger.info("[" + this.id + "] " + "Looking file: " + fileName + " on " + nodeAddress + " leaf nodes except requesting leaf node (" + requestingLeafNode + ")");	
				for(String leafNodeAddress: leafNodes) {
					if(leafNodeAddress.equalsIgnoreCase(requestingLeafNode))
						continue;
					logger.info("[" + this.id + "] " + "Looking file: " + fileName + " on leaf node: " + leafNodeAddress);
					Registry registry;
					try {
						registry = LocateRegistry.getRegistry();
						IRemote serverStub = (IRemote) registry.lookup(leafNodeAddress);
						if(serverStub.queryLeafNode(messageID, TTL - 1, fileName, requestingLeafNode, leafNodesWithFile))
							leafNodesWithFile.add(leafNodeAddress);
					} catch (Exception e) {
						logger.error("[" + this.id + "] " + "Server exception: Unable to query leaf nodes.");
						e.printStackTrace();
					}
				}
			} else {
				logger.info("[" + this.id + "] Leaf Node: " + nodeAddress + " already saw this message. Hence, skipping.");
			}
		}
		return leafNodesWithFile;
	}*/
	
	/*private List<String> queryMySuperPeer(MessageID messageID, long TTL, String fileName, String upstreamIP, List<String> leafNodesWithFile) {
		List<String> leafNodesWithFile = new ArrayList<>();
		String superPeerAddress = Constants.RMI_LOCALHOST + prop.getProperty(superPeer + ".port").trim() + Constants.PEER_SERVER;
		logger.info("[" + this.id + "] " + "Looking file: " + fileName + " on Super Peer: " + superPeerAddress);
		Registry registry;
		try {
			registry = LocateRegistry.getRegistry();
			IRemote serverStub = (IRemote) registry.lookup(superPeerAddress);
			leafNodesWithFile = serverStub.queryLeafNodes(messageID, TTL - 1, fileName, upstreamIP);
			if(leafNodesWithFile.size() > 0) {
				logger.info("[" + this.id + "] " + "Requested file: " + fileName + " found on leaf nodes");
				messageID.setLeafNodeIdAddress(leafNodesWithFile.get(0));
			} else {
				logger.info("[" + this.id + "] " + "Requested file: " + fileName + " not found on leaf nodes");
			}
			logger.info("[" + this.id + "] " + "leafNodesWithFile size: " + leafNodesWithFile.size());
		} catch (Exception e) {
			logger.error("[" + this.id + "] " + "Server exception: Unable to query leaf nodes.");
			e.printStackTrace();
		}
		return leafNodesWithFile;
	}*/
}
