package client;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.Serializable;
import java.rmi.RemoteException;
import java.util.Arrays;
import java.util.Map;
import java.util.Properties;
import java.util.Scanner;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import model.FileConsistencyState;
import model.MessageID;
import model.P2PFile;
import server.Server;
import util.Constants;
import util.DirectoryWatcher;

/**
 * The Class Client.
 *
 * @author Shantanoo
 */
public class Client implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 559133148017097116L;


	/** The Constant logger. */
	private static final Logger logger = LogManager.getLogger(Client.class);

	/** The id. */
	private String id;
	
	/** The server. */
	private Server server;
	
	/** The files dir path. */
	private String filesDirPath = null;
	
	private static long TTR = -1l;
	private static String pushOrPull = null;
	
	private File masterFilesDirectory;
	private File sharedFilesDirectory;
	
	private File USER_DIR = new File(System.getProperty(Constants.USER_DIR));
	
	/** The sequence number. */
	private Long sequenceNumber = 0l;
	
	private Map<File, P2PFile> masterFiles = new ConcurrentHashMap<File, P2PFile>();
	private Map<File, P2PFile> sharedFiles = new ConcurrentHashMap<File, P2PFile>();

	/**
	 * Instantiates a new client.
	 *
	 * @param id the id
	 * @param peerNetworkTopology the peer network topology
	 */
	public Client(String id, String peerNetworkTopology) {
		super();
		this.id = id;
		initServer(this, id, peerNetworkTopology, TTR);
		if (!server.isSuperPeer()) {
			this.masterFilesDirectory = new File(new File(new File(USER_DIR.getParent()).getParent()) + File.separator
					+ Constants.CLIENTS_FOLDER + File.separator + id + File.separator + Constants.FILES_FOLDER + File.separator + Constants.MASTER_FOLDER);
			this.sharedFilesDirectory = new File(new File(new File(USER_DIR.getParent()).getParent()) + File.separator
					+ Constants.CLIENTS_FOLDER + File.separator + id + File.separator + Constants.FILES_FOLDER + File.separator + Constants.SHARED_FOLDER);
		}
		registerFiles();
	}

	/**
	 * Inits the server.
	 *
	 * @param id the id
	 * @param peerNetworkTopology the peer network topology
	 */
	private void initServer(Client client, String id, String peerNetworkTopology, long TTR) {
		try {
			server = new Server(client, id, peerNetworkTopology, TTR);
		} catch (Exception e) {
			logger.error("Exception: Unable to initiate Server for client " + id + ":\n" + e.toString());
			e.printStackTrace();
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
	 * @param id the new id
	 */
	public void setId(String id) {
		this.id = id;
	}

	/**
	 * Gets the server.
	 *
	 * @return the server
	 */
	public Server getServer() {
		return server;
	}

	/**
	 * Sets the server.
	 *
	 * @param server the new server
	 */
	public void setServer(Server server) {
		this.server = server;
	}

	/**
	 * Gets the files dir path.
	 *
	 * @return the files dir path
	 */
	public String getFilesDirPath() {
		return filesDirPath;
	}

	/**
	 * Sets the files dir path.
	 *
	 * @param filesDirPath the new files dir path
	 */
	public void setFilesDirPath(String filesDirPath) {
		this.filesDirPath = filesDirPath;
	}
	
	/**
	 * Gets the masterFiles.
	 *
	 * @return the masterFiles
	 *//*
	public Map<File, Object> getFiles() {
		return masterFiles;
	}

	*//**
	 * Sets the masterFiles.
	 *
	 * @param masterFiles            the masterFiles to set
	 *//*
	public void setFiles(Map<File, Object> files) {
		this.masterFiles = files;
	}*/
	
	/**
	 * Gets the masterFiles directory.
	 *
	 * @return the masterFilesDirectory
	 *//*
	public File getFilesDirectory() {
		return masterFilesDirectory;
	}

	*//**
	 * Sets the masterFiles directory.
	 *
	 * @param masterFilesDirectory            the masterFilesDirectory to set
	 *//*
	public void setFilesDirectory(File filesDirectory) {
		this.masterFilesDirectory = filesDirectory;
	}*/
	
	
	/**
	 * @return the masterFilesDirectory
	 */
	public File getMasterFilesDirectory() {
		return masterFilesDirectory;
	}

	/**
	 * @param masterFilesDirectory the masterFilesDirectory to set
	 */
	public void setMasterFilesDirectory(File masterFilesDirectory) {
		this.masterFilesDirectory = masterFilesDirectory;
	}

	/**
	 * @return the sharedFilesDirectory
	 */
	public File getSharedFilesDirectory() {
		return sharedFilesDirectory;
	}

	/**
	 * @param sharedFilesDirectory the sharedFilesDirectory to set
	 */
	public void setSharedFilesDirectory(File sharedFilesDirectory) {
		this.sharedFilesDirectory = sharedFilesDirectory;
	}

	/**
	 * @return the uSER_DIR
	 */
	public File getUSER_DIR() {
		return USER_DIR;
	}

	/**
	 * @param uSER_DIR the uSER_DIR to set
	 */
	public void setUSER_DIR(File uSER_DIR) {
		USER_DIR = uSER_DIR;
	}

	/**
	 * @return the sequenceNumber
	 */
	public Long getSequenceNumber() {
		return sequenceNumber;
	}

	/**
	 * @param sequenceNumber the sequenceNumber to set
	 *//*
	public void setSequenceNumber(Long sequenceNumber) {
		this.sequenceNumber = sequenceNumber;
	}*/

	/**
	 * @return the masterFiles
	 */
	public Map<File, P2PFile> getMasterFiles() {
		return masterFiles;
	}

	public void addToMasterFiles(File file, P2PFile fileDetails) {
		this.masterFiles.put(file, fileDetails);
	}

	/**
	 * @return the sharedFiles
	 */
	public Map<File, P2PFile> getSharedFiles() {
		return sharedFiles;
	}

	public void addToSharedFiles(File file, P2PFile fileDetails) {
		this.sharedFiles.put(file, fileDetails);
	}

	/**
	 * Register masterFiles.
	 */
	public void registerFiles() {
		if (server.isSuperPeer())
			return;
		
		//loading masterFiles information
		File[] filesArr = getMasterFilesDirectory().listFiles();
		logger.info("*******************************************************************");
		logger.info("[" + this.id + "] " + this.id + " Files Directory:" + getMasterFilesDirectory());
		logger.info("[" + this.id + "] " + this.id + " Available Files:");
		for (int i = 0; i < filesArr.length; i++) {
			if(filesArr[i].isDirectory())
				continue;
			logger.info("[" + this.id + "] " + filesArr[i].getName());
			/*masterFiles.put(filesArr[i], filesArr[i]);*/
			addToMasterFiles(filesArr[i], new P2PFile(1, 100, this.server.getIpAddress(), null, filesArr[i], filesArr[i].getName(), FileConsistencyState.VALID));
		}
		new Thread(new DirectoryWatcher(this)).start();
		logger.info("[" + this.id + "] " + this.id + " Master Files Count:" + masterFiles.size());
		logger.info("[" + this.id + "] " + this.id + " Shared Files Count:" + sharedFiles.size());
		logger.info("*******************************************************************");
	}
	
	/*public void addMasterFile(String fileName) throws RemoteException {
		logger.info("[" + this.id + "] " + "Updating Server file...");
		addMasterFileToRegistry(fileName);
		logger.info("[" + this.id + "] " + "Updated Server masterFiles count:" + masterFiles.size());
	}
	
	public synchronized boolean addMasterFileToRegistry(String fileName) {
		addToMasterFiles(new File(masterFilesDirectory + File.separator + fileName));
		return true;
	}*/

	public void deleteMasterFile(String fileName) throws RemoteException {
		logger.info("[" + this.id + "] " + "Deleting Server file...");
		removeMasterFileFromRegistry(fileName);
		logger.info("[" + this.id + "] " + "Updated Server masterFiles count:" + masterFiles.size());
	}
	
	private synchronized boolean removeMasterFileFromRegistry(String fileName) {
		masterFiles.remove(new File(masterFilesDirectory + File.separator + fileName), new File(masterFilesDirectory + File.separator + fileName));
		return true;
	}
	
	/*public void addSharedFile(String fileName) throws RemoteException {
		logger.info("[" + this.id + "] " + "Updating Server file...");
		addSharedFileToRegistry(fileName);
		logger.info("[" + this.id + "] " + "Updated Server sharedFiles count:" + masterFiles.size());
	}*/
	
	public void deleteSharedFile(String fileName) throws RemoteException {
		logger.info("[" + this.id + "] " + "Deleting Server file...");
		removeSharedFileFromRegistry(fileName);
		logger.info("[" + this.id + "] " + "Updated Server masterFiles count:" + masterFiles.size());
	}
	
	public synchronized boolean addSharedFileToRegistry(String fileName, P2PFile p2PFile) {
		addToSharedFiles(getFileObj(sharedFilesDirectory, fileName), p2PFile);
		return true;
	}
	
	private File getFileObj(File dir, String fileName) {
		return new File(dir + File.separator + fileName);
	}
	
	private synchronized boolean removeSharedFileFromRegistry(String fileName) {
		sharedFiles.remove(new File(sharedFilesDirectory + File.separator + fileName), new File(sharedFilesDirectory + File.separator + fileName));
		return true;
	}
	/**
	 * Retrieve file.
	 *
	 * @param fileName the file name
	 */
	public void retrieveFile(String fileName){
		String leafNodeId = Constants.RMI_LOCALHOST + server.getProperty(id + ".port").trim() + Constants.PEER_SERVER;
		MessageID messageID = new MessageID(leafNodeId, sequenceNumber++);
		try {
        	//logger.info("[" + this.id + "] Querying file " + fileName + " from " + id);
        	server.query(messageID, fileName);
//        	server.query(messageID, server.getTTL(), fileName, server.getIpAddress());
        } catch (Exception e) {
        	logger.error("[" + this.id + "] Client exception: Unable to retrieve file.");
            logger.error("[" + this.id + "] Unable to query file " + fileName + " from " + leafNodeId + ":\n" + e.toString());
            e.printStackTrace();
        }
    }
	
	/**
	 * The main method.
	 *
	 * @param args the arguments
	 */
	public static void main(String[] args) {
		String topology = args[0];
		String id = args[1];
		
		pushOrPull = args[2];
		
		logger.info("*******************************************************************");
		logger.info("[" + id + "] " + "Initialized Topology:" + topology);
		if("Pull".equalsIgnoreCase(pushOrPull)) {
			TTR = args[3] == null ? -1 : Long.parseLong(args[3]);
		}
		logger.info("[" + id + "] " + "Push or Pull:" + pushOrPull + ", TTR:" + TTR);
		logger.info("*******************************************************************");
		
		Properties prop = new Properties();
		File USER_DIR = new File(System.getProperty(Constants.USER_DIR));
		File TOPOLOGY_FILE_PATH = new File(new File(new File(USER_DIR.getParent()).getParent()) + File.separator + Constants.TOPOLOGY_FOLDER);
		
		InputStream is = null;
		try {
			is = new FileInputStream(new File(TOPOLOGY_FILE_PATH + File.separator + topology + ".txt"));
			prop.load(is);
			/*prop.forEach((key, value) -> logger.info(key + ":" + value));
			prop.forEach((key, value) -> logger.info(key + ":" + prop.getProperty((String) key)));*/
		} catch (Exception e) {
			logger.error("[" + id + "] " + "Client exception: Unable to load topology.");
			e.printStackTrace();
		}
		
		Client client = new Client(id, topology);
		logger.info("[" + id + "] " + id + " initialized on port: " + prop.getProperty(id + ".port"));
		logger.info("*******************************************************************");

		if(client.server.isSuperPeer()) {
			while (true) {
				
			}
		} else {
			Scanner scanner = new Scanner(System.in);
			String input;
			logger.info("\nEnter 'exit' to exit the application"
					+ "\nEnter the name of file (with extension) you want to download:\n");
			
			while (true) {
				/*logger.info("\nEnter 'exit' to exit the application"
						+ "\nEnter the name of file (with extension) you want to download:\n");*/
				input = scanner.nextLine();
				if (input.equalsIgnoreCase("exit") || input.equalsIgnoreCase("e")) {
					logger.info("\nClient exiting...\n");
					scanner.close();
					System.exit(0);
				} else if (input != null && (input.trim().contains(Constants.SPACE) || !input.trim().contains(Constants.DOT))) {
					logger.info("Incorrect command");
					logger.info("USAGE: <file name with extension>");
					logger.info("EXAMPLE: <123.txt>");
					logger.info("EXAMPLE: <e or exit>");
				} else {
					long startTime = System.currentTimeMillis();
					if(input.contains(";")) {
						logger.info("[" + id + "] " + "Received multi-file requests");
						logger.info("[" + id + "] " + "Sending file request one-by-one");
						Arrays.asList(input.split(";")).stream().filter(x -> !x.isEmpty()).forEach(fileName -> {
							logger.info("[" + id + "] " + "Requesting file: " + fileName);
							client.retrieveFile(fileName);
						});
					} else {
						client.retrieveFile(input);
					}
					long endTime = System.currentTimeMillis();
					long elapsedTime = endTime - startTime;
					double elapsedTimeInMSecond = (double) elapsedTime / 1000.000;
					logger.info("Process completed in " + TimeUnit.SECONDS.convert(elapsedTime, TimeUnit.MILLISECONDS) + "(" + elapsedTimeInMSecond + ") second");
				}
				logger.info("\nEnter 'exit' to exit the application"
						+ "\nEnter the name of file (with extension) you want to download:\n");
			}
		}
	}

	public void modifyMasterFile(String fileName) {
		P2PFile p2pFile = this.masterFiles.get(getFileObj(masterFilesDirectory, fileName));
		p2pFile.incrementVersion();
		String leafNodeId = Constants.RMI_LOCALHOST + server.getProperty(id + ".port").trim() + Constants.PEER_SERVER;
		MessageID messageID = new MessageID(leafNodeId, sequenceNumber++);
		try {
			if("Push".equalsIgnoreCase(pushOrPull))
				server.invalidate(messageID, p2pFile, this.server.getIpAddress());
		} catch (RemoteException e) {
			e.printStackTrace();
		}
	}
	
	/*public void simulateUpdate(){
        
                ConsistentFile cf = fileMap.get(fileName);
                cf.setVersion(cf.getVersion() +  1);
                if(mode.equals("push")) {
                    invalidateNeighbors(new Pair(fileName, cf.getVersion()));
                    Collections.shuffle(originIndex);
                    fileName = originIndex.get(0);
                    System.out.println(fileName + " has been updated");
                    int delay = (int)nextExponentialDelay(5.0 * 1000.0);
                    System.out.println("Next pseudoupdate will be in " + delay + " milliseconds");
                    updateTimer = new Timer(delay, this);
                    updateTimer.setRepeats(false);
                    updateTimer.start();
        
    }*/
}
