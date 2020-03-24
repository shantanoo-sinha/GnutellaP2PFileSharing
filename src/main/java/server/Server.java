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
	private String leafNodeAddress;
	
	/** The files directory. */
	private File filesDirectory;
	
	/** The ttl. */
	private long TTL;
	
	/** The peer network topology. */
	private String peerNetworkTopology;
	
	/** The files. */
	private Map<File, Object> files = new ConcurrentHashMap<File, Object>();
	
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
		this.filesDirectory = new File(new File(new File(USER_DIR.getParent()).getParent()) + File.separator
				+ Constants.CLIENTS_FOLDER + File.separator + id + File.separator + Constants.FILES_FOLDER);
		this.peerNetworkTopology = peerNetworkTopology;

		init();
		registerFiles();
		getNeighbors();
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
		return leafNodeAddress;
	}

	/**
	 * Sets the ip address.
	 *
	 * @param ipAddress            the ipAddress to set
	 */
	public void setIpAddress(String ipAddress) {
		this.leafNodeAddress = ipAddress;
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
	 * @return the tTL
	 */
	public long getTTL() {
		return TTL;
	}

	/**
	 * Inits the.
	 */
	private void init() {
/*		if (System.getSecurityManager() == null) {
			System.setSecurityManager(new SecurityManager());
		}*/
		try {
			loadConfig();
			
			System.setProperty(Constants.JAVA_RMI_SERVER_HOSTNAME, Constants.LOCALHOST);
			logger.info("[" + this.id + "] " + Constants.JAVA_RMI_SERVER_HOSTNAME + ":" + System.getProperty(Constants.JAVA_RMI_SERVER_HOSTNAME));
			
			IRemote stub = (IRemote) UnicastRemoteObject.exportObject(this, 0);
			Registry registry = LocateRegistry.getRegistry();
			registry.rebind(Constants.RMI_LOCALHOST + prop.getProperty(id).trim() + Constants.PEER_SERVER, stub);
			
			logger.info("[" + this.id + "] Available peers:");
			for (String boundName : registry.list()) {
	            logger.info("[" + this.id + "] " + boundName);
	        }
			
			logger.info("[" + this.id + "] " + "Server bound");
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
			is = new FileInputStream(new File(TOPOLOGY_FILE_PATH + File.separator + peerNetworkTopology + Constants.TXT));
			prop.load(is);
			this.leafNodeAddress = Constants.RMI_LOCALHOST + prop.getProperty(id).trim() + Constants.PEER_SERVER;
			this.TTL = prop.entrySet().stream().filter(key -> key.toString().startsWith(Constants.CLIENT_PREFIX)).count();
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
	 * Register files.
	 */
	public void registerFiles() {
		File[] filesArr = filesDirectory.listFiles();
		logger.info("[" + this.id + "] " + this.id + " Files Directory:" + filesDirectory);
		logger.info("[" + this.id + "] " + this.id + " Available Files:");
		for (int i = 0; i < filesArr.length; i++) {
			logger.info("[" + this.id + "] " + filesArr[i].getName());
			files.put(filesArr[i], filesArr[i]);
		}
		new Thread(new DirectoryWatcher(this)).start();
		logger.info("[" + this.id + "] " + this.id + " Files Count:" + files.size());
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
	/*public void query(MessageID messageID, String fileName) throws RemoteException {

		try {
			if (files.containsKey(new File(filesDirectory + File.separator + fileName))) {
				logger.info("[" + this.id + "] " + "Requested file " + fileName + " is already present on the requesting client");
			} else {
				logger.info("[" + this.id + "] " + "Requested file " + fileName + " is not present on the requesting client. Looking on neighbours.");
				if (!upstreamMap.containsKey(messageID) && TTL >= 0)
					upstreamMap.put(messageID, leafNodeAddress);
				query(messageID, this.TTL-1, fileName, leafNodeAddress);
			}
		} catch (Exception e) {
			logger.error("[" + this.id + "] " + "Server exception: Unable to query Server.");
			e.printStackTrace();
		}
	}*/

	/* (non-Javadoc)
	 * @see server.IRemote#query(model.MessageID, long, java.lang.String, java.lang.String)
	 */
	public void query(MessageID messageID, long TTL, String fileName, String upstreamIP) throws RemoteException {

		try {
			logger.info("[" + this.id + "] " + "file query: " + fileName + ", TTL:" + TTL + ", messageID:" + messageID );
			logger.info("[" + this.id + "] " + "query received from : " + upstreamIP);
			
			if (!upstreamMap.containsKey(messageID) && TTL >= 0) {
				upstreamMap.put(messageID, upstreamIP);
				logger.info("[" + this.id + "] " + "Upstream Address: " + upstreamIP);
			}

			if (TTL >= 0) {
				if (this.files.containsKey(new File(this.filesDirectory + File.separator + fileName))) {
					logger.info("[" + this.id + "] " + "Requested file: " + fileName + " found on " + this.id);
					queryHit(messageID, TTL - 1, fileName, this.leafNodeAddress);
				} else {
					logger.info("[" + this.id + "] " + "Requested file " + fileName + " is not present on the client. Looking on neighbours.");
					this.neighbours.forEach(neighbour -> {
						String upstreamIPAddress = Constants.RMI_LOCALHOST + neighbour.trim() + Constants.PEER_SERVER;
						Registry registry;
						try {
							registry = LocateRegistry.getRegistry();
							IRemote serverStub = (IRemote) registry.lookup(upstreamIPAddress);
							if(!serverStub.checkUpstreamMap(messageID)) {
								logger.info("[" + this.id + "] " + "Looking file: " + fileName + " on:" + upstreamIPAddress);	
								serverStub.query(messageID, TTL - 1, fileName, leafNodeAddress);
							} else {
								logger.info("[" + this.id + "] " + upstreamIPAddress + " already saw this message. Hence, skipping.");
							}
						} catch (Exception e) {
							logger.error("[" + this.id + "] " + "Server exception: Unable to query neighbours.");
							e.printStackTrace();
						}
					});
					/*for (String neighbour : this.neighbours) {
						String upstreamIPAddress = Constants.RMI_LOCALHOST + neighbour.trim() + Constants.PEER_SERVER;
						Registry registry = LocateRegistry.getRegistry();
						IRemote serverStub = (IRemote) registry.lookup(upstreamIPAddress);
						if(!serverStub.checkUpstreamMap(messageID)) {
							logger.info("[" + this.id + "] " + "Looking file: " + fileName + " on:" + upstreamIPAddress);	
							serverStub.query(messageID, TTL - 1, fileName, leafNodeAddress);
						} else {
							logger.info("[" + this.id + "] " + upstreamIPAddress + " already saw this message. Hence, skipping.");
						}
					}*/
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
			if (this.leafNodeAddress.equals(messageID.getLeafNodeId())) {
				logger.info("[" + this.id + "] " + this.id + ": receiving query for file:" + fileName);

				if (this.files.containsKey(new File(this.filesDirectory + File.separator + fileName))) {
					logger.info("[" + this.id + "] " + "Requested file " + fileName + " is already present on the requesting client");
				} else {
					logger.info("[" + this.id + "] " + "Requested file download from " + leafNodeIP);
					Registry registry = LocateRegistry.getRegistry();
					IRemote serverStub = (IRemote) registry.lookup(leafNodeIP);
					byte[] fileContent = serverStub.obtain(fileName);
					if(writeFileContent(fileContent, fileName))
						logger.info("[" + this.id + "] " + "Requested file " + fileName + " downloaded successfully.");
					else
						logger.info("[" + this.id + "] " + "Failed to download the requested file " + fileName + ".");
				}
			} else {
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
	public byte[] obtain(String fileName) throws IOException {
		byte[] fileContent = null;
		try {
			logger.info("[" + this.id + "] " + "Reading file content from " + this.filesDirectory + File.separator + fileName + " ...");
			fileContent = Files.readAllBytes(Paths.get(this.filesDirectory + File.separator + fileName));
			logger.info("[" + this.id + "] " + "Sent the file content from " + this.filesDirectory + File.separator + fileName + " ...");
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
		try {
			String client = prop.getProperty(id);
			String clientNeighbour = prop.getProperty(client);
			if (clientNeighbour != null) {
				neighbours = (Arrays.asList(clientNeighbour.split(Constants.SPLIT_REGEX)));
				logger.info("[" + this.id + "] " + id + "(Running on port:" + client + ") neighbours:");
				neighbours.forEach(x -> {
					String str = getPropertyFromValue(x);
					logger.info("[" + this.id + "] " + str + "(Port:" + x + ")");	
				});
			}
		} catch (Exception e) {
			logger.error("[" + this.id + "] " + "Exception: Unable to get neigbours: " + e.toString());
			e.printStackTrace();
		}
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
	
	private String getPropertyFromValue(String value) {
		return (String) prop.entrySet().stream()
				.filter(entry -> entry.getKey().toString().startsWith(Constants.CLIENT_PREFIX) && entry.getValue().toString().equals(value))
				.findAny()
				.map(Map.Entry::getKey).get();
	}
}
