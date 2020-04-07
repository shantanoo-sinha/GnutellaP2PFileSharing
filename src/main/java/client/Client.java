package client;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.attribute.FileTime;
import java.rmi.RemoteException;
import java.util.Arrays;
import java.util.Map;
import java.util.Properties;
import java.util.Random;
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
	
	/*private Map<File, P2PFile> masterFiles = new ConcurrentHashMap<File, P2PFile>();
	private Map<File, P2PFile> sharedFiles = new ConcurrentHashMap<File, P2PFile>();*/
	private Map<String, P2PFile> masterFiles = new ConcurrentHashMap<String, P2PFile>();
	private Map<String, P2PFile> sharedFiles = new ConcurrentHashMap<String, P2PFile>();
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
	public Map</*File*/String, P2PFile> getMasterFiles() {
		return masterFiles;
	}

	public void addToMasterFiles(/*File file*/String file, P2PFile fileDetails) {
		this.masterFiles.put(file, fileDetails);
	}

	/**
	 * @return the sharedFiles
	 */
	public Map</*File*/String, P2PFile> getSharedFiles() {
		return sharedFiles;
	}

	public void addToSharedFiles(/*File */String file, P2PFile fileDetails) {
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
//			addToMasterFiles(filesArr[i], new P2PFile(1, 100, this.server.getIpAddress(), null, filesArr[i], filesArr[i].getName(), FileConsistencyState.VALID));
			addToMasterFiles(filesArr[i].getName(), new P2PFile(1, 100, this.server.getIpAddress(), this.server.getSuperPeer(), this.server.getIpAddress(), null, filesArr[i], filesArr[i].getName(), FileConsistencyState.VALID));
		}
		new Thread(new DirectoryWatcher(this)).start();
		logger.info("[" + this.id + "] " + this.id + " Master Files Count:" + masterFiles.size());
		logger.info("[" + this.id + "] " + this.id + " Shared Files Count:" + sharedFiles.size());
		logger.info("*******************************************************************");
		
		logger.info("[" + this.id + "] Registering master files to Super Peer");
		server.registerMasterFilesToSuperPeer();
		logger.info("[" + this.id + "] Master files registered to Super Peer");
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
		addToSharedFiles(/*getFileObj(sharedFilesDirectory, fileName)*/fileName, p2PFile);
		return true;
	}
	
	/*private File getFileObj(File dir, String fileName) {
		return new File(dir + File.separator + fileName);
	}*/
	
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
        	server.query(messageID, fileName);
        } catch (Exception e) {
        	logger.error("[" + this.id + "] Client exception: Unable to retrieve file.");
            logger.error("[" + this.id + "] Unable to query file " + fileName + " from " + leafNodeId + ":\n" + e.toString());
            e.printStackTrace();
        }
    }
	
	private void refreshFile(String fileName) {
		try {
			if(getSharedFiles().containsKey(fileName))
				server.refresh(getSharedFiles().get(fileName));
			else
				logger.info("[" + this.id + "] Requested file is not present in shared list. Unable to refresh file.");
        } catch (Exception e) {
        	logger.error("[" + this.id + "] Client exception: Unable to refresh file.");
            logger.error("[" + this.id + "] Unable to refresh file " + fileName + " from " + getSharedFiles().get(fileName).getOriginServerID() + ":\n" + e.toString());
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
				for(Map.Entry<String, Map</*File*/String, P2PFile>> entry : client.server.getLeafNodeMasterFiles().entrySet()) {
					logger.info("[" + id + "] " + "leaf node: " + entry.getKey() + ", Master files size" + entry.getValue().size());
				}
				for(Map.Entry<String, Map</*File*/String, P2PFile>> entry : client.server.getLeafNodeSharedFiles().entrySet()) {
					logger.info("[" + id + "] " + "leaf node: " + entry.getKey() + ", Shared files size" + entry.getValue().size());
					for(Map.Entry<String, P2PFile> val : entry.getValue().entrySet()) {
						logger.info("[" + id + "] " + val.getValue().getFileName() + ", Version: " + val.getValue().getVersion() + ", Origin: " + val.getValue().getOriginServerID()
						+ ", Origin Super Peer: " + val.getValue().getOriginServerSuperPeerID() + "State: " + val.getValue().getState());
					}
				}
				try {
					Thread.sleep(100000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		} else {
			Scanner scanner = new Scanner(System.in);
			String input;
			logger.info("\nEnter 'exit' to exit the application"
					+ "\nEnter 'print' to print the files with version and state information"
					+ "\nEnter file name followed by 'refresh' keyword to re-download the existing file"
					+ "\nEnter the name of file (with extension) you want to download:\n");
			
			while (true) {
				/*logger.info("\nEnter 'exit' to exit the application"
						+ "\nEnter the name of file (with extension) you want to download:\n");*/
				input = scanner.nextLine();
				if (input.equalsIgnoreCase("exit") || input.equalsIgnoreCase("e")) {
					logger.info("\nClient exiting...\n");
					scanner.close();
					System.exit(0);
				} else if (input.contains("refresh")) {
					long startTime = System.currentTimeMillis();
					client.refreshFile(input.substring(0, (input.length()-("refresh".length()))).trim());
					long endTime = System.currentTimeMillis();
					long elapsedTime = endTime - startTime;
					double elapsedTimeInMSecond = (double) elapsedTime / 1000.000;
					logger.info("Process completed in " + TimeUnit.SECONDS.convert(elapsedTime, TimeUnit.MILLISECONDS) + "(" + elapsedTimeInMSecond + ") second");
				} else if (input.startsWith("update")) {
					String fileName = null;
					if(input.split(" ").length>1)
						fileName = input.split(" ")[1];
					long startTime = System.currentTimeMillis();
					client.simulateUpdate(fileName);
					long endTime = System.currentTimeMillis();
					long elapsedTime = endTime - startTime;
					double elapsedTimeInMSecond = (double) elapsedTime / 1000.000;
					logger.info("Process completed in " + TimeUnit.SECONDS.convert(elapsedTime, TimeUnit.MILLISECONDS) + "(" + elapsedTimeInMSecond + ") second");
				} else if (input.equalsIgnoreCase("print")) {
					logger.info("[" + id + "] " + "Printing file information:");
					logger.info("[" + id + "] " + "Master Files:");
					client.masterFiles.forEach((key, val) -> {
						logger.info("[" + id + "] " + val.getFileName() + ", Version: " + val.getVersion());
					});
					logger.info("[" + id + "] " + "Shared Files:");
					client.sharedFiles.forEach((key, val) -> {
						logger.info("[" + id + "] " + val.getFileName() + ", Version: " + val.getVersion() + ", Origin: " + val.getOriginServerID()
						+ ", Origin Super Peer: " + val.getOriginServerSuperPeerID() + "State: " + val.getState());
					});
				} else if (input != null && (input.trim().contains(Constants.SPACE) || !input.trim().contains(Constants.DOT))) {
					logger.info("Incorrect command");
					logger.info("USAGE: <file name with extension>");
					logger.info("EXAMPLE: <123.txt>");
					logger.info("EXAMPLE: <e or exit>");
					logger.info("EXAMPLE: <print>");
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
				logger.info("Master File Count" + client.masterFiles.size());
				logger.info("Shared File Count" + client.sharedFiles.size());
				logger.info("\nEnter 'exit' to exit the application"
						+ "\nEnter the name of file (with extension) you want to download:\n");
			}
		}
	}

	public void modifyMasterFile(String fileName) {
		P2PFile p2pFile = this.masterFiles.get(fileName);
		p2pFile.incrementVersion();
		try {
			server.registerMasterFilesToSuperPeer();
			
			if("Push".equalsIgnoreCase(pushOrPull)) {
				String leafNodeId = Constants.RMI_LOCALHOST + server.getProperty(id + ".port").trim() + Constants.PEER_SERVER;
				MessageID messageID = new MessageID(leafNodeId, sequenceNumber++);
				
				server.invalidate(messageID, p2pFile, this.server.getIpAddress());
			}
		} catch (RemoteException e) {
			logger.error("[" + id + "] " + "Client exception: Unable to modify master file.");
			e.printStackTrace();
		}
	}
	
	public void simulateUpdate(String fileName){
		try {
			if(fileName == null) {
				Object[] fileNames = masterFiles.keySet().toArray();
				Object key = fileNames[new Random().nextInt(fileNames.length)];
				logger.info("[" + id + "] " + "************ Updating a random Master File ************ \n" + key + " :: " + masterFiles.get(key).getFile());
				
				if(masterFiles.get(key).getFile().exists() && masterFiles.get(key).getFile().isFile()) {
					logger.info("[" + id + "] " + "File " + key + " last modified time:" + masterFiles.get(key).getFile().lastModified());
					Files.setLastModifiedTime(masterFiles.get(key).getFile().toPath(), FileTime.fromMillis(System.currentTimeMillis()));
				}
				logger.info("[" + id + "] " + "File " + key + " last modified time updated to:" + masterFiles.get(key).getFile().lastModified());
					
			} else {
				if(masterFiles.containsKey(fileName) && masterFiles.get(fileName).getFile().exists() && masterFiles.get(fileName).getFile().isFile()) {
					logger.info("[" + id + "] " + "************ Updating Master File ************ \n" + fileName);
					logger.info("[" + id + "] " + "File " + fileName + " last modified time:" + masterFiles.get(fileName).getFile().lastModified());
					Files.setLastModifiedTime(masterFiles.get(fileName).getFile().toPath(), FileTime.fromMillis(System.currentTimeMillis()));
					logger.info("[" + id + "] " + "File " + fileName + " last modified time updated to:" + masterFiles.get(fileName).getFile().lastModified());
				} else {
					logger.info("[" + id + "] File does not exist");
				}
			}
		} catch (IOException e) {
			logger.error("[" + id + "] " + "Client exception: Unable to simulate master file update.");
			e.printStackTrace();
		}
		
    }
}
